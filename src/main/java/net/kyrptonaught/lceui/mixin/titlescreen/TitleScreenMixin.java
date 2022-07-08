package net.kyrptonaught.lceui.mixin.titlescreen;

import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    public void hijackInit(CallbackInfo ci) {
       /* if (!((Object) this instanceof LegacyTitleScreen)) {
            MinecraftClient.getInstance().setScreen(new LegacyTitleScreen());
            ci.cancel();
        }*/
    }
}
