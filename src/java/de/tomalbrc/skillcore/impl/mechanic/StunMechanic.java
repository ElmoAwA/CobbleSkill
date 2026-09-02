package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.core.SkillEngine;
import de.tomalbrc.skillcore.impl.aura.AbstractAura;
import de.tomalbrc.skillcore.impl.mechanic.aura.AbstractAuraMechanic;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class StunMechanic extends AbstractAuraMechanic {
    @SerializedName(value = "stopai", alternate = {"ai"})
    Resolvable<Boolean> stopai = Resolvable.literal(false);

    @SerializedName(value = "gravity")
    Resolvable<Boolean> gravity = Resolvable.literal(false);

    @SerializedName(value = "facing", alternate = {"face", "f"})
    Resolvable<Boolean> facing = Resolvable.literal(false);

    @SerializedName(value = "noknockback", alternate = {"nokb", "kb"})
    Resolvable<Boolean> noknockback = Resolvable.literal(false);

    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        for (Target target : targets) {
            if (target.isEntity()) {
                StunGadget gadget = new StunGadget(tree, target, this);
                SkillEngine.getInstance().addAura(gadget);
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.STUN;
    }

    private static class StunGadget extends AbstractAura<StunMechanic> {
        final Entity entity;

        final boolean stopai;
        final boolean gravity;
        final boolean facing;
        final boolean noknockback;

        final float yaw;
        final float bodyYaw;
        final float pitch;
        final Vec3 pos;

        public StunGadget(SkillTree tree, Target target, StunMechanic mechanic) {
            super(tree, mechanic, target);

            this.entity = target.getEntity();
            if (this.entity instanceof Mob mob) mob.stopInPlace();

            this.yaw = entity.getYHeadRot();
            this.pitch = entity.getXRot();
            this.bodyYaw = entity.getYRot();
            this.pos = entity.position();
            this.stopai = mechanic.stopai.resolve(tree, target);
            this.gravity = mechanic.gravity.resolve(tree, target);
            this.facing = mechanic.facing.resolve(tree, target);
            this.noknockback = mechanic.noknockback.resolve(tree, target);

            if (gravity) entity.setNoGravity(true);
            if (stopai && entity instanceof Mob mob) mob.setNoAi(true);
        }

        @Override
        public void onAsyncTick() {
            SkillCore.SERVER.execute(() -> {
                if (!facing) {
                    entity.setYBodyRot(bodyYaw);
                    entity.setYHeadRot(yaw);
                    entity.setXRot(pitch);
                    entity.setYHeadRot(yaw);
                }
                if (noknockback) entity.moveTo(pos);
                else if (entity instanceof Mob mob) {
                    mob.getMoveControl().setWantedPosition(pos.x, pos.y, pos.z, 0.0001);
                }
            });
        }

        @Override
        public void onEnd(boolean run) {
            super.onEnd(run);

            if (entity instanceof Mob mob) {
                if (stopai) {
                    mob.setNoAi(false);
                }
            }
            if (gravity) entity.setNoGravity(false);
        }
    }
}
