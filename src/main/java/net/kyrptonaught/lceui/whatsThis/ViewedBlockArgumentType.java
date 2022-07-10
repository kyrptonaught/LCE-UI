package net.kyrptonaught.lceui.whatsThis;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import java.util.concurrent.CompletableFuture;

public class ViewedBlockArgumentType implements ArgumentType<String> {

    public static ViewedBlockArgumentType viewedBlockArgumentType() {
        return new ViewedBlockArgumentType();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        for (String identifier : WhatsThisInit.descriptionManager.viewedDescriptions) {
            builder.suggest(identifier);
        }
        return builder.buildFuture();
    }

    public static String getViewedBlockArgumentType(CommandContext<FabricClientCommandSource> context, String name) {
        return context.getArgument(name, String.class);
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        int i = reader.getCursor();
        while (reader.canRead() && (Identifier.isCharValid(reader.peek()) || '#' == reader.peek())) {
            reader.skip();
        }
        String string = reader.getString().substring(i, reader.getCursor());
        try {
            return string;
        } catch (InvalidIdentifierException invalidIdentifierException) {
            reader.setCursor(i);
            throw new SimpleCommandExceptionType(new TranslatableText("argument.id.invalid")).createWithContext(reader);
        }
    }
}
