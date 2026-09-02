package de.tomalbrc.skillcore.api.target;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class Target {
    private BlockState blockState;
    private BlockPos blockPos;
    private Vec3 position;
    private Entity entity;
    public Level level;

    float xRot = 0;
    float yRot = 0;

    public static Target of() {
        return new Target();
    }

    public static Target of(Level level, BlockState blockState, BlockPos pos) {
        Target target = new Target();
        target.blockState = blockState;
        target.blockPos = pos;
        target.position = pos.getCenter();
        target.level = level;
        return target;
    }

    public static Target of(Level level, Vec3 pos) {
        Target target = new Target();
        target.position = pos;
        target.blockPos = BlockPos.containing(pos);
        target.level = level;
        return target;
    }

    public static Target of(Level level, Vec3 pos, float yaw, float pitch) {
        Target target = new Target();
        target.position = pos;
        target.blockPos = BlockPos.containing(pos);
        target.xRot = pitch;
        target.yRot = yaw;
        target.level = level;
        return target;
    }

    public static Target of(Entity entity) {
        Target target = new Target();
        target.entity = entity;
        if (entity == null)
            return target;

        target.position = entity.position();//.add(0, entity.getBbHeight()/2., 0);
        target.level = entity.level();
        target.blockPos = entity.blockPosition();
        return target;
    }

    public Entity getEntity() {
        return entity;
    }

    public Vec3 getPosition() {
        return entity != null ? entity.position() : position;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public BlockState getBlockState() {
        return blockState;
    }

    public boolean isEntity() {
        return this.entity != null;
    }

    public boolean isBlock() {
        return this.blockState != null;
    }

    public Level level() {
        return level;
    }

    public float getYRot() {
        return entity != null ? entity.getYRot() : yRot;
    }
    public float getXRot() {
        return entity != null ? entity.getXRot() : xRot;
    }
}
