package net.kyrptonaught.lceui.whatsThis;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.kyrptonaught.lceui.LCEUIMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class DescriptionRenderer {
    private static final Identifier slot = new Identifier(LCEUIMod.MOD_ID, "textures/gui/whatsthis/slot.png");
    private static final Identifier TL = new Identifier(LCEUIMod.MOD_ID, "textures/gui/whatsthis/popup/tl.png");
    private static final Identifier TM = new Identifier(LCEUIMod.MOD_ID, "textures/gui/whatsthis/popup/tm.png");
    private static final Identifier TR = new Identifier(LCEUIMod.MOD_ID, "textures/gui/whatsthis/popup/tr.png");

    private static final Identifier ML = new Identifier(LCEUIMod.MOD_ID, "textures/gui/whatsthis/popup/ml.png");
    private static final Identifier MM = new Identifier(LCEUIMod.MOD_ID, "textures/gui/whatsthis/popup/mm.png");
    private static final Identifier MR = new Identifier(LCEUIMod.MOD_ID, "textures/gui/whatsthis/popup/mr.png");

    private static final Identifier BL = new Identifier(LCEUIMod.MOD_ID, "textures/gui/whatsthis/popup/bl.png");
    private static final Identifier BM = new Identifier(LCEUIMod.MOD_ID, "textures/gui/whatsthis/popup/bm.png");
    private static final Identifier BR = new Identifier(LCEUIMod.MOD_ID, "textures/gui/whatsthis/popup/br.png");

    public static DescriptionInstance renderingDescription;

    public static boolean setToRender(DescriptionInstance descriptionInstance, boolean bypassViewedCheck) {
        if (renderingDescription != null || descriptionInstance == null)
            return false;

        if (!bypassViewedCheck) {
            String key = descriptionInstance.getGroupKey();
            if (WhatsThisInit.descriptionManager.viewedDescriptions.contains(key))
                return false;
            WhatsThisInit.descriptionManager.viewedDescriptions.add(key);
        }

        renderingDescription = descriptionInstance;
        return true;
    }

    public static void renderDescription(MinecraftClient client, MatrixStack matrixStack, float delta) {
        if (renderingDescription == null) return;

        if (!client.isPaused())
            renderingDescription.tickOpen();

        if (renderingDescription.shouldClose(client)) {
            renderingDescription = null;
            return;
        }

        if (renderingDescription.shouldHide(client)) return;

        int x = client.getWindow().getScaledWidth() - 220 - 20;

        BakedModel bakedModel = renderingDescription.getDisplayModel(client);

        render(client, matrixStack, x, 20, renderingDescription.getNameTranslation(), renderingDescription.getDescTranslation(), renderingDescription.getItemStack(), bakedModel, bakedModel != null);
    }

    private static void render(MinecraftClient client, MatrixStack matrixStack, int x, int y, Text titleText, Text descriptionText, ItemStack stack, BakedModel model, boolean displayModel) {
        TextRenderer textRenderer = client.textRenderer;

        List<OrderedText> description = textRenderer.wrapLines(descriptionText, 200);

        int lineHeight = 33 + (description.size() * 11);

        int totalHeight = lineHeight + 7;
        if (displayModel) totalHeight = lineHeight + 15 + 20;

        renderBackground(matrixStack, x, y, 200 + 20, totalHeight);

        //DrawableHelper.drawTextWithShadow(matrixStack, textRenderer, titleText, x + 15, 35, 0xFFFFFF);
        textRenderer.draw(matrixStack, titleText, x + 15.5f, 35.5f, 0x000000);
        textRenderer.draw(matrixStack, titleText, x + 15, 35, 0xFFFFFF);
        for (int i = 0; i < description.size(); i++)
            textRenderer.draw(matrixStack, description.get(i), x + 15, 47.5f + (i * 11), 0xFFFFFF);

        if (displayModel) {
            float scale = 1.8f;
            int halfSize = 18 / 2;
            x += 103;
            y = 21 + lineHeight;
            matrixStack.translate(x + halfSize, y + halfSize, 1);
            matrixStack.scale(scale, scale, scale);
            matrixStack.translate(-(x + halfSize), -(y + halfSize), 1);

            RenderSystem.setShaderTexture(0, slot);

            DrawableHelper.drawTexture(matrixStack, x, y, 0, 0, 18, 18, 18, 18);
            renderGuiItemModel(client.getTextureManager(), client.getItemRenderer(), stack, x + 1, y + 1, scale, model);
        }
    }

    private static void renderBackground(MatrixStack matrices, int x, int y, int width, int height) {
        drawTexture(matrices, x, y, 7, 7, TL);
        drawTexture(matrices, x + 7, y, width - 14, 7, TM);
        drawTexture(matrices, x + width - 7, y, 7, 7, TR);

        y = y + 7;
        drawTexture(matrices, x, y, 7, height - 14, ML);
        drawTexture(matrices, x + 7, y, width - 14, height - 14, MM);
        drawTexture(matrices, x + width - 7, y, 7, height - 14, MR);

        y = y + height - 14;
        drawTexture(matrices, x, y, 7, 7, BL);
        drawTexture(matrices, x + 7, y, width - 14, 7, BM);
        drawTexture(matrices, x + width - 7, y, 7, 7, BR);
    }

    private static void drawTexture(MatrixStack matrices, int x, int y, int width, int height, Identifier texture) {
        RenderSystem.setShaderTexture(0, texture);
        DrawableHelper.drawTexture(matrices, x, y, 0, 0, width, height, 7, 7);
    }

    private static void renderGuiItemModel(TextureManager textureManager, ItemRenderer itemRenderer, ItemStack stack, float x, float y, float scale, BakedModel bakedModel) {
        itemRenderer.zOffset = bakedModel.hasDepth() ? itemRenderer.zOffset + 50.0f + (float) 0 : itemRenderer.zOffset + 50.0f;

        boolean sideLit = !bakedModel.isSideLit();
        textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).setFilter(false, false);
        RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.translate(x, y, 100.0f + itemRenderer.zOffset);
        matrixStack.translate(8.0, 8.0, 0.0);
        matrixStack.scale(1.0f, -1.0f, 1.0f);
        matrixStack.scale(16.0f * scale, 16.0f * scale, 16.0f);
        RenderSystem.applyModelViewMatrix();
        MatrixStack matrixStack2 = new MatrixStack();
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        if (sideLit) {
            DiffuseLighting.disableGuiDepthLighting();
        }
        itemRenderer.renderItem(stack, ModelTransformation.Mode.GUI, false, matrixStack2, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, bakedModel);
        immediate.draw();
        RenderSystem.enableDepthTest();
        if (sideLit) {
            DiffuseLighting.enableGuiDepthLighting();
        }
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
        itemRenderer.zOffset = bakedModel.hasDepth() ? itemRenderer.zOffset - 50.0f - (float) 0 : itemRenderer.zOffset - 50.0f;
    }
}