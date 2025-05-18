package com.kiki.kiautopatch.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "kiautopatch")
public class ModConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    public String apiUrl = "http://stub.kiki-items.cn/api/resources/mc/latest_all";

    @ConfigEntry.Gui.Tooltip
    public boolean eulaAccepted = false;
}
