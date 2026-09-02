package de.tomalbrc.skillcore.mixin.ext;

import de.tomalbrc.skillcore.ext.ThreatTracker;
import de.tomalbrc.skillcore.util.ThreatTable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class ThreatTracker_LivingEntityMixin extends Entity implements ThreatTracker {
    @Unique
    private ThreatTable threatTable;
    @Unique
    private boolean threatTableEnabled = false;

    protected ThreatTracker_LivingEntityMixin(net.minecraft.world.entity.EntityType<? extends LivingEntity> entityType, net.minecraft.world.level.Level level) {
        super(entityType, level);
    }

    @Override
    public ThreatTable getThreatTable() {
        if (this.threatTable == null) {
            this.threatTable = new ThreatTable((Mob) (Object) this);
        }
        return this.threatTable;
    }

    @Override
    public boolean isThreatTableEnabled() {
        return this.threatTableEnabled;
    }
    
    @Override
    public void setThreatTableEnabled(boolean enabled) {
        this.threatTableEnabled = enabled;
    }

    @Inject(method = "hurt", at = @At("RETURN"))
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && this.threatTableEnabled && source.getEntity() instanceof LivingEntity attacker) {
            this.getThreatTable().addThreat(attacker, amount);

            LivingEntity top = this.getThreatTable().getTopThreatTarget();
            if (top != null && ((Mob)(Object)this).getTarget() != top) {
                ((Mob)(Object)this).setTarget(top);
            }
        }
    }

    @Inject(method = "serverAiStep", at = @At("TAIL"))
    private void enforceThreatTarget(CallbackInfo ci) {
        if (!this.isThreatTableEnabled() || this.level().isClientSide) return;

        Mob self = (Mob)(Object) this;
        ThreatTable table = getThreatTable();

        table.tick();

        LivingEntity bestTarget = table.getTopThreatTarget();
        LivingEntity currentTarget = self.getTarget();

        if (bestTarget != null && currentTarget != bestTarget) {
            self.setTarget(bestTarget);
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void saveThreatData(CompoundTag compound, CallbackInfo ci) {
        if (this.threatTableEnabled) {
            compound.putBoolean("ThreatTableEnabled", true);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void loadThreatData(CompoundTag compound, CallbackInfo ci) {
        if (compound.contains("ThreatTableEnabled")) {
            this.threatTableEnabled = compound.getBoolean("ThreatTableEnabled");
        }
    }
}