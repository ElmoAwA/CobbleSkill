package de.tomalbrc.skillcore.api.execution;

import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.api.Skill;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.core.SkillEngine;
import de.tomalbrc.skillcore.impl.variable.Variable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SkillTree {
    protected final ServerLevel level;
    protected final Entity caster;
    protected final Target trigger;
    protected final Vec3 origin;
    protected final Map<String, Variable> vars = new HashMap<>(); // skill-scoped vars
    protected final Deque<SkillExecution> stack = new ArrayDeque<>();
    protected List<Target> currentTargets; // inherited targets

    protected boolean cancelled = false;
    protected int delayRemaining = 0;

    public SkillTree(Entity caster, Target trigger, Vec3 origin) {
        this.level = (ServerLevel) caster.level();
        this.caster = caster;
        this.trigger = trigger;
        this.origin = origin;
        this.currentTargets = trigger == null ? List.of() : List.of(trigger);
    }

    public SkillTree(List<Skill> skills, Entity caster, Target trigger, Vec3 origin, List<Target> inheritedTargets) {
        this.level = (ServerLevel) caster.level();
        this.caster = caster;
        this.trigger = trigger;
        this.origin = origin;
        this.currentTargets = inheritedTargets == null ? List.of() : new ArrayList<>(inheritedTargets);

        for (Skill skill : skills) {
            stack.push(new SkillExecution(skill));
        }
    }


    public SkillTree copy() {
        return new SkillTree(List.of(), caster, trigger, origin, currentTargets);
    }

    public SkillTree copyWithOrigin(Vec3 origin) {
        return new SkillTree(List.of(), caster, trigger, origin, currentTargets);
    }

    public SkillTree copyWith(Vec3 newOrigin, List<Target> targets) {
        return new SkillTree(List.of(), caster, trigger, newOrigin, targets);
    }

    public SkillTree copyWith(Skill skill) {
        return new SkillTree(List.of(skill), caster, trigger, origin, currentTargets);
    }

    public SkillTree copyWith(List<Skill> skills) {
        return new SkillTree(skills, caster, trigger, origin, currentTargets);
    }

    public SkillTree copyWith(List<Skill> skills, List<Target> targets) {
        return new SkillTree(skills, caster, trigger, origin, targets);
    }

    public SkillTree copyWithTargets(List<Target> targets) {
        return new SkillTree(List.of(), caster, trigger, origin, targets);
    }

    public SkillTree copyCaster(Entity caster) {
        return new SkillTree(List.of(), caster, trigger, origin, currentTargets);
    }

    public SkillTree copyWithTrigger(Entity triggerEntity) {
        return new SkillTree(List.of(), caster, Target.of(triggerEntity), origin, currentTargets);
    }

    public boolean isFinished() {
        return stack.isEmpty() || cancelled;
    }

    public InteractionResult tick() {
        if (cancelled) return InteractionResult.CONSUME;

        // skill-list delay
        if (delayRemaining > 0) {
            delayRemaining--;
            return InteractionResult.PASS;
        }

        return runLoop();
    }

    public InteractionResult runLoop() {
        InteractionResult interactionResult = InteractionResult.PASS;

        while (!stack.isEmpty()) {
//            if (caster.isRemoved()) {
//                cancel();
//                return InteractionResult.PASS;
//            }

            SkillExecution execution = stack.peekLast();

            if (execution.delayRemaining > 0) {
                execution.delayRemaining--;

                if (stack.size() > 1) {
                    var t = this.copy();
                    t.stack.push(stack.pollLast());
                    SkillEngine.getInstance().addTree(t);
                }

                return InteractionResult.PASS;
            }

            List<Target> targets = currentTargets;
            if (execution.skill.targeter() != null) {
                if (execution.targetInterval > 0) {
                    if (execution.targetIntervalRemaining <= 0) {
                        targets = execution.skill.targeter().find(this);
                        execution.targetIntervalRemaining = execution.targetInterval;
                    } else {
                        execution.targetIntervalRemaining--;
                        targets = getCurrentTargets();
                    }
                } else {
                    targets = execution.skill.targeter().find(this);
                }

                targets = execution.skill.targeter().sort(this, caster.asLivingEntity() == null || !caster.asLivingEntity().isThreatTableEnabled() ? null : caster.asLivingEntity().getThreatTable(), caster.position(), targets);
            }

            var conditions = execution.skill.conditions();
            if (conditions != null && !conditions.stream().allMatch(x -> x.testWithTrigger(this, x.testTrigger() ? trigger : Target.of(caster)))) {
                stack.pollLast();
                continue;
            }

            if (!execution.skill.canRun(caster)) {
                stack.pollLast();
                continue;
            }

            ExecutionResult result = execution.skill.mechanic().execute(this.copyWithTargets(targets));
            var r = result.result();
            if (r.consumesAction()) {
                interactionResult = r;
            }

            if (result.delay() > 0) {
                this.delayRemaining = result.delay();
                stack.pollLast();
                return interactionResult.consumesAction() ? interactionResult : result.result();
            }

            if (execution.repeatsRemaining > 0) {
                execution.repeatsRemaining--;

                SkillTree fork = this.copyWithTargets(currentTargets);
                fork.stack.push(execution);
                fork.delayRemaining = execution.repeatInterval;
                SkillEngine.getInstance().addTree(fork);
            }

            stack.pollLast();
        }

        return interactionResult;
    }

    public void setCurrentTargets(List<Target> newTargets) {
        this.currentTargets = newTargets;
    }

    public List<Target> getCurrentTargets() { return currentTargets; }

    public void cancel() { cancelled = true; }

    public boolean isCancelled() {
        return cancelled;
    }

    public List<Entity> getNearbyEntities(double radius) {
        AABB box = AABB.unitCubeFromLowerCorner(caster().position()).inflate(radius);
        try {
            return CompletableFuture.supplyAsync(() -> caster.level().getEntities(null, box), SkillCore.SERVER).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Entity> getNearbyEntities(Vec3 origin, double radius) {
        AABB box = AABB.unitCubeFromLowerCorner(origin).inflate(radius);
        try {
            return CompletableFuture.supplyAsync(() -> caster.level().getEntities(null, box), SkillCore.SERVER).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public Entity caster() {
        return caster;
    }

    public Map<String, Variable> vars() {
        return this.vars;
    }

    public ServerLevel level() {
        return level;
    }

    public Vec3 origin() {
        return origin;
    }

    public Target trigger() {
        return trigger;
    }
}