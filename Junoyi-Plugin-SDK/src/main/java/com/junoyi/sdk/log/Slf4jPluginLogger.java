package com.junoyi.sdk.log;

import org.slf4j.Logger;

import java.util.Objects;

/**
 * 基于 SLF4J 的日志实现。
 */
public class Slf4jPluginLogger implements PluginLogger {

    private final Logger logger;

    public Slf4jPluginLogger(Logger logger) {
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

