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
import net.minecraft.world.entity.ai.attributes.Attributes;

public class BaseDamageMechanic extends DamageMechanic {
    @SerializedName(value = "multiplier", alternate = "m")
    float multiplier = 1.0f;
    @SerializedName(value = "useattribute", alternate = {"attribute", "attr"})
    boolean useAttribute = false;

    @Override
    public ExecutionResult execute(SkillTree tree) {
        if (tree.getCurrentTargets() != null) {
            for (Target target : tree.getCurrentTargets()) {
                if (target.getEntity() != null && target.getEntity().asLivingEntity() != null) {
                    Holder.Reference<DamageType> e = SkillCore.SERVER.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.GENERIC);
                    LivingEntity living = target.getEntity().asLivingEntity();
                    var v = tree.caster().asLivingEntity().getAttributes().getBaseValue(Attributes.ATTACK_DAMAGE);
                    if (useAttribute) {
                        v = tree.caster().asLivingEntity().getAttributes().getValue(Attributes.ATTACK_DAMAGE);
                    }

                    v += multiplier;

                    double finalV = v;
                    SkillCore.SERVER.execute(() -> {
                        target.getEntity().hurt(new DamageSource(e), (float) finalV);
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