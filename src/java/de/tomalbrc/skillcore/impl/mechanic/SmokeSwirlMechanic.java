package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.core.SkillEngine;
import de.tomalbrc.skillcore.impl.gadget.AbstractTickingGadget;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.LevelEvent;

import java.util.List;

public class SmokeSwirlMechanic extends AbstractMechanic {
    @SerializedName(value = "duration", alternate = {"d"})
    public int duration = 5;

    @SerializedName(value = "interval", alternate = {"i"})
    public int interval = 1;

    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        if (targets == null || targets.isEmpty()) return ExecutionResult.NULL;

        for (Target t : targets) {
            SkillEngine.getInstance().addGadget(new SmokeSwirlGadget(this, tree, t));
        }

        return ExecutionResult.NULL;
    }

    public static class SmokeSwirlGadget extends AbstractTickingGadget {
        private final Target target;
        private int iteration = 0;

        private static final int[] OFF_X = {1, 1, 0, -1, -1, -1, 0, 1};
        private static final int[] OFF_Z = {0, 1, 1, 1, 0, -1, -1, -1};

        private static final int[] DIR_DATA = {7, 6, 3, 0, 1, 2, 5, 8};

        public SmokeSwirlGadget(SmokeSwirlMechanic mech, SkillTree tree, Target target) {
            super(tree, Math.max(1, mech.duration), Math.max(1, mech.interval));
            this.target = target;
        }

        @Override
        public boolean shouldStop() {
            Entity e = target.getEntity();
            return e != null && !e.isAlive();
        }

        @Override
        public void onAsyncTick() {
            int i = iteration % 8;

            BlockPos origin = target.getBlockPos();
            BlockPos eventPos = origin.offset(OFF_X[i], 0, OFF_Z[i]);

            level().levelEvent(LevelEvent.PARTICLES_SHOOT_SMOKE, eventPos, DIR_DATA[i]);

            iteration++;
        }

        @Override
        public void onHit(Entity entity) {}

        @Override
        public void onEnd() {}
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.SMOKESWIRL;
    }
}