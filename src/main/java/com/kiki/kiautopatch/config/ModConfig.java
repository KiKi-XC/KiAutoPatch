package com.kiki.kiautopatch.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "kiautopatch")
public class ModConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    public UrlOption urlOption = UrlOption.MAIN;

    @ConfigEntry.Gui.Tooltip
    public boolean eulaAccepted = false;

    @ConfigEntry.Gui.Tooltip
    public boolean autoUpdate = true;

    public enum UrlOption {
        MAIN,
        BACKUP
    }

    public String getResolvedUrl() {
        return switch (urlOption) {
            case MAIN   -> "http://stub.kiki-items.cn/api/resources/mc/latest_all";
            case BACKUP -> "http://stub.kiki-items.cn/api/resources/mc/latest";
        };
    }
}
