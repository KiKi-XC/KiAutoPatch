package com.kiki.kiautopatch.config;

import com.kiki.kiautopatch.utils.ModInfo;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "kiautopatch")
public class ModConfig implements ConfigData {
    public String modVersion = ModInfo.getModVersion("kiautopatch");

    @ConfigEntry.Gui.Tooltip
    public UrlOption urlOption = UrlOption.MAIN;

    @ConfigEntry.Gui.Tooltip
    public boolean eulaAccepted = false;

    @ConfigEntry.Gui.Tooltip
    public boolean autoUpdate = true;

    public enum UrlOption {
        MAIN,
        TEST
    }

    public String getResolvedUrl() {
        return switch (urlOption) {
            case MAIN   -> "http://stub.kiki-items.cn/api/resources/mc/latest_all";
            case TEST -> "http://127.0.0.1:8000/api/resources/mc/latest";
        };
    }
}
