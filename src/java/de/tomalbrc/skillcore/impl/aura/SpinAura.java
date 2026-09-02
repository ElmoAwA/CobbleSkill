package de.tomalbrc.skillcore.impl.aura;

import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.mechanic.effect.SpinMechanic;

public class SpinAura extends AbstractAura<SpinMechanic> {
    final float velocity;
    private float currentYaw;

    public SpinAura(SkillTree tree, SpinMechanic mechanic, Target target, float vel) {
        super(tree, mechanic, target);
        this.velocity = vel;
    }

    @Override
    public void onAsyncTick() {
        this.currentYaw += velocity;
        SkillCore.SERVER.execute(() -> {
            this.target.getEntity().setYRot(this.currentYaw);
        });
    }
}
