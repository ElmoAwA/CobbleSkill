package de.tomalbrc.skillcore.api.meta;

import de.tomalbrc.skillcore.impl.variable.Variable;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;

public record MetaSkillInstance(MetaSkill skill, Map<String, Variable> variables) {
    public static MetaSkillInstance create(MetaSkill skill) {
        return new MetaSkillInstance(skill, new Object2ObjectOpenHashMap<>());
    }

    public boolean isWaiting() {
        return false;
    }
}
