package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

import java.util.List;

public class SetAiMechanic extends AbstractMechanic {
    @SerializedName(value = "ai", alternate = {"state", "value"})
    Resolvable<Boolean> ai = Resolvable.literal(false);

    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        if (targets != null) {
            for (Target target : targets) {
                if (target.getEntity() instanceof Mob mob) {
                    var a = ai.resolve(tree, target);
                    SkillCore.SERVER.execute(() -> {
                        mob.setNoAi(!a);
                    });
                }
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.SETAI;
    }
}