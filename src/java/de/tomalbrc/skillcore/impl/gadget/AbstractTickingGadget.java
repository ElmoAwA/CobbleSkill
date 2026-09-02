package de.tomalbrc.skillcore.impl.gadget;

import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.gadget.Gadget;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import net.minecraft.server.level.ServerLevel;

public abstract class AbstractTickingGadget extends ElementHolder implements Gadget {
    protected final int maxLifetime;
    protected final int interval;
    protected SkillTree initialTree;

    protected int ticks;
    protected boolean finished = false;

    boolean markDestroyed = false;

    public AbstractTickingGadget(SkillTree tree, int maxLifetime, int interval) {
        this.initialTree = tree;
        this.maxLifetime = maxLifetime;
        this.interval = interval;
    }

    public SkillTree tree() {
        return initialTree;
    }

    public boolean shouldStop() {
        return false;
    }

    @Override
    public final void asyncTick() {
        if (interval > 1 && ticks % interval != 0) {
            ticks++;
            return;
        }

        if (shouldStop()) {
            destroy();
        }

        if (markDestroyed || ticks >= maxLifetime) {
            this.destroy();
        } else {
            onAsyncTick();
        }

        ticks++;
    }

    public abstract void onAsyncTick();

    @Override
    public ServerLevel level() {
        return initialTree.level();
    }

    @Override
    public void destroy() {
        onEnd();
        this.markDestroyed = true;
        this.finished = true;
        if (this.getAttachment() != null)
            SkillCore.SERVER.execute(super::destroy);
    }

    @Override
    public boolean finished() {
        return finished;
    }
}
