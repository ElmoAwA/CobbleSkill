package de.tomalbrc.skillcore.impl.mechanic.variable;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.GlobalStates;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Variable;
import net.minecraft.resources.ResourceLocation;

public class SetVariableMechanic extends AbstractVariableMechanic {
    @SerializedName(value = "t", alternate = "subtype") //MM: "type"
    Variable.Type vartype;
    @SerializedName(value = "value", alternate = {"v", "amount", "a"})
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
                    vars.put(key, Variable.of(value, vartype));
                }
            }
        } else if (vars != null) {
            vars.put(key, Variable.of(value, vartype));
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.SET_VARIABLE;
    }
}
