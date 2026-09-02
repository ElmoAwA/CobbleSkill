package de.tomalbrc.skillcore.api.meta;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.GlobalStates;
import de.tomalbrc.skillcore.api.Skill;
import de.tomalbrc.skillcore.api.condition.Condition;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.core.SkillEngine;
import net.minecraft.world.InteractionResult;

import java.util.ArrayList;
import java.util.List;

public record MetaSkill(
        String identifier,
        @SerializedName("CancelIfNoTargets") Boolean cancelIfNoTargets,
        @SerializedName("Conditions") List<Condition> conditions,
        @SerializedName("TargetConditions") List<Condition> targetConditions,
        @SerializedName("TriggerConditions") List<Condition> triggerConditions,
        @SerializedName("FailedConditionsSkill") Skill failedConditionsSkill,
        @SerializedName("Cooldown") int cooldown,
        @SerializedName("OnCooldownSkill") Skill onCooldownSkill,
        @SerializedName("Skill") Skill skill,
        @SerializedName("Skills") List<Skill> skills
) {
    public void castAsync(SkillTree tree) {
        GlobalStates.execute(() -> cast(tree));
    }

    public ExecutionResult cast(SkillTree tree) {
        List<Skill> skillList;

        if (tree.caster().overlay().isOnCooldown(identifier)) {
            if (this.onCooldownSkill() != null) {
                SkillEngine.getInstance().submitTreeAsync(tree.copyWith(List.of(this.onCooldownSkill)));
            }
        } else {
            if (this.skill != null) {
                SkillEngine.getInstance().submitTreeAsync(tree.copyWith(List.of(this.skill)));
            }

            tree.caster().overlay().setCooldown(identifier, cooldown*20);

            final Target casterAsTarget = Target.of(tree.caster());
            boolean success = this.conditions() == null || this.conditions().stream().allMatch(x -> x.testWithTrigger(tree, casterAsTarget));
            success = success && (this.triggerConditions() == null || this.triggerConditions().stream().allMatch(x -> x.testWithTrigger(tree, tree.trigger())));

            if (!success && this.failedConditionsSkill() != null) {
                SkillEngine.getInstance().submitTreeAsync(tree.copyWith(List.of(this.failedConditionsSkill)));
            } else if (success) {
                List<Target> targets = applyTargetConditions(this.targetConditions, tree.getCurrentTargets(), tree);
                boolean targetPass = (cancelIfNoTargets == null || cancelIfNoTargets) && !targets.isEmpty();
                if (targetPass) {
                    skillList = this.skills();

                    InteractionResult res = InteractionResult.PASS;
                    if (skillList != null)
                        res = SkillEngine.getInstance().submitTree(tree.copyWith(skillList));

                    if (res.consumesAction())
                        return ExecutionResult.CONSUME;
                }
            }
        }

        return ExecutionResult.NULL;
    }

    public List<Target> applyTargetConditions(List<Condition> targetConditions, List<Target> inputTargets, SkillTree tree) {
        if (targetConditions == null || targetConditions.isEmpty()) return inputTargets == null ? List.of() : inputTargets;
        if (inputTargets == null || inputTargets.isEmpty()) return List.of();

        List<Target> out = new ArrayList<>();
        for (Target t : inputTargets) {
            boolean success = true;
            for (Condition cond : targetConditions) {
                if (!cond.testWithTrigger(tree, t)) {
                    success = false;
                    break;
                }
            }
            if (success) out.add(t);
        }

        return out;
    }
}
