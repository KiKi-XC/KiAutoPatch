package com.kiki.kiautopatch.utils;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.*;

public class KiAutoPatchLogger {
    private static final Logger LOGGER = Logger.getLogger("KiAutoPatch");
    static {
        try {
            // 构造日志目录：<gameDir>/logs/kiautopatch
            Path logDir = FabricLoader.getInstance()
                    .getGameDir()
                    .resolve("logs")
                    .resolve("kiautopatch");
            if (!Files.exists(logDir)) {
                Files.createDirectories(logDir);
            }

            // 日志文件模式
            String pattern = logDir.resolve("kiautopatch-%g.log").toString();
            // 限制单文件 10MB，最多 5 个轮转文件，append 模式
            FileHandler fh = new FileHandler(pattern, 10 * 1024 * 1024, 5, true);
            fh.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fh);

            // 不往父 Logger （控制台）重复输出
            LOGGER.setUseParentHandlers(false);
            LOGGER.setLevel(Level.ALL);
        } catch (IOException e) {
            // 如果文件系统出错，退回到控制台输出
            e.printStackTrace();
        }
    }

    private KiAutoPatchLogger() { /* no instantiation */ }

    public static void info(String msg) {
        LOGGER.log(Level.INFO, msg);
    }

    public static void warn(String msg) {
        LOGGER.log(Level.WARNING, msg);
    }

    public static void error(String msg, Throwable t) {
        LOGGER.log(Level.SEVERE, msg, t);
    }
}
