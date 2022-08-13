package net.kyrptonaught.lceui.mixin.creativeinv;

import net.kyrptonaught.lceui.creativeInv.CustomItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(method = "isIn", at = @At("RETURN"), cancellable = true)
    public void isInCustomGroups(ItemGroup group, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && group instanceof CustomItemGroup customItemGroup) {
            cir.setReturnValue(customItemGroup.containsItem((Item) (Object) this));
        }
    }
}
