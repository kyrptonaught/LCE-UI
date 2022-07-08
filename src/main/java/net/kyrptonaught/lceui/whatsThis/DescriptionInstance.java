package net.kyrptonaught.lceui.whatsThis;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DescriptionInstance {
    private ItemStack displayStack;
    private ItemDescription displayDescription;
    private BakedModel displayModel;


    public static DescriptionInstance ofItem(ItemStack stack) {
        return ofItem(stack, WhatsThisInit.getDescriptionForItem(stack));
    }

    public static DescriptionInstance ofItem(ItemStack stack, ItemDescription itemDescription) {
        DescriptionInstance instance = new DescriptionInstance();
        instance.displayStack = stack;
        instance.displayDescription = itemDescription;
        return instance;
    }

    public static DescriptionInstance ofEntity(Entity entity) {
        return ofEntity(entity, WhatsThisInit.getDescriptionForEntity(entity.getType()));
    }

    public static DescriptionInstance ofEntity(Entity entity, ItemDescription itemDescription) {
        DescriptionInstance instance = new DescriptionInstance();
        instance.displayStack = entity.getPickBlockStack();
        instance.displayDescription = itemDescription;
        return instance;
    }

    public static DescriptionInstance ofBlock(World world, BlockPos pos, BlockState blockState) {
        return ofBlock(world, pos, blockState, WhatsThisInit.getDescriptionForBlock(blockState));
    }

    public static DescriptionInstance ofBlock(World world, BlockPos pos, BlockState blockState, ItemDescription itemDescription) {
        if (blockState == null || blockState.isAir() || itemDescription == null) return null;
        ItemStack itemStack = Item.fromBlock(blockState.getBlock()).getDefaultStack();

        if (itemStack.isEmpty())
            itemStack = blockState.getBlock().getPickStack(world, pos, blockState);

        return ofItem(itemStack, itemDescription);
    }

    public String getGroupKey() {
        return displayDescription.group;
    }

    public MutableText getNameTranslation() {
        return new TranslatableText(displayDescription.text.name);
    }

    public MutableText getDescTranslation() {
        return new TranslatableText(displayDescription.text.description);
    }

    public ItemStack getItemStack() {
        return displayStack;
    }

    public BakedModel getDisplayModel(MinecraftClient client) {
        if (displayModel == null && displayDescription.displaysicon) {
            if (displayDescription.isFieldBlank(displayDescription.model))
                displayModel = client.getItemRenderer().getModel(displayStack, null, client.player, 0);

            else {
                ModelIdentifier modelID = new ModelIdentifier(WhatsThisInit.getCleanIdentifier(new Identifier(displayDescription.model)), "inventory");
                displayModel = client.getBakedModelManager().getModel(modelID);
            }
        }
        return displayModel;
    }
}
