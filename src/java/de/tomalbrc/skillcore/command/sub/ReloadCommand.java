package de.tomalbrc.skillcore.command.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.api.meta.MetaSkill;
import de.tomalbrc.skillcore.command.SkillCoreCommand;
import de.tomalbrc.skillcore.data.DropTable;
import de.tomalbrc.skillcore.data.ItemData;
import de.tomalbrc.skillcore.data.MobData;
import de.tomalbrc.skillcore.data.RandomSpawnData;
import de.tomalbrc.skillcore.io.JsonResourceLoader;
import de.tomalbrc.skillcore.registry.*;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.io.IOException;
import java.nio.file.Path;

public class ReloadCommand {
    private static final Path ROOT = FabricLoader.getInstance().getConfigDir().resolve("skillcore");
    private static final Path MM = FabricLoader.getInstance().getConfigDir().resolve("mythicmobs");

    public static LiteralCommandNode<CommandSourceStack> register() {
        var node = Commands
                .literal("reload")
                .requires(
                        Permissions.require(SkillCore.MODID + ".command.reload", 2)
                );
        return node.executes(ReloadCommand::execute).build();
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        reload(context.getSource());
        return Command.SINGLE_SUCCESS;
    }

    public static void reload(CommandSourceStack stack) {
        if (stack != null)
            stack.sendSuccess(() -> SkillCoreCommand.message("<gold>Reloading..."), false);

        try {
            reloadPath(ROOT);
            reloadPath(MM);
            if (stack != null)
                stack.sendSuccess(() -> SkillCoreCommand.message("<green>Reloaded!"), false);

        } catch (Exception e) {
            if (stack != null)
                stack.sendFailure(SkillCoreCommand.message("<red>Error during reload!"));

            e.printStackTrace();
            throw new RuntimeException("Could not load skills", e);
        }
    }

    public static void reloadPath(Path root) {
        root.resolve("Skills").toFile().mkdirs();
        root.resolve("Mobs").toFile().mkdirs();
        root.resolve("Items").toFile().mkdirs();
        root.resolve("RandomSpawns").toFile().mkdirs();
        root.resolve("DropTables").toFile().mkdirs();

        try {
            var skills = JsonResourceLoader.loadAll(root.resolve("Skills"), MetaSkill.class);
            skills.forEach(MetaSkillRegistry::register);

            var mobs = JsonResourceLoader.loadAll(root.resolve("Mobs"), MobData.class);
            mobs.forEach(MobRegistry::register);

            var items = JsonResourceLoader.loadAll(root.resolve("Items"), ItemData.class);
            items.forEach(ItemRegistry::register);

            var dropTables = JsonResourceLoader.loadAll(root.resolve("DropTables"), DropTable.class);
            dropTables.forEach(DropTableRegistry::register);

            var randomSpawns = JsonResourceLoader.loadAll(root.resolve("RandomSpawns"), RandomSpawnData.class);
            randomSpawns.forEach(SpawnerRegistry::register);

        } catch (IOException e) {
            throw new RuntimeException("Could not load skills", e);
        }
    }
}
