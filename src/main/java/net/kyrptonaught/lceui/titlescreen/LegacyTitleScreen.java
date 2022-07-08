package net.kyrptonaught.lceui.titlescreen;


import net.kyrptonaught.lceui.LCEUIMod;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class LegacyTitleScreen extends TitleScreen {
    public static final LegacyPanoramaRenderer PANORAMA_CUBE_MAP = new LegacyPanoramaRenderer(new Identifier(LCEUIMod.MOD_ID, "textures/gui/title/background/69/day/69_day_large.png"));

    public LegacyTitleScreen() {
        super(false);
    }

    @Override
    public void init() {
        backgroundRenderer = new RotatingCubeMapRenderer(PANORAMA_CUBE_MAP);
        int y = this.height / 4 + 48;
        int spacingY = 24;
        this.addDrawableChild(new LegacyButton(this.width / 2 - 100, y, 200, 20, new LiteralText("Play Game"), button -> this.client.setScreen(new SelectWorldScreen(this))));
        this.addDrawableChild(new LegacyButton(this.width / 2 - 100, y + spacingY * 1, 200, 20, new LiteralText("Mini Games"), button -> this.client.setScreen(new SelectWorldScreen(this))));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_E) {
            this.client.setScreen(new LegacyTitleScreen());
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        PANORAMA_CUBE_MAP.render(matrices, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
