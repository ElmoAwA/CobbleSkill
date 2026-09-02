package de.tomalbrc.skillcore.impl.target;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.minecraft.util.Mth;

import java.util.List;

public class ForwardTargeter extends AbstractTargeter {
    @SerializedName(value = "forward", alternate = {"f", "amount", "a"})
    Resolvable<Float> forward = Resolvable.literal(5f);
    @SerializedName(value = "y")
    Resolvable<Float> y = Resolvable.literal(0f);
    @SerializedName(value = "rotate", alternate = {"rot"})
    Resolvable<Float> rotate = Resolvable.literal(0f);
    @SerializedName(value = "useeyelocation", alternate = {"uel"})
    Resolvable<Boolean> useeyelocation = Resolvable.literal(false);
    @SerializedName(value = "lockpitch")
    Resolvable<Boolean> lockpitch = Resolvable.literal(false);

    @Override
    public List<Target> find(SkillTree tree) {
        var entity = tree.caster();
        var vec = entity.getForward().multiply(1., lockpitch.resolve(tree) ? 0. : 1., 1.).normalize().yRot(Mth.DEG_TO_RAD * rotate.resolve(tree));
        return List.of(Target.of(entity.level(), (useeyelocation.resolve(tree) ? entity.getEyePosition() : entity.position()).add(vec.scale(forward.resolve(tree))).add(0, y.resolve(tree), 0)));
    }
}