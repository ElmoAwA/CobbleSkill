package de.tomalbrc.skillcore.impl.target;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import de.tomalbrc.skillcore.util.RangedValue;
import de.tomalbrc.skillcore.util.Util;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomLocationsNearTargetsTargeter extends AbstractTargeter {
    @SerializedName(value = "amount", alternate = {"a"})
    Resolvable<RangedValue> amount = Resolvable.literal(RangedValue.of(5));

    @SerializedName(value = "radius", alternate = {"r", "maxradius", "maxr"})
    Resolvable<Double> radius = Resolvable.literal(5.0);

    @SerializedName(value = "minradius", alternate = {"minr"})
    Resolvable<Double> minRadius = Resolvable.literal(0.0);

    @SerializedName(value = "spacing", alternate = {"s"})
    Resolvable<Double> spacing = Resolvable.literal(0.0);

    @SerializedName(value = "onsurface", alternate = {"surface"})
    Resolvable<Boolean> onSurface = Resolvable.literal(false);

    transient final Random random = new Random();

    @Override
    public List<Target> find(SkillTree tree) {
        ServerLevel level = tree.level();
        List<Target> inherited = tree.getCurrentTargets();
        List<Target> results = new ArrayList<>();

        int amt = Math.max(0, amount.resolve(tree).getAsInteger());
        double maxR = Math.max(0.0, radius.resolve(tree));
        double minR = Math.max(0.0, Math.min(minRadius.resolve(tree), maxR));
        double minSpacing = Math.max(0.0, spacing.resolve(tree));
        boolean placeOnSurface = onSurface.resolve(tree);

        int attemptsPerPoint = Math.max(8, amt * 6);
        double minSpacingSq = minSpacing * minSpacing;

        for (Target parent : inherited) {
            Vec3 origin = parent.getPosition();

            List<Vec3> placed = new ArrayList<>();
            for (int i = 0; i < amt; i++) {
                for (int attempt = 0; attempt < attemptsPerPoint; attempt++) {
                    double u = random.nextDouble();
                    double r = u * (maxR - minR) + minR;
                    double theta = random.nextDouble() * Math.PI * 2.0;

                    double x = origin.x + Math.cos(theta) * r;
                    double z = origin.z + Math.sin(theta) * r;
                    double y = origin.y;

                    Vec3 candidate = Util.safeSpawnPosition(level, new Vec3(x, y, z), maxR, minR, 1, false, placeOnSurface);
                    if (candidate == null) continue;

                    if (minSpacing > 0.0) {
                        boolean tooClose = false;
                        for (Vec3 p : placed) {
                            double dx = p.x - candidate.x;
                            double dy = p.y - candidate.y;
                            double dz = p.z - candidate.z;
                            if (dx * dx + dy * dy + dz * dz < minSpacingSq) {
                                tooClose = true;
                                break;
                            }
                        }
                        if (tooClose) continue;
                    }

                    placed.add(candidate);
                    break;
                }
            }

            for (Vec3 v : placed) {
                results.add(Target.of(level, v));
            }
        }

        return results;
    }
}
