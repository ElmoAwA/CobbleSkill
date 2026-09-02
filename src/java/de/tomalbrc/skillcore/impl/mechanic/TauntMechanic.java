package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class TauntMechanic extends AbstractMechanic {
    enum Mode {
        @SerializedName("add") ADD,
        @SerializedName("remove") REMOVE,
        @SerializedName("multiply") MULTIPLY,
        @SerializedName("divide") DIVIDE,
        @SerializedName("set") SET,
        @SerializedName(value = "reset", alternate = "delete") RESET,
        @SerializedName(value = "forcetop", alternate = {"force", "top", "topthreat", "taunt"}) FORCETOP
    }

    @SerializedName(value = "amount", alternate = "a")
    Resolvable<Double> amount = Resolvable.literal(1.);

    Mode mode = Mode.ADD;

    @Override
    public ExecutionResult execute(SkillTree tree) {
        if (tree.caster().asLivingEntity() == null || !tree.caster().asLivingEntity().isThreatTableEnabled()) {
            return ExecutionResult.NULL;
        }

        for (Target target : tree.getCurrentTargets()) {
            if (target.isEntity() && target.getEntity().asLivingEntity() != null) {
                this.apply(tree.caster().asLivingEntity(), target.getEntity().asLivingEntity(), this.amount.resolve(tree, target));
            }
        }

        return ExecutionResult.NULL;
    }

    protected void apply(LivingEntity mob, LivingEntity target, double amount) {
        switch (mode) {
            case ADD -> mob.getThreatTable().addThreat(target, amount);
            case REMOVE -> mob.getThreatTable().setThreat(target, mob.getThreatTable().getThreat(target) - amount);
            case MULTIPLY -> mob.getThreatTable().setThreat(target, mob.getThreatTable().getThreat(target) * amount);
            case DIVIDE -> mob.getThreatTable().setThreat(target, mob.getThreatTable().getThreat(target) / amount);
            case SET -> mob.getThreatTable().setThreat(target, amount);
            case RESET -> mob.getThreatTable().remove(target);
            case FORCETOP -> mob.getThreatTable().setThreat(target, mob.getThreatTable().getThreat(mob.getThreatTable().getTopThreatTarget()) * 1.11);
        }
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.TAUNT;
    }
}