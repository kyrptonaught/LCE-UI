package net.kyrptonaught.lceui.whatsThis;

import com.mojang.brigadier.Command;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.kyrptonaught.lceui.LCEUIMod;
import net.kyrptonaught.lceui.whatsThis.resourceloaders.DescriptionResourceLoader;
import net.kyrptonaught.lceui.whatsThis.resourceloaders.ModelResourceLoader;
import net.kyrptonaught.lceui.whatsThis.resourceloaders.TagResourceLoader;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

public class WhatsThisInit {
    public static DescriptionManager descriptionManager;
    public static DescriptionRenderer descriptionRenderer;
    public static KeyBinding invBind;

    public static void init() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new TagResourceLoader());
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new DescriptionResourceLoader());
        ModelLoadingRegistry.INSTANCE.registerModelProvider(ModelResourceLoader::loadModels);

        descriptionManager = new DescriptionManager();
        descriptionRenderer = new DescriptionRenderer();

        ClientTickEvents.END_WORLD_TICK.register(world -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null && world != null && client.currentScreen == null) {
                HitResult hit = client.crosshairTarget;
                if (hit instanceof BlockHitResult blockHitResult) {
                    BlockState state = world.getBlockState(blockHitResult.getBlockPos());
                    if (!state.isAir()) {
                        descriptionRenderer.setToRender(DescriptionInstance.ofBlock(world, blockHitResult.getBlockPos(), state), false);
                    }
                } else if (hit instanceof EntityHitResult entityHitResult) {
                    Entity entity = entityHitResult.getEntity();
                    if (entity.isAlive()) {
                        descriptionRenderer.setToRender(DescriptionInstance.ofEntity(entity), false);
                    }
                }
            }
            descriptionRenderer.tick();
        });

        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            descriptionRenderer.renderDescription(client, matrixStack, tickDelta);
        });

        ClientCommandManager.DISPATCHER.register(
                ClientCommandManager.literal(LCEUIMod.MOD_ID)
                                .then(ClientCommandManager.literal("descriptions")
                                        .then(ClientCommandManager.literal("clear")
                                                .then(ClientCommandManager.literal("all")
                                                    .executes(context -> {
                                                    int count = descriptionManager.viewedDescriptions.size();
                                                    descriptionManager.viewedDescriptions.clear();
                                                    context.getSource().sendFeedback(new TranslatableText("key.lceui.whatsthis.feedback.clearall", count));
                                                    return Command.SINGLE_SUCCESS;
                                                }))
                                                .then(ClientCommandManager.argument("block", ViewedBlockArgumentType.viewedBlockArgumentType())
                                                        .executes(context -> {
                                                            String id = ViewedBlockArgumentType.getViewedBlockArgumentType(context, "block");
                                                            boolean removed = descriptionManager.viewedDescriptions.remove(id);
                                                            if (removed)
                                                                context.getSource().sendFeedback(new TranslatableText("key.lceui.whatsthis.feedback.cleared", id));
                                                            else
                                                                context.getSource().sendFeedback(new TranslatableText("key.lceui.whatsthis.feedback.notfound", id));
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

    public static Identifier getCleanIdentifier(Identifier identifier) {
        return new Identifier(identifier.getNamespace(), identifier.getPath()
                .replace("models/", "")
                .replace(".json", "")
                .replace("block/", "")
                .replace("item/", "")
                .replace("entity/", ""));

    }
}
