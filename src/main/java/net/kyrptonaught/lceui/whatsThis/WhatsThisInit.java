package net.kyrptonaught.lceui.whatsThis;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.Command;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.*;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.kyrptonaught.lceui.LCEUIMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.registry.Registry;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.HashSet;

public class WhatsThisInit {
    public static HashMap<Identifier, ItemDescription> itemDescriptions = new HashMap<>();
    public static HashSet<String> viewedBlocks = new HashSet<>();
    public static KeyBinding invBind;

    public static void init() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new ResourceLoader());
        ModelLoadingRegistry.INSTANCE.registerModelProvider(ResourceLoader::loadModels);

        ClientTickEvents.END_WORLD_TICK.register(world -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null && world != null && client.currentScreen == null) {
                HitResult hit = client.crosshairTarget;
                if (hit instanceof BlockHitResult blockHitResult) {
                    BlockState state = world.getBlockState(blockHitResult.getBlockPos());
                    if (!state.isAir()) {
                        DescriptionRenderer.setToRender(DescriptionInstance.ofBlock(world, blockHitResult.getBlockPos(), state), false);
                    }
                } else if (hit instanceof EntityHitResult entityHitResult) {
                    Entity entity = entityHitResult.getEntity();
                    if (entity.isAlive()) {
                        DescriptionRenderer.setToRender(DescriptionInstance.ofEntity(entity), false);
                    }
                }
            }
        });

        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();

            matrixStack.push();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            DescriptionRenderer.renderDescription(client, matrixStack, tickDelta);

            RenderSystem.disableBlend();
            matrixStack.pop();
        });

        ClientCommandManager.DISPATCHER.register(
                ClientCommandManager.literal(LCEUIMod.MOD_ID)
                        .then(ClientCommandManager.literal("whatsthis")
                                .then(ClientCommandManager.literal("clearAllViewedBlocks")
                                        .executes(context -> {
                                            viewedBlocks.clear();
                                            return Command.SINGLE_SUCCESS;
                                        }))
                                .then(ClientCommandManager.literal("clearViewedBlock")
                                        .then(ClientCommandManager.argument("block", ViewedBlockArgumentType.viewedBlockArgumentType())
                                                .executes(context -> {
                                                    Identifier id = ViewedBlockArgumentType.getViewedBlockArgumentType(context, "block");
                                                    viewedBlocks.remove(id.toString());
                                                    return Command.SINGLE_SUCCESS;
                                                })))));

        invBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.lceui.whatsthis",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_N,
                "key.categories.lceui"
        ));
    }

    public static boolean isKeybindPressed(int pressedKeyCode, InputUtil.Type type) {
        InputUtil.Key key = KeyBindingHelper.getBoundKeyOf(invBind);
        if (key.getCategory() != type)
            return false;

        return key.getCode() == pressedKeyCode;
    }

    public static ItemDescription getDescriptionForEntity(EntityType<?> entity) {
        Identifier id = Registry.ENTITY_TYPE.getId(entity);
        id = new Identifier(id.getNamespace(), "entity/" + id.getPath());
        ItemDescription blockDescription = itemDescriptions.getOrDefault(id, new ItemDescription());

        return getDescription(id, blockDescription, entity.getTranslationKey());
    }

    public static ItemDescription getDescriptionForBlock(BlockState blockState) {
        Identifier id = Registry.BLOCK.getId(blockState.getBlock());
        id = new Identifier(id.getNamespace(), "block/" + id.getPath());
        ItemDescription blockDescription = itemDescriptions.getOrDefault(id, new ItemDescription());

        return getDescription(id, blockDescription, blockState.getBlock().getTranslationKey());
    }

    public static ItemDescription getDescriptionForItem(ItemStack itemStack) {
        Identifier id = Registry.ITEM.getId(itemStack.getItem());
        id = new Identifier(id.getNamespace(), "item/" + id.getPath());
        ItemDescription itemDescription = itemDescriptions.getOrDefault(id, new ItemDescription());

        return getDescription(id, itemDescription, itemStack.getTranslationKey());
    }

    private static ItemDescription getDescription(Identifier itemID, ItemDescription itemDescription, String defaultKey) {
        tryInherit(itemID, itemDescription, new HashSet<>());

        if (itemDescription.isFieldBlank(itemDescription.text.name)) {
            itemDescription.text.name = defaultKey;
        }
        if (itemDescription.isFieldBlank(itemDescription.text.description)) {
            itemDescription.text.description = defaultKey + ".description";
        }
        if (itemDescription.displaysicon == null)
            itemDescription.displaysicon = true;

        if (itemDescription.isFieldBlank(itemDescription.group)) {
            itemDescription.group = itemID.toString();
        }
        return itemDescription;
    }

    public static void tryInherit(Identifier id, ItemDescription itemDescription, HashSet<Identifier> trace) {
        if (trace.contains(id)) {
            System.out.println("Loop detected! " + trace);
            return;
        }
        trace.add(id);
        if (!itemDescription.isFieldBlank(itemDescription.parent)) {
            Identifier parentID = itemDescription.getParent();
            ItemDescription parent = itemDescriptions.get(parentID);
            if (parent == null) {
                if (id.getPath().contains("block"))
                    parent = getDescriptionForBlock(Registry.BLOCK.get(getCleanIdentifier(parentID)).getDefaultState());
                else if (id.getPath().contains("item"))
                    parent = getDescriptionForItem(Registry.ITEM.get(getCleanIdentifier(parentID)).getDefaultStack());
                else if (id.getPath().contains("entity"))
                    parent = getDescriptionForEntity(Registry.ENTITY_TYPE.get(getCleanIdentifier(id)));
            }
            if (parent != null) {
                tryInherit(parentID, parent, trace);
                itemDescription.copyFrom(parent);
            } else {
                System.out.println("Parent does not exist for Item " + id + " : " + parentID);
            }
        }
    }

    public static Identifier getCleanIdentifier(Identifier identifier) {
        return new Identifier(identifier.getNamespace(), identifier.getPath()
                .replace("models/", "")
                .replace(".json", "")
                .replace("block/", "")
                .replace("item/", "")
                .replace("entity/", ""));

    }
}