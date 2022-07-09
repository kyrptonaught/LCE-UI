package net.kyrptonaught.lceui.whatsThis;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DescriptionInstance {
    private ItemStack displayStack;
    private ItemDescription displayDescription;
    private BakedModel displayModel;

    private Screen boundToScreen;
    private int openTicks = 140;


    public static DescriptionInstance ofItem(ItemStack stack) {
        return ofItem(stack, WhatsThisInit.descriptionManager.getDescriptionForItem(stack));
    }

    public static DescriptionInstance ofItem(ItemStack stack, ItemDescription itemDescription) {
        DescriptionInstance instance = new DescriptionInstance();
        instance.displayStack = stack;
        instance.displayDescription = itemDescription;
        return instance;
    }

    public static DescriptionInstance ofEntity(Entity entity) {
        return ofEntity(entity, WhatsThisInit.descriptionManager.getDescriptionForEntity(entity.getType()));
    }

    public static DescriptionInstance ofEntity(Entity entity, ItemDescription itemDescription) {
        DescriptionInstance instance = new DescriptionInstance();
        instance.displayStack = entity.getPickBlockStack();
        instance.displayDescription = itemDescription;
        return instance;
    }

    public static DescriptionInstance ofBlock(World world, BlockPos pos, BlockState blockState) {
        return ofBlock(world, pos, blockState, WhatsThisInit.descriptionManager.getDescriptionForBlock(blockState));
    }

    public static DescriptionInstance ofBlock(World world, BlockPos pos, BlockState blockState, ItemDescription itemDescription) {
        if (blockState == null || blockState.isAir() || itemDescription == null) return null;
        ItemStack itemStack = Item.fromBlock(blockState.getBlock()).getDefaultStack();

        if (itemStack.isEmpty())
            itemStack = blockState.getBlock().getPickStack(world, pos, blockState);

        return ofItem(itemStack, itemDescription);
    }

    public DescriptionInstance bindToScreen(Screen screen) {
        this.boundToScreen = screen;
        return this;
    }

    public void tickOpen() {
        openTicks--;
    }

    public boolean shouldHide(MinecraftClient client) {
        return client.options.debugEnabled;
    }

    public boolean shouldClose(MinecraftClient client) {
        if (openTicks <= 0) return true;

        if (boundToScreen == null) {
            return client.currentScreen instanceof HandledScreen<?>;
        }
        return !boundToScreen.equals(client.currentScreen);
    }

    public String getGroupKey() {
        return displayDescription.group;
    }

    public MutableText getNameTranslation() {
        return Text.translatable(displayDescription.text.name);
    }

    public MutableText getDescTranslation() {
        return Text.translatable(displayDescription.text.description);
    }

    public ItemStack getItemStack() {
        return displayStack;
    }

    public BakedModel getDisplayModel(MinecraftClient client) {
        if (displayModel == null && displayDescription.displaysicon && displayStack != null) {
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
