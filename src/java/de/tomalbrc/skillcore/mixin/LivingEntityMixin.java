package de.tomalbrc.skillcore.mixin;

import com.cobblemon.mod.common.battles.ai.strongBattleAI.AIUtility;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.tomalbrc.skillcore.registry.MobRegistry;
import de.tomalbrc.skillcore.util.DamageMappingUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow
    public abstract void heal(float f);

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    private void sc$invul(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (sc$isInvulnerable(damageSource))
            cir.setReturnValue(true);
    }

    @WrapOperation(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V"))
    private void sc$damageModifier(LivingEntity instance, DamageSource damageSource, float f, Operation<Void> original) {
        var newD = sc$modifyDamage(damageSource, f);
        if (newD >= 0) original.call(instance, damageSource, newD);
    }

    @Unique float sc$modifyDamage(DamageSource source, float d) {
        if (mobId() != null) {
            var mob = MobRegistry.getOptional(mobId());
            if (mob.isPresent() && mob.get().damageModifier() != null) {
                for (String s : mob.get().damageModifier()) {
                    var split = s.split(" ");
                    var id = split[0];
                    float val = (float) (Double.parseDouble(split[1]) * getPower());
                    if (DamageMappingUtil.matches(source, id)) {
                        if (val > 0)
                            d *= val;
                        if (val <= 0) {
                            heal(d*val);
                            d *= val;
                        }
                        break;
                    }
                }
            }

            if (mob.isPresent() && mob.get().elementalModifier() && (Object)this instanceof PokemonEntity pokemon && source.getEntity() instanceof PokemonEntity attacker) {
                var a1 = AIUtility.INSTANCE.getDamageMultiplier(attacker.getPokemon().getPrimaryType(), pokemon.getPokemon().getPrimaryType());
                var a2 = AIUtility.INSTANCE.getDamageMultiplier(attacker.getPokemon().getSecondaryType(), pokemon.getPokemon().getSecondaryType());
                var r = (a1+a2) / 2.0;

                d *= (float) r;
            }
        }

        return d;
    }

    @Unique boolean sc$isInvulnerable(DamageSource source) {
        return sc$modifyDamage(source, 1.f) < 0;
    }
}
