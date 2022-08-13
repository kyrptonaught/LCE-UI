package net.kyrptonaught.lceui.mixin.creativeinv;

import net.kyrptonaught.lceui.creativeInv.CustomItemGroup;
import net.kyrptonaught.lceui.creativeInv.ItemGroupExpander;
import net.minecraft.item.ItemGroup;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ItemGroup.class)
public class ItemGroupMixin implements ItemGroupExpander {
    @Shadow
    @Final
    @Mutable
    public static ItemGroup[] GROUPS;

    @Override
    public void replaceGroups(List<CustomItemGroup> customItemGroupList) {
        GROUPS = customItemGroupList.toArray(ItemGroup[]::new);
    }

    @Override
    public void clearGroups() {
        GROUPS = new ItemGroup[1];
    }

    @Override
    public void shrinkGroups() {
        ItemGroup[] tempGroups = GROUPS;
        GROUPS = new ItemGroup[12];

        for (int i = 0; i < GROUPS.length; i++) {
            GROUPS[i] = tempGroups[i];
        }
    }
}
