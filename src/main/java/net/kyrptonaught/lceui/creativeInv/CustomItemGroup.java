package net.kyrptonaught.lceui.creativeInv;

import net.kyrptonaught.kyrptconfig.TagHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomItemGroup extends ItemGroup {
    private String itemModel;
    private Set<Identifier> items = new HashSet<>();

    public CustomItemGroup(int index, String id) {
        super(index, id);
    }

    public void setEnchanted(boolean isEnchanted) {
        ItemStack stack = getIcon();
        NbtCompound nbt = stack.getOrCreateNbt();
        if (isEnchanted) {
            if (!nbt.contains(ItemStack.ENCHANTMENTS_KEY, 9)) {
                nbt.put(ItemStack.ENCHANTMENTS_KEY, new NbtList());
            }
            nbt.getList(ItemStack.ENCHANTMENTS_KEY, 10).add(EnchantmentHelper.createNbt(null, 0));
        } else nbt.remove(ItemStack.ENCHANTMENTS_KEY);
    }

    public void setItemModel(String model) {
        this.itemModel = model;
    }

    public String getItemModel() {
        return itemModel;
    }

    public void setItems(List<String> newItems) {
        items.clear();
        newItems.forEach(s -> {
            if (s.startsWith("#")) {
                List<Identifier> tags = TagHelper.getItemsIDsInTag(new Identifier(s.replaceAll("#", "")));
                items.addAll(tags);
            } else
                items.add(new Identifier(s));
        });
    }

    public boolean containsItem(Item item) {
        Identifier id = Registry.ITEM.getId(item);

        return items.contains(id);
    }

    @Override
    public ItemStack createIcon() {
        return Items.STICK.getDefaultStack();
    }

    public static CustomItemGroup fromLoadedGroup(LoadedCustomGroup loadedCustomGroup) {
        CustomItemGroup itemGroup = new CustomItemGroup(ItemGroup.GROUPS.length - 1, loadedCustomGroup.name);
        itemGroup.setEnchanted(loadedCustomGroup.isEnchanted);
        itemGroup.setItemModel(loadedCustomGroup.tabIcon);
        itemGroup.setItems(loadedCustomGroup.items);
        return itemGroup;
    }

    public static class LoadedCustomGroup {

        public String name = "dummyGroup";
        public int index = -1;
        public String tabIcon = "empty";
        public boolean isEnchanted = false;

        public List<String> items = new ArrayList<>();

    }
}
