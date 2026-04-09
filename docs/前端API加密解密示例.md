# 前端 API 加密/解密示例

## 加密协议说明

- 响应加密：服务端用 RSA 私钥加密 AES 密钥 → 前端用公钥解密
- 请求加密：前端用 RSA 公钥加密 AES 密钥 → 服务端用私钥解密
- 数据格式：`encryptedAesKey.iv.encryptedData`（Base64）
- RSA 算法：RSA/ECB/PKCS1Padding
- AES 算法：AES-GCM (256-bit key, 12-byte IV, 128-bit tag)

## RSA 公钥（硬编码到前端）

```
-----BEGIN PUBLIC KEY-----
MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDBu1lMcN6zG6Mn9buXfdmHIlFx
jinQ5TS5pEt/HkWFyuN06HdboTizsq9Xh8X824aiholqPmz6dW9e4f/exn5nMEa/
SQQnBL1BihNaS00g5Qz8mLkm7Us4Ld+WvAA4HRqotJ5aK9eC2ICTgNmHN2r8EPBC
tWVKCrYwlM6P3Yd0FQIDAQAB
-----END PUBLIC KEY-----
```

## 前端代码示例（使用 node-forge）

### 安装依赖

```bash
npm install node-forge
```

### crypto.js

```javascript
import forge from 'node-forge';

// 硬编码的 RSA 公钥
const PUBLIC_KEY_PEM = `-----BEGIN PUBLIC KEY-----
MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDBu1lMcN6zG6Mn9buXfdmHIlFx
jinQ5TS5pEt/HkWFyuN06HdboTizsq9Xh8X824aiholqPmz6dW9e4f/exn5nMEa/
SQQnBL1BihNaS00g5Qz8mLkm7Us4Ld+WvAA4HRqotJ5aK9eC2ICTgNmHN2r8EPBC
tWVKCrYwlM6P3Yd0FQIDAQAB
-----END PUBLIC KEY-----`;

// 解析公钥
const publicKey = forge.pki.publicKeyFromPem(PUBLIC_KEY_PEM);

/**
 * 解密响应（服务端私钥加密 → 前端公钥解密）
 */
function decryptResponse(encryptedText) {
  const parts = encryptedText.split('.');
  if (parts.length !== 3) throw new Error('加密数据格式错误');

  // 1. Base64 解码
  const encryptedAesKey = forge.util.decode64(parts[0]);
  const iv = forge.util.decode64(parts[1]);
  const encryptedData = forge.util.decode64(parts[2]);

  // 2. 使用 RSA 公钥解密 AES 密钥
  // 注意：这里是"公钥解密私钥加密的数据"，需要用 raw RSA
  const n = publicKey.n;
  const e = publicKey.e;
  const encryptedBigInt = new forge.jsbn.BigInteger(forge.util.bytesToHex(encryptedAesKey), 16);
  const decryptedBigInt = encryptedBigInt.modPow(e, n);
  let decryptedHex = decryptedBigInt.toString(16);
  // 补齐前导零
  while (decryptedHex.length < 512) decryptedHex = '0' + decryptedHex;
  const decryptedBytes = forge.util.hexToBytes(decryptedHex);
  
  // 去除 PKCS1 填充
  const aesKeyBytes = removePkcs1Padding(decryptedBytes);

  // 3. 使用 AES-GCM 解密数据
  const decipher = forge.cipher.createDecipher('AES-GCM', aesKeyBytes);
  decipher.start({ 
    iv: iv, 
    tagLength: 128,
    tag: forge.util.createBuffer(encryptedData.slice(-16))
  });
  decipher.update(forge.util.createBuffer(encryptedData.slice(0, -16)));
  const success = decipher.finish();
  
  if (!success) throw new Error('AES 解密失败');
  return decipher.output.toString('utf8');
}

/**
 * 去除 PKCS1 v1.5 填充
 */
function removePkcs1Padding(data) {
  let i = 0;
  if (data.charCodeAt(i++) !== 0x00) throw new Error('Invalid padding');
  if (data.charCodeAt(i++) !== 0x01) throw new Error('Invalid padding'); // 私钥加密用 0x01
  while (data.charCodeAt(i) === 0xff) i++;
  if (data.charCodeAt(i++) !== 0x00) throw new Error('Invalid padding');
  return data.substring(i);
}

/**
 * 加密请求（前端公钥加密 → 服务端私钥解密）
 */
function encryptRequest(plainText) {
  // 1. 生成随机 AES 密钥 (32 bytes = 256 bits)
  const aesKey = forge.random.getBytesSync(32);
  
  // 2. 生成随机 IV (12 bytes for GCM)
  const iv = forge.random.getBytesSync(12);

  // 3. 使用 AES-GCM 加密数据
  const cipher = forge.cipher.createCipher('AES-GCM', aesKey);
  cipher.start({ iv: iv, tagLength: 128 });
  cipher.update(forge.util.createBuffer(plainText, 'utf8'));
  cipher.finish();
  const encryptedData = cipher.output.getBytes() + cipher.mode.tag.getBytes();

  // 4. 使用 RSA 公钥加密 AES 密钥
  const encryptedAesKey = publicKey.encrypt(aesKey, 'RSAES-PKCS1-V1_5');

  // 5. 组合结果
  return [
    forge.util.encode64(encryptedAesKey),
    forge.util.encode64(iv),
    forge.util.encode64(encryptedData)
  ].join('.');
}

export { decryptResponse, encryptRequest };
```

## 使用示例

```javascript
import { decryptResponse, encryptRequest } from './crypto.js';

// 发送加密请求
async function login(username, password) {
  const requestBody = JSON.stringify({ username, password });
  const encryptedBody = encryptRequest(requestBody);

  const response = await fetch('/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'text/plain',
      'X-Encrypted': 'true'
    },
    body: encryptedBody
  });

  const responseText = await response.text();
  
  // 检查响应头判断是否加密
  if (response.headers.get('X-Encrypted') === 'true') {
    const decrypted = decryptResponse(responseText);
    return JSON.parse(decrypted);
  }
  
  return JSON.parse(responseText);
}

// 普通 GET 请求（响应会被加密）
async function getSystemInfo() {
  const response = await fetch('/system/info');
  const responseText = await response.text();
  
  if (response.headers.get('X-Encrypted') === 'true') {
    const decrypted = decryptResponse(responseText);
    return JSON.parse(decrypted);
  }
  
  return JSON.parse(responseText);
}
```

## Axios 拦截器封装

```javascript
import axios from 'axios';
import { decryptResponse, encryptRequest } from './crypto.js';

const api = axios.create({ baseURL: '/api' });

// 请求拦截器：加密请求体
api.interceptors.request.use(config => {
  if (config.data && config.headers['X-Encrypted'] === 'true') {
    config.data = encryptRequest(JSON.stringify(config.data));
    config.headers['Content-Type'] = 'text/plain';
  }
  return config;
});

// 响应拦截器：解密响应体
api.interceptors.response.use(response => {
  if (response.headers['x-encrypted'] === 'true') {
    response.data = JSON.parse(decryptResponse(response.data));
  }
  return response;
});

export default api;
```

## 注意事项

1. 公钥可以安全地硬编码到前端，私钥必须保密在服务端
2. 如果更换密钥对，需要同时更新前端和后端
3. 建议生产环境使用 HTTPS + API 加密双重保护
4. 不需要加密的请求可以带请求头 `X-No-Encrypt: true` 跳过响应加密
