package de.tomalbrc.skillcore.impl.mechanic.effect;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.core.SkillEngine;
import de.tomalbrc.skillcore.impl.gadget.BloodyScreenGadget;
import de.tomalbrc.skillcore.impl.mechanic.AbstractMechanic;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class BloodyScreenMechanic extends AbstractMechanic {
    @SerializedName(value = "duration", alternate = {"d"})
    Resolvable<Integer> duration = Resolvable.literal(20);
    @SerializedName(value = "cancel", alternate = {"c"})
    Resolvable<Boolean> cancel = Resolvable.literal(false);

    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        if (targets != null) {
            for (Target target : targets) {

                if (target.getEntity() instanceof ServerPlayer serverPlayer) {
                    if (cancel.resolve(tree, target)) {
                        // TODO: remove gadget by entity+class
                    } else {
                        var bs = new BloodyScreenGadget(tree.copy(), duration.resolve(tree, target), 1, serverPlayer);
                        SkillEngine.getInstance().addGadget(bs);
                    }
                }
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.BLOODY_SCREEN;
    }
}