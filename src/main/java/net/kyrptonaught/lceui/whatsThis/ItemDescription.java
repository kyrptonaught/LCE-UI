package net.kyrptonaught.lceui.whatsThis;

import net.minecraft.util.Identifier;

public class ItemDescription {
    String parent;
    String model;

    TextTranslations text = new TextTranslations();

    public static class TextTranslations {
        String name;
        String description;
    }

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
    }
}
