package de.tomalbrc.skillcore.impl.target;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class ObstructingBlockTargeter extends AbstractTargeter {
    @Override
    public List<Target> find(SkillTree tree) {
        ServerLevel level = tree.level();
        List<Target> inherited = tree.getCurrentTargets();
        List<Target> results = new ArrayList<>();

        for (Target parent : inherited) {
            Vec3 origin = parent.getPosition();
            float yaw = parent.getYRot();

            BlockPos base = BlockPos.containing(origin);
            Direction forward = Direction.fromYRot(yaw);

            BlockPos front = base.relative(forward);
            BlockPos probe = front;

            int i = 0;
            while (canPass(level, probe)) {
                if (++i >= 10) break;

                Direction left  = forward.getCounterClockWise();
                Direction right = forward.getClockWise();

                switch (i) {
                    case 1 -> probe = base;
                    case 2 -> probe = front.above(1);
                    case 3 -> probe = front.above(2);

                    case 4 -> probe = front.relative(left);
                    case 5 -> probe = front.relative(left).above(1);
                    case 6 -> probe = front.relative(left).above(2);

                    case 7 -> probe = front.relative(right);
                    case 8 -> probe = front.relative(right).above(1);
                    case 9 -> probe = front.relative(right).above(2);
                }
            }
        }

        return results;
    }

    private static boolean canPass(Level level, BlockPos pos) {
        var state = level.getBlockState(pos);
        return state.isAir() || state.getCollisionShape(level, pos).isEmpty();
    }
}
