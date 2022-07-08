package net.kyrptonaught.lceui.mixin.whatsthis;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.lceui.whatsThis.DescriptionInstance;
import net.kyrptonaught.lceui.whatsThis.DescriptionRenderer;
import net.kyrptonaught.lceui.whatsThis.WhatsThisInit;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(HandledScreen.class)
public class HandledScreenMixin extends Screen {

    @Shadow
    @Nullable
    protected Slot focusedSlot;

    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void invsort$mouseClicked(double x, double y, int button, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (client == null || client.player == null)
            return;
        if (WhatsThisInit.isKeybindPressed(button, InputUtil.Type.MOUSE)) {
            if (focusedSlot != null && !focusedSlot.getStack().isEmpty()) {
                DescriptionRenderer.setToRender(DescriptionInstance.ofItem(focusedSlot.getStack()), true);
            }
            callbackInfoReturnable.setReturnValue(true);
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void invsort$keyPressed(int keycode, int scancode, int modifiers, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (client == null || client.player == null)
            return;
        if (WhatsThisInit.isKeybindPressed(keycode, InputUtil.Type.KEYSYM)) {
            if (focusedSlot != null && !focusedSlot.getStack().isEmpty()) {
                DescriptionRenderer.setToRender(DescriptionInstance.ofItem(focusedSlot.getStack()), true);
            }
            callbackInfoReturnable.setReturnValue(true);
        }
    }
}
