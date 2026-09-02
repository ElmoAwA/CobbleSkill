package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.meta.MetaSkill;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.api.target.Targeter;
import de.tomalbrc.skillcore.registry.MetaSkillRegistry;
import net.minecraft.resources.ResourceLocation;

public class SudoSkillMechanic extends SkillMechanic {
    @SerializedName(value = "setcasterastrigger", alternate = {"cat"})
    boolean setcasterastrigger = false;

    @SerializedName(value = "target", alternate = "t")
    Targeter targeter;

    @Override
    public ExecutionResult execute(SkillTree tree) {
        MetaSkill metaSkill = MetaSkillRegistry.get(skill);

        if (metaSkill == null)
            return ExecutionResult.NULL;

        if (targeter != null) {
            var nt = targeter.find(tree);
            tree = tree.copyWithTargets(nt);
        }

        if (setcasterastrigger) {
            tree = tree.copyWithTrigger(tree.caster());
        }

        for (Target target : tree.getCurrentTargets()) {
            metaSkill.cast(tree.copyCaster(target.getEntity()));
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.SUDOSKILL;
    }
}
