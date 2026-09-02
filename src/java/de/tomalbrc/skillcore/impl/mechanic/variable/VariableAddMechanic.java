package de.tomalbrc.skillcore.impl.mechanic.variable;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.GlobalStates;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Variable;
import net.minecraft.resources.ResourceLocation;

public class VariableAddMechanic extends AbstractVariableMechanic {
    @SerializedName(value = "amount", alternate = {"a", "value", "val", "v"})
    Object value;

    @Override
    public ExecutionResult execute(SkillTree tree) {
        var vars = switch (scope) {
            case GLOBAL -> GlobalStates.getGlobalVariables();
            case WORLD -> GlobalStates.getWorldVariables(tree.level().dimension());
            case SKILL -> tree.vars();
            case CASTER -> tree.caster().getVariables();
            case TARGET -> null;
        };

        if (scope == Variable.Scope.TARGET && tree.getCurrentTargets() != null) {
            for (Target target : tree.getCurrentTargets()) {
                if (target.isEntity()) {
                    vars = target.getEntity().getVariables();
                    var v = vars.get(key);
                    v = v.add(Variable.of(value, v.type()));
                    vars.put(key, v);
                }
            }
        } else if (vars != null) {
            var v = vars.get(key);
            if (v != null) {
                v = v.add(Variable.of(value, v.type()));
                vars.put(key, v);
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.VARIABLE_ADD;
    }
}
