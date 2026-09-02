package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;

public class ConsumeMechanic extends AbstractMechanic {
    @SerializedName(value = "damage", alternate = {"d", "dmg"})
    float damage = 1.f;
    @SerializedName(value = "heal", alternate = "h")
    float heal = 1.f;

    @Override
    public ExecutionResult execute(SkillTree tree) {
        if (tree.getCurrentTargets() != null) {
            for (Target target : tree.getCurrentTargets()) {
                if (target.getEntity() != null && target.getEntity().asLivingEntity() != null) {
                    var e = SkillCore.SERVER.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.GENERIC);

                    SkillCore.SERVER.execute(() -> {
                        target.getEntity().hurt(new DamageSource(e), damage);
                        if (tree.caster().asLivingEntity() != null) tree.caster().asLivingEntity().heal(heal);
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