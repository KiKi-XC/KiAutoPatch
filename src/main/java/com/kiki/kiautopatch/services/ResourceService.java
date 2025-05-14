package com.kiki.kiautopatch.services;

import com.kiki.kiautopatch.utils.HttpUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;

import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResourceService {
    private static final String API_URL = "http://127.0.0.1:8000/api/resources/mc/resource_pack";
    private static final String PACK_PREFIX = "NeoTccResourcepack-";
    private static final Pattern VERSION_PATTERN = Pattern.compile(PACK_PREFIX + "(\\d+(?:\\.\\d+)*)\\.zip");
    private static boolean hasChecked = false;

    /** 检查并更新资源包（仅执行一次），通过 Toast 显示结果 */
    public static void checkAndUpdate(MinecraftClient client) {
        if (hasChecked) return;
        hasChecked = true;

        try {
            String json = HttpUtil.getString(API_URL);
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            String remoteVer = obj.get("version").getAsString();
            String downloadUrl = obj.get("download_url").getAsString();

            Path packDir = FabricLoader.getInstance()
                    .getGameDir().resolve("resourcepacks");

            Optional<String> localVer = Optional.empty();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(packDir, PACK_PREFIX + "*.zip")) {
                for (Path p : stream) {
                    Matcher m = VERSION_PATTERN.matcher(p.getFileName().toString());
                    if (m.matches()) {
                        String v = m.group(1);
                        localVer = localVer.isEmpty() ? Optional.of(v) : Optional.of(maxVersion(localVer.get(), v));
                    }
                }
            }

            boolean updated = false;
            String resultVer = remoteVer;
            if (localVer.isEmpty()) {
                downloadNew(downloadUrl, remoteVer, packDir);
                updated = true;
            } else {
                String current = localVer.get();
                if (compareVersion(remoteVer, current) > 0) {
                    deleteLocalPacks(packDir);
                    downloadNew(downloadUrl, remoteVer, packDir);
                    updated = true;
                } else {
                    resultVer = current;
                }
            }

            showToast(client, updated
                            ? "[KiAutoPatch] 已更新至 " + resultVer
                            : "[KiAutoPatch] 已是最新版本：" + resultVer,
                    updated);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            showToast(client, "[KiAutoPatch] 更新失败，错误码: -1", false);
        }
    }

    private static void showToast(MinecraftClient client, String message, boolean success) {
        ToastManager tm = client.getToastManager();
        tm.add(SystemToast.create(
                client,
                success ? SystemToast.Type.NARRATOR_TOGGLE : SystemToast.Type.WORLD_BACKUP,
                Text.of(message),
                Text.empty()
        ));
    }

    private static void downloadNew(String url, String version, Path dir) throws IOException, InterruptedException {
        Path target = dir.resolve(PACK_PREFIX + version + ".zip");
        HttpUtil.downloadTo(url, target);
        System.out.println("[KiAutoPatch] 已下载资源包版本 " + version);
    }

    private static void deleteLocalPacks(Path dir) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, PACK_PREFIX + "*.zip")) {
            for (Path p : stream) Files.deleteIfExists(p);
        }
    }

    private static String maxVersion(String v1, String v2) {
        return compareVersion(v1, v2) >= 0 ? v1 : v2;
    }

    private static int compareVersion(String v1, String v2) {
        String[] a1 = v1.split("\\."), a2 = v2.split("\\.");
        for (int i = 0, len = Math.max(a1.length, a2.length); i < len; i++) {
            int n1 = i < a1.length ? Integer.parseInt(a1[i]) : 0;
            int n2 = i < a2.length ? Integer.parseInt(a2[i]) : 0;
            if (n1 != n2) return n1 - n2;
        }
        return 0;
    }
}
