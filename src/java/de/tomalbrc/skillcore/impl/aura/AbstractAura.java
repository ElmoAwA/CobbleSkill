package de.tomalbrc.skillcore.impl.aura;

import de.tomalbrc.skillcore.api.aura.Aura;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.mechanic.aura.AbstractAuraMechanic;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;

import java.util.UUID;

public abstract class AbstractAura<T extends AbstractAuraMechanic> implements Aura {
    public final ServerLevel level;
    public final SkillTree tree;
    public final T mechanic;

    public final Entity caster;
    public final Target target;

    public final String auraName;
    public final String auraType;

    public final int duration;
    public final int interval;
    public boolean cancelled;
    public int ticks = 0;
    public int nextTick = 0;

    public int stacks = 1;
    public final int maxStacks;

    public final boolean refreshDuration;

    public int charges;

    public ServerBossEvent bar;

    public AbstractAura(SkillTree tree, T mechanic, Target target) {
        this.tree = tree;
        this.level = tree.level();
        this.mechanic = mechanic;
        this.caster = tree.caster();
        this.target = target;

        this.auraName = resolveName(mechanic, tree);
        this.auraType = mechanic.auraType.resolve(tree);

        this.duration = mechanic.duration.resolve(tree);
        this.interval = mechanic.interval.resolve(tree);
        this.maxStacks = mechanic.maxStacks.resolve(tree);
        this.refreshDuration = mechanic.refreshDuration;

        this.charges = mechanic.charges;
    }

    private String resolveName(T mechanic, SkillTree tree) {
        String name = mechanic.auraName.resolve(tree);
        return (name == null || name.isEmpty()) ? UUID.randomUUID().toString() : name;
    }

    public void addStack() {
        this.stacks++;
    }

    public void removeStack() {
        this.stacks--;
    }

    public int getStacks() {
        return this.stacks;
    }

    public boolean canStack() {
        return this.maxStacks > 0;
    }

    public int maxStacks() {
        return this.maxStacks;
    }

    public void onStart() {
        mechanic.onStartSkill.cast(this::createTree);
    }

    /**
     *
     * @return true when aura expired
     */
    final public boolean asyncTick() {
        if (target.getEntity().isRemoved() || cancelled)
            return true;

        if (ticks >= duration) return true;

        this.onAsyncTick();

        if (ticks >= nextTick && mechanic.onTickSkill != null) {
            mechanic.onTickSkill.cast(this::createTree);
            nextTick = ticks + interval;
        }

        if (bar != null) {
            float progress = 1f - (ticks / (float) duration);
            bar.setProgress(progress);
        }

        ticks++;
        return false;
    }

    @Override
    public void onEntityHit() {
        // TODO: implement
    }

    @Override
    public void cancel() {
        this.cancelled = true;
    }

    public void onEnd(boolean runEnd) {
        if (bar != null) {
            bar.removeAllPlayers();
            bar = null;
        }

        if (runEnd && mechanic.onEndSkill != null) {
            mechanic.onEndSkill.cast(this::createTree);
        }
    }

    public void createBossBar() {
        if (mechanic.showBar && caster instanceof ServerPlayer sp) {
            bar = new ServerBossEvent(Component.literal(auraName), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS);
            bar.addPlayer(sp);
        }
    }

    protected SkillTree createTree() {
        return new SkillTree(caster, target, target.getPosition());
    }
}
