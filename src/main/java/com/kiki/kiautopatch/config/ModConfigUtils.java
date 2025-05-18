package com.kiki.kiautopatch.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;

public class ModConfigUtils {
    public static ModConfig get() {
        ConfigHolder<ModConfig> holder = AutoConfig.getConfigHolder(ModConfig.class);
        return holder.getConfig();
    }

    public static void save() {
        ConfigHolder<ModConfig> holder = AutoConfig.getConfigHolder(ModConfig.class);
        holder.save();
    }
}
