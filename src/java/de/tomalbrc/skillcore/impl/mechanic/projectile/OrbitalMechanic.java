package de.tomalbrc.skillcore.impl.mechanic.projectile;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.core.SkillEngine;
import de.tomalbrc.skillcore.impl.gadget.OrbitGadget;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class OrbitalMechanic extends AbstractProjectileMechanic {
    @SerializedName(value = "charges", alternate = {"ch"})
    public int charges;
    @SerializedName(value = "points", alternate = "p")
    public Resolvable<Integer> points = Resolvable.literal(32);
    @SerializedName(value = "radius")
    public Resolvable<Float> radius = Resolvable.literal(4f);

    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        if (targets != null) {
            for (Target target : targets) {
                var t = new OrbitGadget(tree.copyWith(target.getPosition(), List.of(target)), this, target);
                SkillEngine.getInstance().addGadget(t);
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.ORBITAL;
    }
}