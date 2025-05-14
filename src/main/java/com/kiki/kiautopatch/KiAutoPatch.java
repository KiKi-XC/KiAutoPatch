package com.kiki.kiautopatch;

import com.kiki.kiautopatch.config.ModConfig;
import com.kiki.kiautopatch.services.ResourceService;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class KiAutoPatch implements ClientModInitializer {
	public static ModConfig config;
	@Override
	public void onInitializeClient() {
        KiAutoPatch.config = ModConfig.load();
        ClientTickEvents.END_CLIENT_TICK.register(ResourceService::checkAndUpdate);
	}
}
