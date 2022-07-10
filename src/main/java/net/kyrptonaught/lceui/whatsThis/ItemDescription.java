package net.kyrptonaught.lceui.whatsThis;

import net.minecraft.util.Identifier;

public class ItemDescription {
    String parent;
    String model;
    String group;
    Boolean displaysicon;

    TextTranslations text = new TextTranslations();

    boolean initialized = false;

    public boolean isFieldBlank(String field) {
        return field == null || field.isEmpty() || field.isBlank();
    }

    public Identifier getParent() {
        return new Identifier(parent);
    }

    public void copyFrom(ItemDescription other) {
        if (isFieldBlank(model))
            model = other.model;

        if (isFieldBlank(text.name))
            text.name = other.text.name;

        if (isFieldBlank(text.description))
            text.description = other.text.description;

        if (isFieldBlank(group))
            group = other.group;

        if (displaysicon == null)
            displaysicon = other.displaysicon;
    }

    public static class TextTranslations {
        String name;
        String description;
    }
}
