package net.kyrptonaught.lceui.whatsThis;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.kyrptonaught.kyrptconfig.keybinding.DisplayOnlyKeyBind;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.registry.Registry;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;

public class WhatsThisInit {
    public static HashMap<Identifier, ItemDescription> itemDescriptions = new HashMap<>();

    public static KeyBinding invBind;

    public static void init() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new ResourceLoader());
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            matrixStack.push();
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null && client.world != null && client.currentScreen == null) {
                HitResult hit = client.crosshairTarget;
                if (hit instanceof BlockHitResult blockHitResult) {
                    BlockState state = client.world.getBlockState(blockHitResult.getBlockPos());
                    if (!state.isAir())
                        DescriptionRenderer.setToRender(state);
                }
            }
            DescriptionRenderer.renderDescription(client, matrixStack, tickDelta);
            matrixStack.pop();
        });

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

    public static ItemDescription getDescription(Identifier itemID, ItemDescription itemDescription, String defaultKey) {
        tryInherit(itemID, itemDescription);

        if (itemDescription.isFieldBlank(itemDescription.text.name)) {
            itemDescription.text.name = defaultKey;
        }
        if (itemDescription.isFieldBlank(itemDescription.text.description)) {
            itemDescription.text.description = defaultKey + ".description";
        }
        return itemDescription;
    }

    public static void tryInherit(Identifier id, ItemDescription itemDescription) {
        if (!itemDescription.isFieldBlank(itemDescription.parent)) {
            Identifier parentID = itemDescription.getParent();

            ItemDescription parent = itemDescriptions.get(parentID);
            if (parent != null) {
                tryInherit(parentID, parent);
                itemDescription.copyFrom(itemDescriptions.get(parentID));
            } else {
                System.out.println("Invalid Parent for Item " + id + " : " + parentID);
            }
        }
    }
}