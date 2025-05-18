package com.kiki.kiautopatch;

import com.kiki.kiautopatch.config.ConfigUtils;
import com.kiki.kiautopatch.config.ModConfig;
import com.kiki.kiautopatch.services.ResourceService;
import com.kiki.kiautopatch.views.EulaScreen;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class KiAutoPatch implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
		ModConfig cfg = ConfigUtils.get();
		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			if (!cfg.eulaAccepted) {
				MinecraftClient.getInstance().setScreen(new EulaScreen(null));
			} else {
				ResourceService.checkAndUpdate(client);
			}
		});
	}
}
