package de.tomalbrc.skillcore.impl.target;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;

import java.util.List;

public class TargetLocationTargeter extends AbstractTargeter {
    @SerializedName(value = "maxdistance", alternate = {"max", "distance", "d"})
    Resolvable<Double> maxdistance = Resolvable.literal(64.);

    @SerializedName(value = "ignoreTransparent", alternate = "it")
    boolean ignoreTransparent;

    boolean lockpitch = false;

    @Override
    public List<Target> find(SkillTree tree) {
        if (tree.caster() instanceof Player player) {
            var target = Target.of(player);
            var hitResult = player.level().clip(new ClipContext(player.getEyePosition(), player.getEyePosition().add(player.getForward().multiply(1, lockpitch ? 0 : 1, 1).normalize().scale(maxdistance.resolve(tree, target))), ignoreTransparent ? ClipContext.Block.VISUAL : ClipContext.Block.COLLIDER, ignoreTransparent ? ClipContext.Fluid.NONE : ClipContext.Fluid.ANY, player));
            return List.of(Target.of(player.level(), hitResult.getLocation(), player.getYRot(), player.getXRot()));
        } else if (tree.caster() instanceof Mob mob){
            var t =  mob.getTarget();
            if (t == null && mob.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET)) t = mob.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
            if (t == null) t = mob.getLastHurtByMob();

            if (t != null)
                return List.of(Target.of(t));
        }

        return List.of();
    }
}