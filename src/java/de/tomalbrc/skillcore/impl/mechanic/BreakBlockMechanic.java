package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class BreakBlockMechanic extends AbstractMechanic {

    @SerializedName(value = "dodrops", alternate = {"drops", "d"})
    protected boolean doDrops = true;

    @SerializedName(value = "doeffect", alternate = {"effect", "e"})
    protected boolean doEffect = true;

    @SerializedName(value = "usetool", alternate = {"tool", "t"})
    protected boolean useTool = true;

    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        if (targets == null || targets.isEmpty()) return ExecutionResult.NULL;

        ServerLevel level = tree.level();

        for (Target target : targets) {
            Vec3 pos = target.getPosition();
            BlockPos blockPos = BlockPos.containing(pos.x, pos.y, pos.z);
            doBreak(level, blockPos, target.getEntity());
        }

        return ExecutionResult.NULL;
    }

    protected void doBreak(ServerLevel level, BlockPos pos, Entity entity) {
        BlockState state = level.getBlockState(pos);

        if (doEffect) {
            level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(state));
        }

        if (doDrops) {
            if (useTool && entity instanceof ServerPlayer player) {
                ItemStack tool = player.getMainHandItem();
                state.getBlock().playerDestroy(level, player, pos, state, level.getBlockEntity(pos), tool);
            } else {
                LootParams.Builder builder = new LootParams.Builder(level).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos));
                builder.withOptionalParameter(LootContextParams.THIS_ENTITY, entity);

                if (entity instanceof ServerPlayer player) {
                    builder.withOptionalParameter(LootContextParams.TOOL, player.getMainHandItem());
                }

                List<ItemStack> drops = state.getDrops(builder);
                for (ItemStack stack : drops) {
                    Block.popResource(level, pos, stack);
                }

                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            }
        } else {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.BREAK_BLOCK;
    }
}
