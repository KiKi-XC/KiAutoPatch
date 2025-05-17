package com.kiki.kiautopatch.services;

import com.google.gson.*;
import com.kiki.kiautopatch.KiAutoPatch;
import com.kiki.kiautopatch.utils.HttpUtil;
import com.kiki.kiautopatch.utils.KiAutoPatchLogger;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;

import java.io.IOException;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResourceService {
    private static final String apiUrl = KiAutoPatch.config.apiUrl;

    private static boolean hasChecked = false;

    /** 入口：检查并更新所有资源包 */
    public static void checkAndUpdate(MinecraftClient client) {
        if (hasChecked) return;
        hasChecked = true;

        Path packDir = FabricLoader.getInstance().getGameDir().resolve("resourcepacks");
        if (KiAutoPatch.config.eulaAccepted  ){

        }
        try {
            KiAutoPatchLogger.info("[KiAutoPatch] 开始检查更新");
            String json = HttpUtil.getString(apiUrl);
            JsonObject rootObj = JsonParser.parseString(json).getAsJsonObject();

            if (!"OK".equals(rootObj.get("status").getAsString())) {
                KiAutoPatchLogger.warn("[KiAutoPatch] API 状态异常");
                showToast(client, "[KiAutoPatch] API 状态异常", false);
                return;
            }

            JsonArray dataArray = rootObj.getAsJsonArray("data");
            if (dataArray == null || dataArray.isEmpty()) {
                KiAutoPatchLogger.warn("[KiAutoPatch] API 无资源包数据");
                showToast(client, "[KiAutoPatch] API 无资源包数据", false);
                return;
            }

            // 遍历每个资源包信息，逐个检查更新
            for (JsonElement el : dataArray) {
                JsonObject obj = el.getAsJsonObject();

                String packName = obj.get("pack_name").getAsString();
                String remoteVer = obj.get("version").getAsString();
                String downloadUrl = obj.get("download_url").getAsString();
                String md5Expected = obj.has("md5") ? obj.get("md5").getAsString() : null;

                // 版本匹配正则，注意转义 packName
                Pattern versionPattern = Pattern.compile(Pattern.quote(packName) + "(\\d+(?:\\.\\d+)*)\\.zip");

                Optional<String> localVer = Optional.empty();
                try (DirectoryStream<Path> ds = Files.newDirectoryStream(packDir, packName + "*.zip")) {
                    for (Path p : ds) {
                        Matcher m = versionPattern.matcher(p.getFileName().toString());
                        if (m.matches()) {
                            String v = m.group(1);
                            localVer = localVer.isEmpty() ? Optional.of(v) : Optional.of(maxVersion(localVer.get(), v));
                        }
                    }
                }

                boolean updated = false;
                if (localVer.isEmpty()) {
                    // 本地无此资源包，直接下载
                    KiAutoPatchLogger.info("[KiAutoPatch] 本地无资源包数据，开始下载");
                    downloadNew(downloadUrl, packName, remoteVer, packDir);
                    updated = true;
                } else {
                    String current = localVer.get();
                    if (compareVersion(remoteVer, current) > 0) {
                        // 远程版本更新，删除旧包并下载
                        KiAutoPatchLogger.info("[KiAutoPatch] 本地已有资源包数据，开始替换");
                        deleteLocalPacks(packDir, packName);
                        downloadNew(downloadUrl, packName, remoteVer, packDir);
                        updated = true;
                    }
                }

                // MD5 校验
                if (updated && md5Expected != null) {
                    Path downloaded = packDir.resolve(packName + remoteVer + ".zip");
                    if (!checkMD5(downloaded, md5Expected)) {
                        throw new IOException("MD5 校验失败: " + downloaded.getFileName());
                    }
                }

                if (updated) {
                    showToast(client, "[KiAutoPatch] 已将 [" + packName + "] 更新至 " + remoteVer, true);
                    KiAutoPatchLogger.info("[KiAutoPatch] 已将 [" + packName + "] 更新至 " + remoteVer);
                }
            }

        } catch (Exception e) {
            KiAutoPatchLogger.error("[KiAutoPatch] 更新失败", e);
            showToast(client, "[KiAutoPatch] 更新失败，请查看日志或询问管理员", false);
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

    private static void downloadNew(String url, String packName, String version, Path dir) throws IOException, InterruptedException {
        Path target = dir.resolve(packName + version + ".zip");
        HttpUtil.downloadTo(url, target);
    }

    private static void deleteLocalPacks(Path dir, String packName) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, packName + "*.zip")) {
            for (Path p : stream) {
                Files.deleteIfExists(p);
            }
        }
    }

    private static String maxVersion(String v1, String v2) {
        return compareVersion(v1, v2) >= 0 ? v1 : v2;
    }

    private static int compareVersion(String v1, String v2) {
        String[] a1 = v1.split("\\.");
        String[] a2 = v2.split("\\.");
        for (int i = 0, len = Math.max(a1.length, a2.length); i < len; i++) {
            int n1 = i < a1.length ? Integer.parseInt(a1[i]) : 0;
            int n2 = i < a2.length ? Integer.parseInt(a2[i]) : 0;
            if (n1 != n2) return n1 - n2;
        }
        return 0;
    }

    private static boolean checkMD5(Path file, String expectedMd5) throws IOException {
        try {
            byte[] fileBytes = Files.readAllBytes(file);
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(fileBytes);
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString().equalsIgnoreCase(expectedMd5);
        } catch (Exception e) {
            KiAutoPatchLogger.error("[KiAutoPatch] MD5校验失败", e);
            return false;
        }
    }
}
