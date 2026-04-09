package com.junoyi.framework.plugin.core;

import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.sdk.log.JunoYiLogger;

import java.util.Objects;

/**
 * 基于 JunoYiLog 的插件日志适配器。
 */
public class JunoYiJunoYiLoggerAdapter implements JunoYiLogger {

    private final JunoYiLog logger;

    public JunoYiJunoYiLoggerAdapter(JunoYiLog logger) {
        this.logger = Objects.requireNonNull(logger, "logger must not be null");
    }

    @Override
    public void debug(String message) {
        logger.debug(message);
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void warn(String message) {
        logger.warn(message);
    }

    @Override
    public void error(String message) {
        logger.error(message);
    }

    @Override
    public void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }
}

