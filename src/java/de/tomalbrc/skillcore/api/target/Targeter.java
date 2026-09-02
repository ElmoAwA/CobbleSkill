package de.tomalbrc.skillcore.api.target;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.util.ThreatTable;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Targeter {
    List<Target> find(SkillTree tree);

    List<Target> sort(SkillTree tree, @Nullable ThreatTable threatTable, Vec3 origin, List<Target> targets);

    enum Sorting {
        NONE,
        RANDOM,
        NEAREST,
        FURTHEST,
        // entity only
        HIGHEST_HEALTH,
        LOWEST_HEALTH,
        HIGHEST_THREAT,
        LOWEST_THREAT
    }
}