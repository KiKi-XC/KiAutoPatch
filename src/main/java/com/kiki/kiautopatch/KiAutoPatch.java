package com.kiki.kiautopatch;

import com.kiki.kiautopatch.services.ResourceService;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class KiAutoPatch implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// 每当客户端进入世界后检查一次（也可改为 onGameStarted 或其他时机）
        // 只执行一次，可加标志位避免重复触发
        // 注：可以在执行完毕后注销监听，或使用一次性调度器
        ClientTickEvents.END_CLIENT_TICK.register(ResourceService::checkAndUpdate);
	}
}
