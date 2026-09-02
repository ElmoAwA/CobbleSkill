package de.tomalbrc.skillcore.command;

import com.mojang.brigadier.CommandDispatcher;
import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.command.sub.ItemCommand;
import de.tomalbrc.skillcore.command.sub.ReloadCommand;
import de.tomalbrc.skillcore.command.sub.SpawnCommand;
import de.tomalbrc.skillcore.registry.DropTableRegistry;
import de.tomalbrc.skillcore.registry.ItemRegistry;
import de.tomalbrc.skillcore.registry.MetaSkillRegistry;
import de.tomalbrc.skillcore.registry.MobRegistry;
import de.tomalbrc.skillcore.util.TextUtil;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.Person;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.stream.Collectors;

public class SkillCoreCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) -> {
            register(dispatcher);
        });
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var rootNode = Commands
                .literal(SkillCore.MODID).requires(Permissions.require(SkillCore.MODID + ".command", 2))
                .executes(ctx -> {
                    var meta = FabricLoader.getInstance().getModContainer(SkillCore.MODID).orElseThrow().getMetadata();

                    ctx.getSource().sendSuccess(() -> message(meta.getName() + " " + meta.getVersion().getFriendlyString() + " by " + meta.getAuthors().stream().map(Person::getName).collect(Collectors.joining())), false);

                    ctx.getSource().sendSuccess(() -> message("Skills: " + MetaSkillRegistry.all().size()), false);
                    ctx.getSource().sendSuccess(() -> message("Mobs: " + MobRegistry.all().size()), false);
                    ctx.getSource().sendSuccess(() -> message("Items: " + ItemRegistry.all().size()), false);
                    ctx.getSource().sendSuccess(() -> message("DropTables: " + DropTableRegistry.all().size()), false);

                    return 0;
                });

        rootNode.then(ItemCommand.register());
        rootNode.then(SpawnCommand.register());
        rootNode.then(ReloadCommand.register());

        dispatcher.register(rootNode);
    }

    public static Component message(String message) {
        return TextUtil.formatText("<gradient:#2980B9:#6DD5FA>[SkillCore]</gradient> " + message);
    }
}
