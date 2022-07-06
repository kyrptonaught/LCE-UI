package net.kyrptonaught.lceui.whatsThis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Objects;

public class ResourceLoader implements SimpleSynchronousResourceReloadListener {
    public static final Identifier ID = new Identifier("lceui", "descriptions");
    private static final Gson GSON = (new GsonBuilder()).create();

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    public void reload(ResourceManager manager) {
        WhatsThisInit.blockDescriptions.clear();
        Collection<Identifier> resources = manager.findResources(ID.getPath() + "/block", (string) -> string.endsWith(".json"));
        for (Identifier id : resources) {
            if (id.getNamespace().equals(ID.getNamespace()))
                try {
                    JsonObject jsonObj = (JsonObject) JsonParser.parseReader(new InputStreamReader(manager.getResource(id).getInputStream()));
                    BlockDescription blockDescription = GSON.fromJson(jsonObj, BlockDescription.class);
                    String fileName = id.getPath().substring(id.getPath().lastIndexOf("/") + 1).replace(".json", "");
                    Identifier name = new Identifier(fileName);
                    WhatsThisInit.blockDescriptions.put(name, blockDescription);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}