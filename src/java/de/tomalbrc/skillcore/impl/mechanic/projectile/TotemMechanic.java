package de.tomalbrc.skillcore.impl.mechanic.projectile;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.core.SkillEngine;
import de.tomalbrc.skillcore.impl.gadget.TotemGadget;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class TotemMechanic extends AbstractProjectileMechanic {
    @SerializedName(value = "charges", alternate = {"ch"})
    public int charges;

    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        if (targets != null) {
            for (Target target : targets) {
                var t = new TotemGadget(tree.copyWith(target.getPosition(), List.of(target)), this);
                SkillEngine.getInstance().addGadget(t);
            }
        }

        return ExecutionResult.NULL;
    }


    @Override
    public ResourceLocation id() {
        return Mechanics.TOTEM;
    }
}