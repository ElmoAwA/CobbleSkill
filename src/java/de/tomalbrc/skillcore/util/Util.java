package de.tomalbrc.skillcore.util;

import de.tomalbrc.skillcore.SkillCore;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Util {
    public static ResourceLocation id(String s) {
        return ResourceLocation.fromNamespaceAndPath(SkillCore.MODID, s);
    }

    public static void clickSound(ServerPlayer player) {
        player.playNotifySound(SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.MASTER, 0.5f, 1F);
    }

    public static Vec3 safeSpawnPosition(
            ServerLevel world,
            Vec3 base,
            double hRadius,
            double vRadius,
            int mobHeight,
            boolean yMod,
            boolean onSurface
    ) {

        for (int attempt = 0; attempt < 40; attempt++) {
            double x = base.x + (Math.random() * 2 - 1) * hRadius;
            double z = base.z + (Math.random() * 2 - 1) * hRadius;

            double y = base.y;
            if (yMod) {
                y += (Math.random() * 2 - 1) * vRadius;
            }

            BlockPos pos = BlockPos.containing(x, y, z);

            if (onSurface) {
                pos = pos.below();
                BlockState bs = world.getBlockState(pos);
                VoxelShape shape = bs.getCollisionShape(world, pos);
                double top = shape.isEmpty() ? 1.0 : shape.max(Direction.Axis.Y);
                y = pos.getY() + top;
            }

            BlockPos finalPos = BlockPos.containing(x, y, z);

            boolean ok = true;
            for (int i = 0; i < mobHeight; i++) {
                if (world.getBlockState(finalPos.above(i)).isSolid()) {
                    ok = false;
                    break;
                }
            }

            if (!ok) continue;

            return new Vec3(x, y, z);
        }

        return null;  // no safe location found
    }

    public static Double launchAngle(Vec3 from, Vec3 to, double v, double elevation, double g) {
        double horizontalDist = to.subtract(from).horizontalDistance();
        double vSquared = v * v;
        double vQuad = vSquared * vSquared;
        double underSqrt = vQuad - g * (g * horizontalDist * horizontalDist + 2 * elevation * vSquared);
        if (underSqrt < 0) {
            return null;
        }

        return Math.atan((vSquared - Math.sqrt(underSqrt)) / (g * horizontalDist));
    }

    public static double hangtime(double angle, double velocity, double y, double g) {
        double a = velocity * Math.sin(angle);
        double b = -2. * g * y;
        return Math.pow(a, 2.) + b < 0. ? 0. : (a + Math.sqrt(Math.pow(a, 2.) + b)) / g;
    }
}