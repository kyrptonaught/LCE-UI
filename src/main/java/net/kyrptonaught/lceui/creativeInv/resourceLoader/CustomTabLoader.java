package net.kyrptonaught.lceui.creativeInv.resourceLoader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.kyrptonaught.lceui.LCEUIMod;
import net.kyrptonaught.lceui.creativeInv.CreativeInvInit;
import net.kyrptonaught.lceui.creativeInv.CustomItemGroup;
import net.kyrptonaught.lceui.whatsThis.WhatsThisInit;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;

public class CustomTabLoader implements SimpleSynchronousResourceReloadListener {
    public static final Identifier ID = new Identifier(LCEUIMod.MOD_ID, "creativetabs");
    private static final Gson GSON = (new GsonBuilder()).create();

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    public void reload(ResourceManager manager) {
        CreativeInvInit.clearGroups();
        Collection<Identifier> resources = manager.findResources(ID.getPath(), (string) -> string.endsWith(".json"));
        for (Identifier id : resources) {
            if (id.getNamespace().equals(ID.getNamespace()))
                try {
                    JsonObject jsonObj = (JsonObject) JsonParser.parseReader(new InputStreamReader(manager.getResource(id).getInputStream()));
                    CustomItemGroup.LoadedCustomGroup customTabGroup = GSON.fromJson(jsonObj, CustomItemGroup.LoadedCustomGroup.class);
                    CreativeInvInit.addCustomTabGroup(customTabGroup);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}