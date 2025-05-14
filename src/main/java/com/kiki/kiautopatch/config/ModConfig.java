package com.kiki.kiautopatch.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfig {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("kiautopatch.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public String apiUrl = "http://127.0.0.1:8000/api/resources/mc/resource_pack";
    public String resourcePackName = "NeoTccResourcepack";

    public static ModConfig load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                return GSON.fromJson(reader, ModConfig.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 配置文件不存在，写入默认配置
        ModConfig defaultConfig = new ModConfig();
        try {
            Files.createDirectories(CONFIG_PATH.getParent()); // 确保目录存在
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(defaultConfig, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return defaultConfig;
    }

    public void save() {
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
