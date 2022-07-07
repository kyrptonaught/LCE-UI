package net.kyrptonaught.lceui.whatsThis;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
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
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class DescriptionRenderer {
    private static final Identifier slot = new Identifier("lceui:textures/gui/whatsthis/slot.png");
    private static final Identifier TL = new Identifier("lceui:textures/gui/whatsthis/popup/tl.png");
    private static final Identifier TM = new Identifier("lceui:textures/gui/whatsthis/popup/tm.png");
    private static final Identifier TR = new Identifier("lceui:textures/gui/whatsthis/popup/tr.png");

    private static final Identifier ML = new Identifier("lceui:textures/gui/whatsthis/popup/ml.png");
    private static final Identifier MM = new Identifier("lceui:textures/gui/whatsthis/popup/mm.png");
    private static final Identifier MR = new Identifier("lceui:textures/gui/whatsthis/popup/mr.png");

    private static final Identifier BL = new Identifier("lceui:textures/gui/whatsthis/popup/bl.png");
    private static final Identifier BM = new Identifier("lceui:textures/gui/whatsthis/popup/bm.png");
    private static final Identifier BR = new Identifier("lceui:textures/gui/whatsthis/popup/br.png");

    public static ItemStack renderingStack;
    public static ItemDescription renderingDescription;
    public static float renderedTicks;

    public static void setToRender(BlockState blockState) {
        if (blockState == null || blockState.isAir()) return;
        ItemDescription blockDescription = WhatsThisInit.getDescriptionForBlock(blockState);
        setToRender(blockState, blockDescription);

    }

    public static void setToRender(BlockState blockState, ItemDescription description) {
        if (blockState == null || blockState.isAir() || description == null) return;
        ItemStack itemStack = Item.fromBlock(blockState.getBlock()).getDefaultStack();
        setToRender(itemStack, description);
    }

    public static void setToRender(ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty()) return;
        ItemDescription blockDescription = WhatsThisInit.getDescriptionForItem(itemStack);
        setToRender(itemStack, blockDescription);
    }

    public static void setToRender(ItemStack itemStack, ItemDescription description) {
        if (itemStack == null || itemStack.isEmpty() || description == null) return;
        renderingStack = itemStack;
        renderingDescription = description;
        renderedTicks = 0;
    }

    public static void renderDescription(MinecraftClient client, MatrixStack matrixStack, float delta) {
        if (renderingStack == null || renderingStack.isEmpty() || renderingDescription == null) return;

        renderedTicks += delta;
        if (renderedTicks > 200) {
            renderingStack = null;
            renderingDescription = null;
            renderedTicks = 0;
            return;
        }

        int x = client.getWindow().getScaledWidth() - 220 - 20;

        BakedModel bakedModel;
        if (renderingDescription.isFieldBlank(renderingDescription.model))
            bakedModel = client.getItemRenderer().getModel(renderingStack, null, client.player, 0);
        else {
            ModelIdentifier modelID = new ModelIdentifier(renderingDescription.model.replace("item/", "") + "#inventory");
            bakedModel = client.getBakedModelManager().getModel(modelID);
        }
        render(client, matrixStack, x, 20, renderingDescription.text.name, renderingDescription.text.description, renderingStack, bakedModel);
    }

    private static void render(MinecraftClient client, MatrixStack matrixStack, int x, int y, String titleKey, String descriptionKey, ItemStack stack, BakedModel model) {
        TextRenderer textRenderer = client.textRenderer;

        List<OrderedText> description = textRenderer.wrapLines(new TranslatableText(descriptionKey), 200);

        int totalHeight = 32 + (description.size() * 10);

        renderBackground(matrixStack, x, y, 200 + 20, (totalHeight + 15 + 20));

        DrawableHelper.drawTextWithShadow(matrixStack, textRenderer, new TranslatableText(titleKey), x + 15, 35, 0xFFFFFF);
        for (int i = 0; i < description.size(); i++)
            textRenderer.draw(matrixStack, description.get(i), x + 15, 47 + (i * 10), 0xFFFFFF);

        matrixStack.translate(x + 103 - 1, 15 + totalHeight + 5 - 1, 1);
        matrixStack.scale(1.5f,1.5f,1.5f);
        matrixStack.translate(-(x + 103 - 1) -3, -(15 + totalHeight + 5 - 1) - 3, 1);
        RenderSystem.setShaderTexture(0, slot);
        DrawableHelper.drawTexture(matrixStack, x + 103 - 1, 15 + totalHeight + 5 - 1, 0, 0, 18, 18, 128, 128);
        renderGuiItemModel(client.getTextureManager(), client.getItemRenderer(), stack, x + 103, 15 + totalHeight + 5, model);
    }

    private static void renderBackground(MatrixStack matrices, int x, int y, int width, int height) {
        drawTexture(matrices, x, y, 8, 8, TL);
        drawTexture(matrices, x + 8, y, width - 16, 8, TM);
        drawTexture(matrices, x + width - 8, y, 8, 8, TR);

        y = y + 8;
        drawTexture(matrices, x, y, 8, height - 16, ML);
        drawTexture(matrices, x + 8, y, width - 16, height - 16, MM);
        drawTexture(matrices, x + width - 8, y, 8, height - 16, MR);

        y = y + height - 16;
        drawTexture(matrices, x, y, 8, 8, BL);
        drawTexture(matrices, x + 8, y, width - 16, 8, BM);
        drawTexture(matrices, x + width - 8, y, 8, 8, BR);
    }

    private static void drawTexture(MatrixStack matrices, int x, int y, int width, int height, Identifier texture) {
        RenderSystem.setShaderTexture(0, texture);
        DrawableHelper.drawTexture(matrices, x, y, 0, 0, width, height, 8, 8);
    }

    private static void renderGuiItemModel(TextureManager textureManager, ItemRenderer itemRenderer, ItemStack stack, int x, int y, BakedModel bakedModel) {
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
        matrixStack.scale(16.0f + 8, 16.0f + 8, 16.0f);
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
