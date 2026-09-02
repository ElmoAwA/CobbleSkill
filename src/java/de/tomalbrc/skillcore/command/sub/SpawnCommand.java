package de.tomalbrc.skillcore.command.sub;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.command.SkillCoreCommand;
import de.tomalbrc.skillcore.registry.MobRegistry;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class SpawnCommand {
    public static LiteralCommandNode<CommandSourceStack> register() {
        var node = literal("spawn")
                .requires(Permissions.require(SkillCore.MODID + ".command.spawn", 2))
                .then(argument("id", StringArgumentType.word()).suggests((x, builder) -> SharedSuggestionProvider.suggest(MobRegistry.all().keySet(), builder)).executes(SpawnCommand::execute));

        return node.build();
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        try {
            var id = StringArgumentType.getString(context, "id");
            var data = MobRegistry.get(id);
            var pos = context.getSource().getPosition();
            var level = context.getSource().getLevel();
            var entity = data.spawn(level, pos);

            context.getSource().sendSuccess(() -> SkillCoreCommand.message("<green>Spawned " + id), false);

            return entity == null ? 0 : 1;
        } catch (Exception e) {
            context.getSource().sendFailure(SkillCoreCommand.message("<red>Could not spawn entity"));
            throw new RuntimeException(e);
        }
    }
}
