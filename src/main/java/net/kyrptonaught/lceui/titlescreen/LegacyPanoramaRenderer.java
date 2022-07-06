package net.kyrptonaught.lceui.titlescreen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class LegacyPanoramaRenderer extends CubeMapRenderer {
    private final Identifier texture;

    float time = 0;

    int width = 6150;
    int height = 1080;

    public LegacyPanoramaRenderer(Identifier texture) {
        super(new Identifier(""));
        this.texture = texture;
    }

    //dummy to prevent drawing
    public void draw(MinecraftClient client, float x, float y, float alpha) {
    }

    public void render(MatrixStack matrices, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();
        float deltaH = client.getWindow().getScaledHeight() * 1f / height;
        float loop = width * deltaH;
        time += delta;
        if (time >= loop) time -= loop;

        matrices.push();
        matrices.translate(-time, 0, 0);
        matrices.scale(deltaH, deltaH, 1);

        RenderSystem.setShaderTexture(0, texture);
        TitleScreen.drawTexture(matrices, 0, 0, 0, 0, width, height, width, height);
        TitleScreen.drawTexture(matrices, width, 0, 0, 0, width, height, width, height);
        TitleScreen.drawTexture(matrices, width * 2, 0, 0, 0, width, height, width, height);
        TitleScreen.drawTexture(matrices, width * 3, 0, 0, 0, width, height, width, height);
        matrices.pop();
        //System.out.println(time);
    }

    public CompletableFuture<Void> loadTexturesAsync(TextureManager textureManager, Executor executor) {
        return CompletableFuture.allOf(textureManager.loadTextureAsync(texture, executor));
    }
}