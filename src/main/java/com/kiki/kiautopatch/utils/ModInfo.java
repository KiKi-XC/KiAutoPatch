package com.kiki.kiautopatch.utils;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.util.Optional;

public class ModInfo {
    public static String getModVersion(String modId) {
        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(modId);
        return modContainer.map(container -> container.getMetadata().getVersion().getFriendlyString())
                .orElse("unknown");
    }
}