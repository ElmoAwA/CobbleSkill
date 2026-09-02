package de.tomalbrc.skillcore.impl;

import de.tomalbrc.skillcore.SkillCoreComponents;
import de.tomalbrc.skillcore.api.Skill;
import de.tomalbrc.skillcore.api.SkillTrigger;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.core.SkillEngine;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EquipmentTriggerHandler extends TriggerHandler<ServerPlayer> {
    public EquipmentTriggerHandler(@NotNull ServerPlayer parent) {
        super(parent);
    }

    protected List<Skill> getSkills(ItemStack item, SkillTrigger trigger) {
        SkillCoreComponents.Skillset skillset = item.get(SkillCoreComponents.SKILLS);
        if (skillset != null) {
            var set = skillset.skillsForTrigger(trigger);
            if (set != null)
                return set;
        }

        return List.of();
    }

    // Runs equipment skills instead of configured list from mobData
    @Override
    public InteractionResult fireTrigger(SkillTrigger skillTrigger, @Nullable Target triggerer, @Nullable String signal) {
        var consume = false;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            var item = parent.getItemBySlot(slot);
            var skills = getSkills(item, skillTrigger);
            consume |= fireTriggerList(skills, skillTrigger, triggerer, signal).consumesAction();
        }
        return consume ? InteractionResult.CONSUME : InteractionResult.PASS;
    }

    // run skills from single itemstack
    public InteractionResult fireTrigger(SkillTrigger skillTrigger, ItemStack itemStack, @Nullable Target triggerer, @Nullable String signal) {
        List<Skill> list = getSkills(itemStack, skillTrigger);
        if (list == null || list.isEmpty()) return InteractionResult.PASS;

        boolean consume = false;
        for (Skill skill : list) {
            if (skill.signal() == null || skill.signal().equals(signal))
                consume |= SkillEngine.getInstance().runSkill(parent, skillTrigger, triggerer, skill);
        }

        return consume ? InteractionResult.CONSUME : InteractionResult.PASS;
    }

    public InteractionResult onUse(Player player, InteractionHand interactionHand) {
        return this.fireTrigger(SkillTrigger.ON_USE, player.getItemInHand(interactionHand), Target.of(player), null);
    }

    public void onCancelUse(Player player, InteractionHand interactionHand) {
        this.fireTrigger(SkillTrigger.ON_CANCEL_USE, player.getItemInHand(interactionHand), Target.of(player), null);
    }

    public InteractionResult onInteractAsPlayer(ServerPlayer player, Entity entity) {
        return this.fireTrigger(SkillTrigger.ON_INTERACT, Target.of(entity));
    }

    public InteractionResult onItemPickup(ItemEntity item, ItemStack stack) {
        boolean c1 = false;
        if (stack.has(SkillCoreComponents.SKILLS)) {
            c1 = this.fireTrigger(SkillTrigger.ON_PICKUP, stack, Target.of(parent), null).consumesAction();
        }

        var c2 = c1 || this.fireTrigger(SkillTrigger.ON_ITEM_PICKUP, Target.of(parent)).consumesAction();
        return c2 ? InteractionResult.CONSUME : InteractionResult.PASS;
    }

    public InteractionResult onSwap() {
        return this.fireTrigger(SkillTrigger.ON_PRESS_F, Target.of(parent));
    }

    public void onSwing() {
        this.fireTrigger(SkillTrigger.ON_SWING, Target.of(parent));
    }

    public void onJump() {
        this.fireTrigger(SkillTrigger.ON_JUMP, Target.of(parent));
    }

    public void onCrouch() {
        this.fireTrigger(SkillTrigger.ON_CROUCH, Target.of(parent));
    }

    public void onUnCrouch() {
        this.fireTrigger(SkillTrigger.ON_UNCROUCH, Target.of(parent));
    }

    public void onHeld(ItemStack itemStack) {
        this.fireTrigger(SkillTrigger.ON_HOLD, itemStack, Target.of(parent), null);
    }

    public void onUnHeld(ItemStack itemStack) {
        this.fireTrigger(SkillTrigger.ON_UN_HELD, itemStack, Target.of(parent), null);
    }

    public void onEquipItem(ItemStack stack) {
        this.fireTrigger(SkillTrigger.ON_EQUIP, stack, Target.of(parent), null);
    }

    public void onUnEquipItem(ItemStack stack) {
        this.fireTrigger(SkillTrigger.ON_UNEQUIP, stack, Target.of(parent), null);
    }

    public void onRespawn() {
        this.fireTrigger(SkillTrigger.ON_RESPAWN, Target.of(parent), null);
    }

    public void onJoin() {
        this.fireTrigger(SkillTrigger.ON_JOIN, Target.of(parent), null);
    }

//    public void onPlayerKillPlayer(Entity livingEntity) {
//        this.fireTrigger(SkillTrigger.ON_KILL_PLAYER, Target.of(livingEntity));
//    }
}