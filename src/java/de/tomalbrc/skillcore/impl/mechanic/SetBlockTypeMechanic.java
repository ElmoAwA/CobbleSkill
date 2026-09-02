package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Locale;

public class SetBlockTypeMechanic extends AbstractMechanic {
    @SerializedName(value = "material", alternate = {"m","mat","t","type","types","block","b"})
    Resolvable<String> material = Resolvable.literal("dirt");

    @SerializedName(value = "physics", alternate = {"p"})
    Resolvable<Boolean> physics = Resolvable.literal(true);

    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        if (targets == null || targets.isEmpty()) return ExecutionResult.NULL;

        ServerLevel level = tree.level();

        String rawMaterial = material.resolve(tree).toLowerCase(Locale.ROOT);
        boolean applyPhysics = physics.resolve(tree);

        for (Target t : targets) {
            Entity ent = t.getEntity();
            if (ent == null) continue;

            BlockPos pos = BlockPos.containing(ent.position());

            BlockState state = parseBlockState(level, rawMaterial);
            if (state == null) continue;

            int flags = applyPhysics ? Block.UPDATE_ALL : Block.UPDATE_CLIENTS;

            level.setBlock(pos, state, flags);
        }

        return ExecutionResult.NULL;
    }

    private BlockState parseBlockState(Level level, String input) {
        try {
            var parsed = BlockStateParser.parseForBlock(level.holderLookup(Registries.BLOCK), input, false);
            return parsed.blockState();
        } catch (Exception ignored) {
            return null;
        }
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.SETBLOCKTYPE;
    }
}
