package de.tomalbrc.skillcore.impl;

import de.tomalbrc.skillcore.api.Skill;
import de.tomalbrc.skillcore.api.SkillTrigger;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.core.SkillEngine;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TriggerHandler<T extends Entity> {
    final Map<SkillTrigger, List<Skill>> skills = new EnumMap<>(SkillTrigger.class);
    final @NotNull T parent;

    public TriggerHandler(@NotNull T parent) {
        this.parent = parent;
    }

    public void add(Skill skill) {
        this.skills.computeIfAbsent(skill.trigger(), k -> new LinkedList<>()).add(skill);
    }

    public void tick() {
        fireTrigger(SkillTrigger.ON_TIMER, Target.of(parent));
    }

    public InteractionResult fireTrigger(SkillTrigger skillTrigger, @Nullable Target triggerer) {
        return fireTrigger(skillTrigger, triggerer, null);
    }

    protected List<Skill> getSkills(SkillTrigger trigger) {
        return this.skills.get(trigger);
    }

    public InteractionResult fireTrigger(SkillTrigger skillTrigger, @Nullable Target triggerer, @Nullable String signal) {
        List<Skill> list = getSkills(skillTrigger);
        return fireTriggerList(list, skillTrigger, triggerer, signal);
    }

    public InteractionResult fireTriggerList(List<Skill> skills, SkillTrigger skillTrigger, @Nullable Target triggerer, @Nullable String signal) {
        if (skills == null || skills.isEmpty()) return InteractionResult.PASS;

        boolean consume = false;
        for (Skill skill : skills) {
            if (skill.signal() == null || skill.signal().equals(signal))
                consume |= SkillEngine.getInstance().runSkill(parent, skillTrigger, triggerer, skill);
        }

        return consume ? InteractionResult.CONSUME : InteractionResult.PASS;
    }

    public InteractionResult onAttack(ServerLevel serverLevel, Entity entity) {
        return fireTrigger(SkillTrigger.ON_ATTACK, Target.of(entity));
    }

    public void onSignal(@Nullable Entity sender, String signal) {
        fireTrigger(SkillTrigger.ON_SIGNAL, Target.of(sender), signal);
    }

    public void onSpawn() {
        this.fireTrigger(SkillTrigger.ON_SPAWN, Target.of(this.parent));
        this.onSpawnOrLoad();
    }

    public void onDespawn() {
        this.fireTrigger(SkillTrigger.ON_DESPAWN, Target.of(this.parent));
    }

    public void onLoad() {
        this.fireTrigger(SkillTrigger.ON_LOAD, Target.of(this.parent));
        this.onSpawnOrLoad();
    }

    public void onSpawnOrLoad() {
        this.fireTrigger(SkillTrigger.ON_SPAWN_OR_LOAD, Target.of(this.parent));
    }

    public InteractionResult onDeath(DamageSource damageSource) {
        var res = this.fireTrigger(SkillTrigger.ON_DEATH, Target.of(this.parent));
        if (res.consumesAction()) {
            var o = parent.overlay();
            var mobData = o != null ? o.getMobData() : null;
            if (mobData != null)
                this.parent.asLivingEntity().setHealth(Optional.ofNullable(mobData.options() == null ? null : mobData.options().reviveHealth()).orElse(parent.asLivingEntity().getMaxHealth()));
        }


        SkillEngine.getInstance().auraManager().onDeath(this.parent);

        return res;
    }

    public InteractionResult onInteract(ServerPlayer player, InteractionHand hand) {
        return this.fireTrigger(SkillTrigger.ON_INTERACT, Target.of(player));
    }

    public void onBreed(Animal parent) { // TODO: call from event
        this.fireTrigger(SkillTrigger.ON_BREED, Target.of(parent));
    }

    public InteractionResult onChangeTarget(LivingEntity entity) {
        return this.fireTrigger(SkillTrigger.ON_CHANGE_TARGET, Target.of(entity));
    }

    public InteractionResult onDamage(DamageSource damageSource, float amount) {
        return this.fireTrigger(SkillTrigger.ON_DAMAGED, Target.of(damageSource.getEntity()));
    }

    public void onEnterCombat(LivingEntity livingEntity) {
        this.fireTrigger(SkillTrigger.ON_ENTER_COMBAT, Target.of(livingEntity));
    }

    public void onDropCombat(LivingEntity livingEntity) {
        this.fireTrigger(SkillTrigger.ON_DROP_COMBAT, Target.of(livingEntity));
    }

    public void onPlayerKill(Entity livingEntity) {
        this.fireTrigger(SkillTrigger.ON_PLAYER_KILL, Target.of(livingEntity));
    }

    public InteractionResult onTame(Player player) {
        return this.fireTrigger(SkillTrigger.ON_TAME, Target.of(player));
    }

    public InteractionResult onHear(Entity e) {
        return this.fireTrigger(SkillTrigger.ON_HEAR, Target.of(e));
    }

    public InteractionResult onPrime() {
        return this.fireTrigger(SkillTrigger.ON_PRIME, Target.of(parent));
    }

    public InteractionResult onExplode() {
        return this.fireTrigger(SkillTrigger.ON_EXPLODE, Target.of(parent));
    }

    public InteractionResult onCreeperCharge() {
        return this.fireTrigger(SkillTrigger.ON_CREEPER_CHARGE, Target.of(parent));
    }

    public InteractionResult onTeleport() {
        return this.fireTrigger(SkillTrigger.ON_TELEPORT, Target.of(parent));
    }

    public void onTrade(Player tradingPlayer) {
        this.fireTrigger(SkillTrigger.ON_TRADE, Target.of(tradingPlayer));
    }
}