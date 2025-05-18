package com.kiki.kiautopatch.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<Screen> getModConfigScreenFactory() {
        return parent -> {
            ConfigHolder<ModConfig> holder = AutoConfig.getConfigHolder(ModConfig.class);
            ModConfig config = holder.getConfig();

            // 创建 Builder
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.translatable("text.autoconfig.kiautopatch.title"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            // 保存时写盘
            builder.setSavingRunnable(holder::save);

            var general = builder.getOrCreateCategory(Text.translatable("text.autoconfig.kiautopatch.category.general"));
            general.addEntry(entryBuilder.startEnumSelector(
                                    Text.translatable("text.autoconfig.kiautopatch.urlOption"),
                                    ModConfig.UrlOption.class,
                                    config.urlOption
                            )
                            .setDefaultValue(ModConfig.UrlOption.MAIN)
                            .setSaveConsumer(value -> config.urlOption = value)
                            .build()
            );

            general.addEntry(entryBuilder.startBooleanToggle(
                                    Text.translatable("text.autoconfig.kiautopatch.eulaAccepted"),
                                    config.eulaAccepted
                            )
                            .setDefaultValue(true)
                            .setSaveConsumer(value -> config.eulaAccepted = value)
                            .build()
            );

            general.addEntry(entryBuilder.startBooleanToggle(
                                    Text.translatable("text.autoconfig.kiautopatch.autoUpdate"),
                                    config.autoUpdate
                            )
                            .setDefaultValue(true)
                            .setSaveConsumer(value -> config.autoUpdate = value)
                            .build()
            );

            return builder.build();
        };
    }
}
