package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.overlay.EntityOverlay;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.List;

public class LockModelMechanic extends AbstractMechanic {
    @SerializedName(value = "lock", alternate = {"l"})
    boolean lock = true;

    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        if (targets == null || targets.isEmpty()) return ExecutionResult.NULL;

        for (Target target : targets) {
            EntityOverlay<? extends Entity> o = target.getEntity().overlay();
            if (o != null && o.customModels() != null) {
                for (var s : o.customModels()) {
                    s.lockHead = lock; // TODO: lock entity itself?
                    s.lockBody = lock;
                }
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.LOCK_MODEL;
    }
}
