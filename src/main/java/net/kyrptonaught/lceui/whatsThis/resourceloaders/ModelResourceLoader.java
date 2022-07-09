package net.kyrptonaught.lceui.whatsThis.resourceloaders;

import net.kyrptonaught.lceui.LCEUIMod;
import net.kyrptonaught.lceui.whatsThis.WhatsThisInit;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

public class ModelResourceLoader {
    public static final Identifier ID = new Identifier(LCEUIMod.MOD_ID, "models/item");

    public static void loadModels(ResourceManager manager, Consumer<Identifier> out) {
        Map<Identifier, Resource> resources = manager.findResources(ID.getPath(), (string) -> string.getPath().endsWith(".json"));
        for (Identifier id : resources.keySet()) {
            if (id.getNamespace().equals(ID.getNamespace())) {
                out.accept(new ModelIdentifier(WhatsThisInit.getCleanIdentifier(id), "inventory"));
            }
        }
    }
}
