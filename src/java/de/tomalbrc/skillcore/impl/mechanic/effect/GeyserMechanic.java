package de.tomalbrc.skillcore.impl.mechanic.effect;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.core.SkillEngine;
import de.tomalbrc.skillcore.impl.gadget.GeyserGadget;
import de.tomalbrc.skillcore.impl.mechanic.AbstractMechanic;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class GeyserMechanic extends AbstractMechanic {
    final static String WATER = "WATER";
    final static String LAVA = "LAVA";

    public @SerializedName(value = "t", alternate = "subtype") String t = WATER;
    public @SerializedName(value = "interval", alternate = {"i", "speed", "s"}) int interval = 10;
    public @SerializedName(value = "duration", alternate = {"maxduration", "md", "d"}) Integer duration = 3;
    public @SerializedName(value = "height", alternate = {"h"}) Integer height = 3;

    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        if (targets != null) {
            for (Target target : targets) {
                var t = new GeyserGadget(
                        tree.copyWith(target.getPosition(), List.of(target)),
                        this.height,
                        this.interval,
                        this.duration,
                        LAVA.equalsIgnoreCase(this.t)
                );

                SkillEngine.getInstance().addGadget(t);
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.GEYSER;
    }
}