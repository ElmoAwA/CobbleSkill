package de.tomalbrc.skillcore.impl.target;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LivingInLineTargeter extends AbstractTargeter {
    @SerializedName(value = "radius", alternate = {"r"})
    Resolvable<Double> radius = Resolvable.literal(1.0);

    @SerializedName(value = "fromorigin", alternate = {"fo"})
    Resolvable<Boolean> fromOrigin = Resolvable.literal(false);

    @Override
    public List<Target> find(SkillTree tree) {
        var rad = radius.resolve(tree);
        var useOrigin = fromOrigin.resolve(tree);
        var caster = tree.caster();

        List<Target> inherited = tree.getCurrentTargets();
        if (inherited == null || inherited.isEmpty()) return List.of();

        Set<Object> found = new HashSet<>();
        List<Target> result = new ArrayList<>();

        Vec3 start = useOrigin ? tree.origin() : caster.position();

        for (Target tgt : inherited) {
            var ent = tgt.getEntity();
            if (ent == null) continue;

            Vec3 end = ent.position();
            Vec3 dir = end.subtract(start);
            double length = dir.length();
            if (length == 0) continue;

            Vec3 step = dir.normalize();
            double stepDistance = rad;

            for (double d = 0; d <= length; d += stepDistance) {
                Vec3 point = start.add(step.scale(d));
                double search = rad + 0.5;

                for (var e : tree.getNearbyEntities(search)) {
                    if (e.position().distanceTo(point) <= rad) {
                        if (found.add(e)) {
                            result.add(Target.of(e));
                        }
                    }
                }
            }
        }

        result.removeIf(x -> x.getEntity() == tree.caster());
        return result;
    }
}
