package com.kiki.kiautopatch.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;

public class ConfigUtils {
    /** 取得配置实例 **/
    public static ModConfig get() {
        ConfigHolder<ModConfig> holder = AutoConfig.getConfigHolder(ModConfig.class);
        return holder.getConfig();
    }

    /** 保存当前修改 **/
    public static void save() {
        ConfigHolder<ModConfig> holder = AutoConfig.getConfigHolder(ModConfig.class);
        holder.save();
    }
}
