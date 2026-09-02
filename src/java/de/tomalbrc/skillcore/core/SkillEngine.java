package de.tomalbrc.skillcore.core;

import com.google.common.collect.ImmutableList;
import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.api.GlobalStates;
import de.tomalbrc.skillcore.api.Skill;
import de.tomalbrc.skillcore.api.SkillTrigger;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.gadget.Gadget;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.aura.AbstractAura;
import de.tomalbrc.skillcore.impl.mechanic.aura.AbstractAuraMechanic;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

public class SkillEngine {
    static final SkillEngine INSTANCE = new SkillEngine();

    final List<SkillTree> activeTrees = Collections.synchronizedList(new ObjectArrayList<>());
    final List<Gadget> gadgets = new CopyOnWriteArrayList<>();

    final AuraManager auraManager = new AuraManager();

    private SkillEngine() {}

    public static SkillEngine getInstance() {
        return INSTANCE;
    }

    public AuraManager auraManager() {
        return auraManager;
    }

    public void addGadget(Gadget gadget) {
        this.gadgets.add(gadget);
    }

    public InteractionResult submitTree(SkillTree tree) {
        var res = tree.tick();
        if (!tree.isFinished()) {
            activeTrees.add(tree);
        }
        return res;
    }

    public void addTree(SkillTree tree) {
        activeTrees.add(tree);
    }

    public void submitTreeAsync(SkillTree tree) {
        GlobalStates.execute(() -> {
            tree.tick();
            if (!tree.isFinished()) {
                activeTrees.add(tree);
            }
        });
    }

    public boolean runSkill(Entity casterEntity, SkillTrigger skillTrigger, @Nullable Target triggerer, Skill skill) {
        if (skillTrigger == SkillTrigger.ON_TIMER && skill.time() != 0 && casterEntity.tickCount % skill.time() != 0)
            return false;

        if (!skill.canRun(casterEntity))
            return false;

        Supplier<Boolean> supplier = () -> {
            SkillTree tree = new SkillTree(
                    ImmutableList.of(skill),
                    casterEntity,
                    triggerer,
                    casterEntity.position(),
                    ImmutableList.of() // inherited targets are resolved via the targeter
            );

            // resolve targets via the targeter on the new tree
            List<Target> targets = skill.targeter() != null ? skill.targeter().find(tree) : (triggerer == null ? List.of() : List.of(triggerer));
            targets = skill.targeter() != null ? skill.targeter().sort(tree, casterEntity.asLivingEntity() == null || !casterEntity.asLivingEntity().isThreatTableEnabled() ? null : casterEntity.asLivingEntity().getThreatTable(), casterEntity.position(), targets) : targets;
            tree.setCurrentTargets(targets);
            return submitTree(tree).consumesAction();
        };

        if (skill.mechanic().sync()) {
            return supplier.get();
        } else {
            CompletableFuture.supplyAsync(supplier, GlobalStates.executorService()).exceptionally(x -> {
                SkillCore.LOGGER.error(x.getLocalizedMessage());
                x.printStackTrace();
                return null;
            });
        }

        return false;
    }

    public void cancelAllTrees() {
        for (SkillTree tree : activeTrees) tree.cancel();
        activeTrees.clear();
    }

    public void tick(MinecraftServer server) {
        // tick and cleanup active trees
        GlobalStates.execute(() -> {
            synchronized (activeTrees) {
                activeTrees.forEach(SkillTree::tick);
                activeTrees.removeIf(SkillTree::isFinished);
            }

            synchronized (gadgets) {
                gadgets.forEach(Gadget::asyncTick);
                gadgets.removeIf(Gadget::finished);
            }

            auraManager.asyncTick();
        });
    }

    public List<SkillTree> activeTrees() {
        return activeTrees;
    }

    public void onDespawn(Entity entity, ServerLevel serverLevel) {
        auraManager.onDespawn(entity, serverLevel);
    }

    public void addAura(AbstractAura<? extends AbstractAuraMechanic> aura) {
        this.auraManager().add(aura);
    }
}
