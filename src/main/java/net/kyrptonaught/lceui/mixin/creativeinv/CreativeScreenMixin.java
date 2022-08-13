package net.kyrptonaught.lceui.mixin.creativeinv;

import net.kyrptonaught.lceui.creativeInv.CustomItemGroup;
import net.kyrptonaught.lceui.whatsThis.DescriptionRenderer;
import net.kyrptonaught.lceui.whatsThis.WhatsThisInit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(CreativeInventoryScreen.class)
public class CreativeScreenMixin {

    @Inject(method = "renderTabIcon", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;II)V"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void renderLCECustomIcon(MatrixStack matrices, ItemGroup group, CallbackInfo ci, boolean bl, boolean bl2, int i, int j, int k, int l, int m) {
        if (group instanceof CustomItemGroup customItemGroup) {
            MinecraftClient client = MinecraftClient.getInstance();

            ModelIdentifier modelID = new ModelIdentifier(WhatsThisInit.getCleanIdentifier(new Identifier(customItemGroup.getItemModel())), "inventory");
            BakedModel model = client.getBakedModelManager().getModel(modelID);

            int n2 = group.isTopRow() ? 1 : -1;
            DescriptionRenderer.renderGuiItemModel(client.getTextureManager(), client.getItemRenderer(), group.getIcon(), l, m, 1, model);
            client.getItemRenderer().zOffset = 0.0f;
            ci.cancel();
        }
    }
}
