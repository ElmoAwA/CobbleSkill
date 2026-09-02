package de.tomalbrc.skillcore.mixin.trigger;

import de.tomalbrc.skillcore.SkillCoreComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "doHurtTarget", at = @At("HEAD"), cancellable = true)
    private void sc$onAttack(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (this.overlay() != null) {
            if (Objects.requireNonNull(this.overlay()).getTriggerHandler().onAttack((ServerLevel) entity.level(), entity).consumesAction())
                cir.setReturnValue(false);
        }
    }

    @Inject(method = "stopUsingItem", at = @At("HEAD"))
    private void sc$onStopUsing(CallbackInfo ci) {
        if ((Object)this instanceof Player player && player.overlay() != null) {
            var o = player.playerOverlay();
            if (o != null)
                o.getTriggerHandler().onCancelUse(player, player.getUsedItemHand());
        }
    }

    @Inject(method = "onEquipItem", at = @At("HEAD"))
    private void sc$onEquip(EquipmentSlot equipmentSlot, ItemStack oldItem, ItemStack newItem, CallbackInfo ci) {
        if ((Object)this instanceof Player player && player.overlay() != null && equipmentSlot.isArmor() && !ItemStack.isSameItemSameComponents(oldItem, newItem)) {
            var o = player.playerOverlay();
            if (o != null) {
                if (oldItem.has(SkillCoreComponents.SKILLS)) {
                    o.getTriggerHandler().onUnEquipItem(oldItem);
                }
                if (newItem.has(SkillCoreComponents.SKILLS)) {
                    o.getTriggerHandler().onEquipItem(newItem);
                }
            }
        }
    }
}
