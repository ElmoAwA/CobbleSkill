package de.tomalbrc.skillcore.impl.gadget;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.mechanic.projectile.OrbitalMechanic;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

// TODO: is actually aura
public class OrbitGadget extends AbstractProjectileGadget<OrbitalMechanic> {
    protected Target target;

    public OrbitGadget(SkillTree tree, OrbitalMechanic mechanic, Target target) {
        super(tree, mechanic, target);
        this.target = target;
    }

    @Override
    public void onAsyncTick() {
        float r = mechanic.radius.resolve(initialTree, target);
        int p = mechanic.points.resolve(initialTree, target);
        float step = 360.f / p;

        this.currentPos = target.getPosition().add(new Vec3(r, 0, 0).yRot(ticks * step * Mth.DEG_TO_RAD));

        super.onAsyncTick();
    }

    @Override
    public void onHit(Entity entity) {
        if (hits > mechanic.charges) {
            this.destroy();
            return;
        }

        super.onHit(entity);
    }
}
