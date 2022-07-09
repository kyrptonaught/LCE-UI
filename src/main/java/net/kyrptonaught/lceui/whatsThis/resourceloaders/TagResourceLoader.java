package net.kyrptonaught.lceui.whatsThis.resourceloaders;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.kyrptonaught.lceui.LCEUIMod;
import net.kyrptonaught.lceui.whatsThis.WhatsThisInit;
import net.minecraft.resource.ResourceManager;
import net.minecraft.tag.TagGroupLoader;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TagResourceLoader implements SimpleSynchronousResourceReloadListener {
    public static final Identifier ID = new Identifier(LCEUIMod.MOD_ID, "tags/descriptions");

    private final TagGroupLoader<Identifier> tagLoader = new TagGroupLoader<>(this::get, getFabricId().getPath());


    private Optional<Identifier> get(Object o) {
        return Optional.of((Identifier) o);
    }

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    public void reload(ResourceManager manager) {
        WhatsThisInit.descriptionManager.clearTags();
        Map<Identifier, List<TagGroupLoader.TrackedEntry>> tags = tagLoader.loadTags(manager);
        WhatsThisInit.descriptionManager.setTags(tagLoader.buildGroup(tags));
    }
}