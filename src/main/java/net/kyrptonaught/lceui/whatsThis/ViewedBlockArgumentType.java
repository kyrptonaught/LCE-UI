package net.kyrptonaught.lceui.whatsThis;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class ViewedBlockArgumentType extends IdentifierArgumentType {
    public static ViewedBlockArgumentType viewedBlockArgumentType() {
        return new ViewedBlockArgumentType();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        for (String identifier : WhatsThisInit.viewedBlocks) {
            builder.suggest(identifier);
        }
        return builder.buildFuture();
    }

    public static Identifier getViewedBlockArgumentType(CommandContext<FabricClientCommandSource> context, String name) {
        return context.getArgument(name, Identifier.class);
    }
}
