package com.kiki.kiautopatch.views;

import com.kiki.kiautopatch.config.ModConfigUtils;
import com.kiki.kiautopatch.config.ModConfig;
import com.kiki.kiautopatch.services.ResourceService;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;


@Environment(EnvType.CLIENT)
public class EulaScreen extends Screen {
    private final Screen parent;

    public EulaScreen(Screen parent) {
        super(Text.of("End User License Agreement"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int btnW    = 100;
        int btnH    = 20;
        int xCenter = this.width / 2;
        int yBase   = this.height / 2;

        this.addDrawableChild(ButtonWidget.builder(Text.of("同意"), btn -> {
            ModConfig cfg = ModConfigUtils.get();
            cfg.eulaAccepted = true;
            ModConfigUtils.save();
            MinecraftClient.getInstance().setScreen(parent);
            ResourceService.checkAndUpdate(MinecraftClient.getInstance());
        }).dimensions(xCenter - btnW - 5, yBase + 40, btnW, btnH).build());

        this.addDrawableChild(ButtonWidget.builder(Text.of("不同意"), btn -> {
            MinecraftClient.getInstance().scheduleStop();
        }).dimensions(xCenter + 5, yBase + 40, btnW, btnH).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        String line1 = "请详细阅读并同意 【KiAutoPatch-资源包自动更新】的 EULA 后继续游戏";
        String line2 = "不同意将退出游戏，请手动卸载Mod";
        String line3 = "https://kap.kiki-items.cn/eula";

        int w1 = this.textRenderer.getWidth(line1);
        int x1 = (this.width - w1) / 2;
        int y1 = this.height / 2 - 30;  // 适当上移，给三行留空间
        context.drawText(this.textRenderer, line1, x1, y1, 0xFFFFFF, true);

        int w2 = this.textRenderer.getWidth(line2);
        int x2 = (this.width - w2) / 2;
        int y2 = this.height / 2 - 10;
        context.drawText(this.textRenderer, line2, x2, y2, 0xFFFFFF, true);

        int w3 = this.textRenderer.getWidth(line3);
        int x3 = (this.width - w3) / 2;
        int y3 = this.height / 2 + 10;
        context.drawText(this.textRenderer, line3, x3, y3, 0x00AAFF, true);
    }
}
