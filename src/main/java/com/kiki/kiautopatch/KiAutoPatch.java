package com.kiki.kiautopatch;

import com.kiki.kiautopatch.config.ModConfig;
import com.kiki.kiautopatch.services.ResourceService;
import com.kiki.kiautopatch.views.EulaScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class KiAutoPatch implements ClientModInitializer {
	public static ModConfig config;

	@Override
	public void onInitializeClient() {
		config = ModConfig.load();

		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			if (!config.eulaAccepted) {
				MinecraftClient.getInstance().setScreen(
						new EulaScreen(null)
				);
			} else {
				ResourceService.checkAndUpdate(client);
			}
		});
	}
}
