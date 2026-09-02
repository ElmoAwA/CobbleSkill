package de.tomalbrc.skillcore.api;

import de.tomalbrc.skillcore.api.condition.Condition;
import de.tomalbrc.skillcore.api.mechanic.Mechanic;
import de.tomalbrc.skillcore.api.target.Targeter;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record Skill(
        Mechanic mechanic,
        SkillTrigger trigger,
        int time,
        String signal,
        Targeter targeter,
        @Nullable SkillHealthCondition healthCondition,
        @Nullable Double chance,
        @Nullable List<Condition> conditions
) {
    public boolean canRun(Entity parent) {
        if (healthCondition() != null && !healthCondition().isMet(parent)) return false;
        return chance() == null || !(chance() < 1.0) || !(parent.getRandom().nextDouble() > chance());
    }
}