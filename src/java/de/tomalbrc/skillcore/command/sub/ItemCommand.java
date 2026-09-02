package de.tomalbrc.skillcore.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.command.SkillCoreCommand;
import de.tomalbrc.skillcore.registry.ItemRegistry;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.world.entity.item.ItemEntity;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ItemCommand {
    public static LiteralCommandNode<CommandSourceStack> register() {
        var node = literal("item")
                .requires(Permissions.require(SkillCore.MODID + ".command.item", 2))
                .then(argument("id", StringArgumentType.word()).suggests((x, builder) -> SharedSuggestionProvider.suggest(ItemRegistry.all().keySet(), builder)).executes(ItemCommand::execute));

        return node.build();
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        try {
            var id = StringArgumentType.getString(context, "id");
            var data = ItemRegistry.get(id);
            var pos = context.getSource().getPosition();
            var level = context.getSource().getLevel();


            var item = data.asItemStack();
            ItemEntity itemEntity = new ItemEntity(level, pos.x, pos.y + 0.25, pos.z, item);
            itemEntity.setDefaultPickUpDelay();
            level.addFreshEntity(itemEntity);

            context.getSource().sendSuccess(() -> SkillCoreCommand.message("1x " + id), false);

            return Command.SINGLE_SUCCESS;
        } catch (Exception e) {
            context.getSource().sendFailure(SkillCoreCommand.message("Could not get item"));
            throw new RuntimeException(e);
        }
    }
}
