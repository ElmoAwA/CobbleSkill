package de.tomalbrc.skillcore.impl.mechanic.effect;

import com.google.gson.annotations.SerializedName;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.core.SkillEngine;
import de.tomalbrc.skillcore.impl.gadget.AbstractTickingGadget;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class ParticleTornadoMechanic extends ParticleEffectMechanic {
    @SerializedName(value = "cloudhspread", alternate = {"chs"})
    public float cloudHSpread = 1.0f;
    @SerializedName(value = "cloudvspread", alternate = {"cvs"})
    public float cloudVSpread = 1.8f;
    @SerializedName(value = "cloudpspeed", alternate = {"cps"})
    public float cloudPSpeed = 2.0f;
    @SerializedName(value = "cloudyoffset", alternate = {"cyo"})
    public float cloudYOffset = 1.8f;
    @SerializedName(value = "cloudparticle", alternate = {"cp"})
    public String strCloudParticle = "cloud";

    @SerializedName(value = "cloudsize", alternate = {"cs"})
    public float cloudSize = 2.5f;
    @SerializedName(value = "particlespeedramp", alternate = {"psr"})
    public float particleSpeedRamp = 0.0f;
    @SerializedName(value = "cloudamount", alternate = {"ca"})
    public int cloudAmount = 1;

    @SerializedName(value = "height", alternate = {"h", "tornadoheight"})
    public float tornadoHeight = 5.0f;
    @SerializedName(value = "maxradius", alternate = {"mr", "maxtornadoradius"})
    public float maxTornadoRadius = 5.0f;
    @SerializedName(value = "sliceheight", alternate = {"sh"})
    public int sliceHeight = 64;
    @SerializedName(value = "interval", alternate = {"i"})
    public int interval = 4;
    @SerializedName(value = "duration")
    public int duration = 200;
    @SerializedName(value = "rotationspeed", alternate = {"rs"})
    public float rotationSpeed = 0.04f;

    @SerializedName(value = "showcloud", alternate = {"cloud", "sc"})
    public boolean showCloud = true;
    @SerializedName(value = "showtornado", alternate = {"tornado", "st"})
    public boolean showTornado = true;
    @SerializedName(value = "stoponcasterdeath", alternate = {"scd"})
    public boolean stopOnCasterDeath = true;
    @SerializedName(value = "stoponentitydeath", alternate = {"sed"})
    public boolean stopOnEntityDeath = true;
    @SerializedName(value = "distance", alternate = {"dist"})
    public double distance = 0.375;

    @Override
    public ResourceLocation id() {
        return Mechanics.PARTICLE_TORNADO;
    }

    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        for (Target t : targets) {
            SkillEngine.getInstance().addGadget(new ParticleTornadoGadget(this, tree, t));
        }
        return ExecutionResult.NULL;
    }

    public static class ParticleTornadoGadget extends AbstractTickingGadget {
        private final ParticleTornadoMechanic mech;
        private final SkillTree tree;
        private final Target target;

        private final float resolvedMainSpeed;
        private final int resolvedMainAmount;
        private final double resolvedEffX;
        private final double resolvedEffV;
        private final double resolvedEffZ;
        private final double resolvedViewDistance;
        private final ParticleOptions cloudOptions;

        public ParticleTornadoGadget(ParticleTornadoMechanic mech, SkillTree tree, Target target) {
            super(tree, Math.max(1, mech.duration), Math.max(1, mech.interval));
            this.mech = mech;
            this.tree = tree;
            this.target = target;

            this.resolvedMainSpeed = mech.speed != null && mech.speed.resolve(tree) != null ? mech.speed.resolve(tree) : 0f;
            this.resolvedMainAmount = Math.max(1, mech.localAmount(tree));

            double globalH = (mech.hSpread != null && mech.hSpread.resolve(tree) != null) ? mech.hSpread.resolve(tree) : mech.spread.resolve(tree);
            double globalV = (mech.vSpread != null && mech.vSpread.resolve(tree) != null) ? mech.vSpread.resolve(tree) : mech.spread.resolve(tree);

            this.resolvedEffX = (mech.xSpread != null && mech.xSpread.resolve(tree) != null) ? mech.xSpread.resolve(tree) : globalH;
            this.resolvedEffV = globalV;
            this.resolvedEffZ = (mech.zSpread != null && mech.zSpread.resolve(tree) != null) ? mech.zSpread.resolve(tree) : globalH;

            this.resolvedViewDistance = mech.viewDistance != null && mech.viewDistance.resolve(tree) != null ? mech.viewDistance.resolve(tree) : 128.0;

            mech.initializeParticle(tree);
            this.cloudOptions = parseCloudParticle(mech.strCloudParticle);
        }

        private ParticleOptions parseCloudParticle(String particleId) {
            if (particleId == null || particleId.isEmpty()) return null;
            try {
                return ParticleEffectMechanic.readParticle(new StringReader(particleId.toLowerCase(Locale.ROOT)), SkillCore.SERVER.registryAccess());
            } catch (CommandSyntaxException ignored) {
                return null;
            }
        }

        @Override
        public boolean shouldStop() {
            if (mech.stopOnCasterDeath) {
                var caster = tree.caster();
                if (caster == null || !caster.isAlive()) return true;
            }

            if (mech.stopOnEntityDeath) {
                var ent = target.getEntity();
                return ent != null && !ent.isAlive();
            }

            return false;
        }

        @Override
        public void onAsyncTick() {
            double distSq = resolvedViewDistance * resolvedViewDistance;
            Collection<ServerPlayer> viewers = level().players().stream().filter(p -> p.position().distanceToSqr(tree.origin()) <= distSq).toList();

            if (viewers.isEmpty()) return;

            Vec3 origin = target.getPosition();

            if (mech.showCloud && cloudOptions != null) {
                Vec3 cloudBase = origin.add(0.0, mech.cloudYOffset, 0.0);
                for (int i = 0; i < mech.cloudAmount; i++) {
                    double angle = Math.random() * Math.PI * 2.0;
                    double radius = Math.random() * mech.cloudSize;

                    mech.spawnPositionsForTarget(tree, List.of(cloudBase.add(Math.cos(angle) * radius, 0.0, Math.sin(angle) * radius)), viewers, cloudOptions, mech.cloudPSpeed, mech.cloudAmount, mech.cloudHSpread, mech.cloudVSpread, mech.cloudHSpread);
                }
            }

            if (mech.showTornado && mech.particleOptions != null) {
                Vec3 tornadoBase = origin.add(0.0, 0.2, 0.0);
                double rot = (double) ticks * mech.rotationSpeed;
                double step = Math.max(1e-6, mech.distance);

                double radiusMultiplier = 0.45 * mech.maxTornadoRadius * (2.35 / mech.tornadoHeight);
                int slices = Math.max(4, mech.sliceHeight);
                double angStep = (2.0 * Math.PI) / (double) slices;

                for (double y = 0.0; y < mech.tornadoHeight; y += step) {
                    double frac = y / mech.tornadoHeight;
                    double currentRadius = Math.min(mech.maxTornadoRadius, radiusMultiplier * y);

                    for (int s = 0; s < slices; s++) {
                        double angle = s * angStep + rot;
                        Vec3 pos = tornadoBase.add(Math.cos(angle) * currentRadius, y, Math.sin(angle) * currentRadius);

                        float pSpeed = (float) (resolvedMainSpeed + (mech.particleSpeedRamp * frac));

                        mech.spawnPositionsForTarget(tree, List.of(pos), viewers, mech.particleOptions, pSpeed, resolvedMainAmount, resolvedEffX, resolvedEffV, resolvedEffZ);
                    }
                }
            }
        }

        @Override
        public void onHit(Entity entity) {
        }

        @Override
        public void onEnd() {
        }
    }
}