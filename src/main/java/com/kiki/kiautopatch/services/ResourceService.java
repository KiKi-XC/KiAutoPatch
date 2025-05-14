package com.kiki.kiautopatch.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kiki.kiautopatch.KiAutoPatch;
import com.kiki.kiautopatch.utils.HttpUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResourceService {
    private static final String apiUrl = KiAutoPatch.config.apiUrl;
    private static final String resourcePackName = KiAutoPatch.config.resourcePackName;
    private static final Pattern versionPattern = Pattern.compile(resourcePackName + "(\\d+(?:\\.\\d+)*)\\.zip");
    private static boolean hasChecked = false;

    /** 检查并更新资源包（仅执行一次），通过 Toast 显示结果 */
    public static void checkAndUpdate(MinecraftClient client) {
        if (hasChecked) return;
        hasChecked = true;

        try {
            String json = HttpUtil.getString(apiUrl);
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            String remoteVer = obj.get("version").getAsString();
            String downloadUrl = obj.get("download_url").getAsString();

            Path packDir = FabricLoader.getInstance()
                    .getGameDir().resolve("resourcepacks");

            Optional<String> localVer = Optional.empty();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(packDir, resourcePackName + "*.zip")) {
                for (Path p : stream) {
                    Matcher m = versionPattern.matcher(p.getFileName().toString());
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

            if (updated) {
                showToast(client, "[KiAutoPatch] 已将 [NeoTccResourcepack] 更新至 " + resultVer, true);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            showToast(client, "[KiAutoPatch] 更新失败,错误码: -1", false);
        }
    }

    private static void showToast(MinecraftClient client, String message, boolean success) {
        ToastManager tm = client.getToastManager();
        tm.add(SystemToast.create(
                client,
                SystemToast.Type.PERIODIC_NOTIFICATION,
                Text.of(message),
                Text.empty()
        ));
    }

    private static void downloadNew(String url, String version, Path dir) throws IOException, InterruptedException {
        Path target = dir.resolve(resourcePackName + version + ".zip");
        HttpUtil.downloadTo(url, target);
    }

    private static void deleteLocalPacks(Path dir) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, resourcePackName + "*.zip")) {
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
