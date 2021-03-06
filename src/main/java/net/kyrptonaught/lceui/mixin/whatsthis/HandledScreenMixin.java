package net.kyrptonaught.lceui.mixin.whatsthis;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.kyrptonaught.lceui.whatsThis.DescriptionInstance;
import net.kyrptonaught.lceui.whatsThis.DescriptionRenderer;
import net.kyrptonaught.lceui.whatsThis.WhatsThisInit;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(HandledScreen.class)
public class HandledScreenMixin extends Screen {

    DescriptionRenderer descriptionRenderer;

    @Shadow
    @Nullable
    protected Slot focusedSlot;

    @Shadow
    protected int x;

    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void invsort$mouseClicked(double x, double y, int button, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (client == null || client.player == null)
            return;
        if (WhatsThisInit.isKeybindPressed(button, InputUtil.Type.MOUSE)) {
            if (focusedSlot != null && !focusedSlot.getStack().isEmpty()) {
                //  DescriptionRenderer.setToRender(DescriptionInstance.ofItem(focusedSlot.getStack().copy()), true);
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
                if (descriptionRenderer == null) descriptionRenderer = new DescriptionRenderer();
                descriptionRenderer.setToRender(DescriptionInstance.ofItem(focusedSlot.getStack().copy()).bindToScreen(this), true);
                // shiftAmount = x;
                //shiftAmount = Math.max(25, this.x - 100);
                //shiftAmount = 250;

                // GLFW.glfwSetCursorPos(MinecraftClient.getInstance().getWindow().getHandle(), this.focusedSlot.x, client.mouse.getY());
            }
            callbackInfoReturnable.setReturnValue(true);
        }
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    public void tickDescription(CallbackInfo ci) {
        if (descriptionRenderer != null)
            descriptionRenderer.tick();
    }

    @Inject(method = "render", at = @At(value = "HEAD"))
    public void fixRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        //matrices.translate(-shiftAmount, 0, 0);
    }

    @Inject(method = "render", at = @At(value = "TAIL"))
    public void fixRender2(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (descriptionRenderer != null)
            descriptionRenderer.renderDescription(this.client, matrices, delta);
        //matrices.translate(shiftAmount, 0, 0);
    }
}
