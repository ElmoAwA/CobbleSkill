package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.data.MobData;
import de.tomalbrc.skillcore.mixin.accessor.MobAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.PathfinderMob;

public class RunAiGoalSelectorMechanic extends AbstractMechanic {
    @SerializedName(value = "goal", alternate = {"aigoalselector", "g", "goalselector", "s", "string", "t", "target"})
    String goal;

    @Override
    public ExecutionResult execute(SkillTree tree) {
        var t = tree.getCurrentTargets();
        if (t != null) {
            for (Target target : t) {
                SkillCore.SERVER.execute(() -> {
                    if (target.getEntity() instanceof PathfinderMob mob) {
                        var goalSelector = ((MobAccessor)mob).getGoalSelector();
                        var rgoal = MobData.getGoal(mob, goal);
                        if (rgoal != null) {
                            goalSelector.addGoal(3, rgoal);
                        }
                    }
                });
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.RUN_AI_GOAL_SELECTOR;
    }
}