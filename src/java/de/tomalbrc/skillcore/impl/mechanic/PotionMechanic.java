package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.util.BukkitIdConverter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.Locale;

public class PotionMechanic extends AbstractMechanic {
    @SerializedName(value = "subtype", alternate = "t")
    String potionType;
    @SerializedName(value = "duration", alternate = "d")
    int duration;
    @SerializedName(value = "level", alternate = {"lvl", "l"})
    int level;

    @SerializedName(value = "hasicon", alternate = {"icon", "i"})
    boolean hasIcon = false;
    @SerializedName(value = "hasparticles", alternate = {"particles", "p"})
    boolean hasParticles = false;
    @SerializedName(value = "ambientparticles", alternate = {"ambient", "a"})
    boolean ambientparticles = false;
    @SerializedName(value = "force", alternate = {"overwrite", "ow", "override", "or"})
    boolean force = false;

    @Override
    public ExecutionResult execute(SkillTree tree) {
        if (tree.getCurrentTargets() != null) for (Target target : tree.getCurrentTargets()) {
            if (target.getEntity() instanceof LivingEntity livingEntity) {
                var id = BukkitIdConverter.mobEffect(potionType).orElse(ResourceLocation.parse(potionType.toLowerCase(Locale.ROOT)));
                var holder = BuiltInRegistries.MOB_EFFECT.getHolder(id);
                if (holder.isPresent()) {
                    SkillCore.SERVER.execute(() -> {
                        livingEntity.addEffect(new MobEffectInstance(holder.orElseThrow(), duration, level, ambientparticles, hasParticles, hasIcon));
                    });
                } else {
                    SkillCore.LOGGER.error("Could not load potion: {}", potionType);
                }
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.POTION;
    }
}
