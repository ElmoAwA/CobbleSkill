package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;

public class PercentDamageMechanic extends AbstractMechanic {
    @SerializedName(value = "percent", alternate = "p")
    float percent = 0.1f;
    @SerializedName(value = "currenthealth", alternate = {"current", "c", "ch"})
    boolean currentHealth = false;

    @Override
    public ExecutionResult execute(SkillTree tree) {
        if (tree.getCurrentTargets() != null) {
            for (Target target : tree.getCurrentTargets()) {
                if (target.getEntity() != null && target.getEntity().asLivingEntity() != null) {
                    Holder.Reference<DamageType> e = SkillCore.SERVER.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.GENERIC);
                    LivingEntity living = target.getEntity().asLivingEntity();
                    float dmg = (currentHealth ? living.getHealth() : living.getMaxHealth()) * percent;
                    SkillCore.SERVER.execute(() -> {
                        target.getEntity().hurt(new DamageSource(e), dmg);
                    });
                }
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.DAMAGE;
    }
}