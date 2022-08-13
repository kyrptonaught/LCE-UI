package net.kyrptonaught.lceui.creativeInv;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.impl.item.group.ItemGroupExtensions;
import net.kyrptonaught.lceui.creativeInv.resourceLoader.CustomTabLoader;
import net.minecraft.item.ItemGroup;
import net.minecraft.resource.ResourceType;

import java.util.ArrayList;
import java.util.List;

public class CreativeInvInit {

    public static List<CustomItemGroup> customItemGroupList = new ArrayList<>();

    public static void init() {
        //  new CustomItemGroup(2, "testing");
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new CustomTabLoader());
    }

    public static void addCustomTabGroup(CustomItemGroup.LoadedCustomGroup loadedCustomGroup) {
        ((ItemGroupExtensions) ItemGroup.BUILDING_BLOCKS).fabric_expandArray();
        customItemGroupList.add(CustomItemGroup.fromLoadedGroup(loadedCustomGroup));
    }

    public static void clearGroups() {
        customItemGroupList.clear();
        ((ItemGroupExpander) ItemGroup.BUILDING_BLOCKS).shrinkGroups();
    }
}
