package de.tomalbrc.skillcore.impl.mechanic.projectile;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.core.SkillEngine;
import de.tomalbrc.skillcore.impl.gadget.ProjectileGadget;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ProjectileMechanic extends AbstractProjectileMechanic {
    public @SerializedName(value = "Type", alternate = "subtype") String type;
    public @SerializedName(value = "gravity", alternate = "g") float gravity;
    public @SerializedName(value = "bounces", alternate = "bounce") boolean bounces;
    public @SerializedName(value = "bouncevelocity", alternate = "bv") double bounceVelocity = 0.9;
    public @SerializedName(value = "hugsurface", alternate = "hs") boolean hugSurface;
    public @SerializedName(value = "hugliquid", alternate = {"hugwater", "huglava"}) boolean hugLiquid;
    public @SerializedName(value = "heightfromsurface", alternate = "hfs") float heightFromSurface = 0.5f;
    public @SerializedName(value = "maxclimbheight", alternate = "mch") float maxClimbHeight = 3;
    public @SerializedName(value = "maxdropheight", alternate = "mdh") float maxDropHeight = 10;
    public @SerializedName(value = "highaccuracymode", alternate = "ham") String highAccuracyMode = "PLAYERS_ONLY";

    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        if (targets != null) {
            for (Target target : targets) {
                // TODO: check implementation
//                if (requireLineOfSight && !hasLineOfSight(tree.caster(), target)) {
//                    continue;
//                }

                var t = new ProjectileGadget(tree.copyWith(tree.caster().position().add(0, startYOffset, 0), List.of(target)), this, target);
                SkillEngine.getInstance().addGadget(t);
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.PROJECTILE;
    }
}
