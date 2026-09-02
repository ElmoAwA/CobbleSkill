package de.tomalbrc.skillcore.impl.gadget;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.mechanic.projectile.TotemMechanic;
import net.minecraft.world.entity.Entity;

public class TotemGadget extends AbstractProjectileGadget<TotemMechanic> {
    public TotemGadget(SkillTree tree, TotemMechanic mechanic) {
        super(tree, mechanic, Target.of(tree.caster()));
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
