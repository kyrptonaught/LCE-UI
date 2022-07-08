package net.kyrptonaught.lceui.whatsThis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.function.Consumer;

public class ResourceLoader implements SimpleSynchronousResourceReloadListener {
    public static final Identifier ID = new Identifier("lceui", "descriptions");
    private static final Gson GSON = (new GsonBuilder()).create();

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    public void reload(ResourceManager manager) {
        WhatsThisInit.itemDescriptions.clear();
        Collection<Identifier> resources = manager.findResources(ID.getPath(), (string) -> string.endsWith(".json"));
        for (Identifier id : resources) {
            if (id.getNamespace().equals(ID.getNamespace()) && id.getPath().contains("/block") || id.getPath().contains("/item"))
                try {
                    JsonObject jsonObj = (JsonObject) JsonParser.parseReader(new InputStreamReader(manager.getResource(id).getInputStream()));
                    ItemDescription itemDescription = GSON.fromJson(jsonObj, ItemDescription.class);
                    String fileName = id.getPath().substring(id.getPath().lastIndexOf("/") + 1).replace(".json", "");
                    if (id.getPath().contains("/block"))
                        fileName = "block/" + fileName;
                    else if (id.getPath().contains("/item"))
                        fileName = "item/" + fileName;

                    Identifier name = new Identifier(fileName);
                    WhatsThisInit.itemDescriptions.put(name, itemDescription);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public static void loadModels(ResourceManager manager, Consumer<Identifier> out) {
        Collection<Identifier> resources = manager.findResources("models/item", (string) -> string.endsWith(".json"));
        for (Identifier id : resources) {
            if (id.getNamespace().equals(ID.getNamespace())) {
                out.accept(new ModelIdentifier(WhatsThisInit.getCleanIdentifier(id), "inventory"));
            }
        }
    }
}