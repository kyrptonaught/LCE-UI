package net.kyrptonaught.lceui.whatsThis.resourceloaders;

import net.kyrptonaught.lceui.LCEUIMod;
import net.kyrptonaught.lceui.whatsThis.WhatsThisInit;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.function.Consumer;

public class ModelResourceLoader {
    public static final Identifier ID = new Identifier(LCEUIMod.MOD_ID, "models/item");

    public static void loadModels(ResourceManager manager, Consumer<Identifier> out) {
        Collection<Identifier> resources = manager.findResources(ID.getPath(), (string) -> string.endsWith(".json"));
        for (Identifier id : resources) {
            if (id.getNamespace().equals(ID.getNamespace())) {
                out.accept(new ModelIdentifier(WhatsThisInit.getCleanIdentifier(id), "inventory"));
            }
        }
    }
}
