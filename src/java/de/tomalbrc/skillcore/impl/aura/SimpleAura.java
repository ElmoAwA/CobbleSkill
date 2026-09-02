package de.tomalbrc.skillcore.impl.aura;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.mechanic.aura.AuraMechanic;

public class SimpleAura extends AbstractAura<AuraMechanic> {
    public SimpleAura(SkillTree tree, AuraMechanic mechanic, Target target) {
        super(tree, mechanic, target);
    }

    @Override
    public void onAsyncTick() {

    }
}
