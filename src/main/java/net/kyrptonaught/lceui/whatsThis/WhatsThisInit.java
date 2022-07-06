package net.kyrptonaught.lceui.whatsThis;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.List;

public class WhatsThisInit {
    private static Identifier background = new Identifier("lceui:textures/gui/whatsthis.png");
    private static Identifier slot = new Identifier("lceui:textures/gui/slot.png");
    public static HashMap<Identifier, BlockDescription> blockDescriptions = new HashMap<>();

    public static void init() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new ResourceLoader());
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            renderPopUp(matrixStack);
        });
    }

    public static void renderPopUp(MatrixStack matrixStack) {
        matrixStack.push();
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.world != null) {
            TextRenderer textRenderer = client.textRenderer;
            HitResult hit = client.crosshairTarget;
            if (hit instanceof BlockHitResult blockHitResult) {
                int x = client.getWindow().getScaledWidth() - 220 - 20;
                BlockState block = client.world.getBlockState(blockHitResult.getBlockPos());
                ItemStack itemStack = block.getBlock().getPickStack(client.world, blockHitResult.getBlockPos(), block);
                BakedModel bakedModel = null;

                Identifier id = Registry.BLOCK.getId(block.getBlock());
                BlockDescription blockDescription = blockDescriptions.get(id);
                if (blockDescription != null && blockDescription.model != null) {
                    ModelIdentifier modelID = new ModelIdentifier(blockDescription.model.replace("item/", "") + "#inventory");
                    bakedModel = client.getBakedModelManager().getModel(modelID);
                }

                if (bakedModel == null) {
                    bakedModel = client.getItemRenderer().getModel(itemStack, null, client.player, 0);
                }
                String key = block.getBlock().getTranslationKey();
                List<OrderedText> description = textRenderer.wrapLines(new TranslatableText(key + ".description"), 200);

                int totalHeight = 32 + (description.size() * 10);
                float scale = (totalHeight + 5 + 20) / 110f;

                matrixStack.push();
                //matrixStack.scale(200 /799f,scale,1);
                renderBackground(matrixStack, x, 20, 200 + 20, (totalHeight + 5 + 20));
                matrixStack.pop();

                DrawableHelper.drawTextWithShadow(matrixStack, textRenderer, new TranslatableText(key), x + 15, 35, 0xFFFFFF);
                for (int i = 0; i < description.size(); i++) {
                    textRenderer.draw(matrixStack, description.get(i), x + 15, 47 + (i * 10), 0xFFFFFF);
                }

                //client.getItemRenderer().renderInGuiWithOverrides(client.player, itemStack, 5, 18 + (description.size() * 10), 0);
                //client.getItemRenderer().renderGuiItemOverlay(textRenderer, itemStack, 5, 18 + (description.size() * 10), "");
                // client.getBakedModelManager().getModel();

                RenderSystem.setShaderTexture(0, slot);
                DrawableHelper.drawTexture(matrixStack, x +103 -1, 15 + totalHeight + 5 - 1, 0, 0, 17, 17, 128, 128);
                renderGuiItemModel(client.getTextureManager(), client.getItemRenderer(), itemStack, x + 103, 15 + totalHeight + 5, bakedModel);
            }
        }
        matrixStack.pop();
    }

    private static void renderBackground(MatrixStack matrices, int x, int y, int width, int height) {
        RenderSystem.setShaderTexture(0, background);
        DrawableHelper.drawTexture(matrices, x, y, 0, 0, width, height, width, height);
    }

    protected static void renderGuiItemModel(TextureManager textureManager, ItemRenderer itemRenderer, ItemStack stack, int x, int y, BakedModel bakedModel) {
        itemRenderer.zOffset = bakedModel.hasDepth() ? itemRenderer.zOffset + 50.0f + (float) 0 : itemRenderer.zOffset + 50.0f;

        boolean bl;
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
        matrixStack.scale(16.0f, 16.0f, 16.0f);
        RenderSystem.applyModelViewMatrix();
        MatrixStack matrixStack2 = new MatrixStack();
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        boolean bl2 = bl = !bakedModel.isSideLit();
        if (bl) {
            DiffuseLighting.disableGuiDepthLighting();
        }
        itemRenderer.renderItem(stack, ModelTransformation.Mode.GUI, false, matrixStack2, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, bakedModel);
        immediate.draw();
        RenderSystem.enableDepthTest();
        if (bl) {
            DiffuseLighting.enableGuiDepthLighting();
        }
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
        itemRenderer.zOffset = bakedModel.hasDepth() ? itemRenderer.zOffset - 50.0f - (float) 0 : itemRenderer.zOffset - 50.0f;
    }
}
