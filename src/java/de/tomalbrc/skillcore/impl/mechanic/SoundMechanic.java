package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class SoundMechanic extends AbstractMechanic {
    @SerializedName(value = "sound", alternate = {"s"})
    ResourceLocation sound;
    @SerializedName(value = "soundcategory", alternate = {"sc", "category", "source"})
    SoundSource source = SoundSource.MASTER;
    @SerializedName(value = "pitch", alternate = {"p"})
    Resolvable<Float> pitch = Resolvable.literal(1f);
    @SerializedName(value = "volume", alternate = {"v"})
    Resolvable<Float> volume = Resolvable.literal(.5f);

    @Override
    public ExecutionResult execute(SkillTree tree) {
        if (tree.getCurrentTargets() != null) {
            for (Target target : tree.getCurrentTargets()) {
                target.level().playSound(
                        null,
                        target.getPosition().x(),
                        target.getPosition().y(),
                        target.getPosition().z(),
                        SoundEvent.createVariableRangeEvent(sound),
                        source == null ? SoundSource.MASTER : source,
                        volume.resolve(tree),
                        pitch.resolve(tree)
                );
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.SOUND;
    }
}