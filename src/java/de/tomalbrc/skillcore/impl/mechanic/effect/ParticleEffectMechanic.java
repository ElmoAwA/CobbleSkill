package de.tomalbrc.skillcore.impl.mechanic.effect;

import com.google.gson.annotations.SerializedName;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.DataResult;
import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.mechanic.AbstractMechanic;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import de.tomalbrc.skillcore.util.BukkitIdConverter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.FastColor;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class ParticleEffectMechanic extends AbstractMechanic {

    @SerializedName(value = "particle", alternate = {"p"})
    protected Resolvable<String> particle = Resolvable.literal("minecraft:flame");

    @SerializedName(value = "mob", alternate = {"m", "t"})
    protected Resolvable<ResourceLocation> mob;

    @SerializedName(value = "amount", alternate = {"a"})
    protected Resolvable<Integer> amount = Resolvable.literal(10);

    @SerializedName(value = "spread", alternate = {"offset"})
    protected Resolvable<Double> spread = Resolvable.literal(0.0);

    @SerializedName(value = "hspread", alternate = {"hs"})
    protected Resolvable<Double> hSpread;

    @SerializedName(value = "vspread", alternate = {"vs", "ys"})
    protected Resolvable<Double> vSpread;

    @SerializedName(value = "xspread", alternate = {"xs"})
    protected Resolvable<Double> xSpread;

    @SerializedName(value = "zspread", alternate = {"zs"})
    protected Resolvable<Double> zSpread;

    @SerializedName(value = "speed", alternate = {"s"})
    protected Resolvable<Float> speed = Resolvable.literal(0.f);

    @SerializedName(value = "yoffset", alternate = {"y"})
    protected Resolvable<Double> yOffset = Resolvable.literal(0.0);

    @SerializedName(value = "startforwardoffset", alternate = {"sfo"})
    protected Resolvable<Double> startForwardOffset = Resolvable.literal(0.0);

    @SerializedName(value = "startsideoffset", alternate = {"sso"})
    protected Resolvable<Double> startSideOffset = Resolvable.literal(0.0);

    @SerializedName(value = "directional", alternate = {"d"})
    protected Resolvable<Boolean> directional = Resolvable.literal(false);

    @SerializedName(value = "directionreversed", alternate = {"dr"})
    protected Resolvable<Boolean> directionReversed = Resolvable.literal(false);

    @SerializedName(value = "direction", alternate = {"dir"})
    protected Resolvable<Vec3> direction = Resolvable.literal(Vec3.ZERO);

    @SerializedName(value = "fixedyaw", alternate = {"yaw"})
    protected Resolvable<Double> fixedYaw = Resolvable.literal(-1111.);

    @SerializedName(value = "fixedpitch", alternate = {"pitch"})
    protected Resolvable<Double> fixedPitch = Resolvable.literal(-1111.);

    @SerializedName(value = "fromorigin", alternate = {"fo"})
    protected boolean fromOrigin = false;

    @SerializedName(value = "viewdistance", alternate = {"vd"})
    protected Resolvable<Double> viewDistance = Resolvable.literal(128.0);

    @SerializedName(value = "color", alternate = {"c", "col"})
    protected String color = "#FF0000";

    @SerializedName(value = "size")
    protected double size = 1.;

    transient ParticleOptions particleOptions;

    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        if (targets == null || targets.isEmpty()) return ExecutionResult.NULL;

        ServerLevel level = tree.level();

        if (particleOptions == null) initializeParticle(tree);

        double effH = hSpread != null && hSpread.resolve(tree) != null ? hSpread.resolve(tree) : spread.resolve(tree);
        double effV = vSpread != null && vSpread.resolve(tree) != null ? vSpread.resolve(tree) : spread.resolve(tree);
        double effX = xSpread != null && xSpread.resolve(tree) != null ? xSpread.resolve(tree) : effH;
        double effZ = zSpread != null && zSpread.resolve(tree) != null ? zSpread.resolve(tree) : effH;

        float pSpeed = speed != null && speed.resolve(tree) != null ? speed.resolve(tree) : 0f;
        int amt = Math.max(1, localAmount(tree));

        double vd = viewDistance != null && viewDistance.resolve(tree) != null ? viewDistance.resolve(tree) : 128.0;
        Collection<ServerPlayer> viewers = level.players().stream().filter(p -> tree.caster() == null || p.position().distanceToSqr(tree.caster().position()) <= vd * vd).toList();

        for (Target target : targets) {
            List<Vec3> positions = computePositions(tree, target, effX, effV, effZ);
            spawnPositionsForTarget(tree, positions, viewers, particleOptions, pSpeed, amt, effX, effV, effZ);
        }

        return ExecutionResult.NULL;
    }

    void initializeParticle(SkillTree tree) {
        String particleString = particle.resolve(tree);
        var pid = BukkitIdConverter.particle(particleString).orElse(particleString);
        var col = color != null && color.startsWith("#") ? Integer.parseInt(color.substring(1), 16) : Integer.parseInt(color);
        var r = FastColor.ARGB32.red(col) / 255.f;
        var g = FastColor.ARGB32.green(col) / 255.f;
        var b = FastColor.ARGB32.blue(col) / 255.f;
        pid = pid.replace("scale:1", "scale:" + size);
        pid = pid.replace("color:[1,0,0]", String.format(Locale.ROOT, "color:[%f,%f,%f]", r, g, b));
        try {
            particleOptions = readParticle(new StringReader(pid.toLowerCase(Locale.ROOT)), SkillCore.SERVER.registryAccess());
        } catch (CommandSyntaxException e) {
            SkillCore.LOGGER.warn("Invalid particle spec '{}'", particleString, e);
        }
    }

    protected Integer localAmount(SkillTree tree) {
        return amount.resolve(tree);
    }

    protected List<Vec3> computePositions(SkillTree tree, Target target, double effX, double effV, double effZ) {
        Vec3 origin = fromOrigin && tree.origin() != null ? tree.origin() : target.getPosition();
        origin = origin.add(0, yOffset.resolve(tree), 0);
        int count = Math.max(1, localAmount(tree));
        List<Vec3> positions = new ArrayList<>(count);

        if (directional.resolve(tree)) {
            positions.add(origin);
            return positions;
        }

        for (int i = 0; i < count; i++) {
            Vec3 p = origin.add(randomOffset(effX), randomOffset(effV), randomOffset(effZ));
            if (fixedYaw.resolve(tree) != -1111 || fixedPitch.resolve(tree) != -1111) {
                p = applyFixedRotation(origin, p, fixedYaw.resolve(tree), fixedPitch.resolve(tree));
            }
            positions.add(p);
        }

        return positions;
    }

    public void spawnPositionsForTarget(SkillTree tree, List<Vec3> positions, Collection<ServerPlayer> viewers, ParticleOptions options, float pSpeed, int amount, double effX, double effV, double effZ) {
        if (positions == null || positions.isEmpty() || viewers == null || viewers.isEmpty() || options == null) return;

        boolean isDirectional = directional.resolve(tree);
        Vec3 dirVec = Vec3.ZERO;
        if (isDirectional) {
            Vec3 d = direction.resolve(tree);
            dirVec = directionReversed.resolve(tree) ? d.reverse() : d;
        }

        float xOff = isDirectional ? (float) dirVec.x : (float) effX;
        float yOff = isDirectional ? (float) dirVec.y : (float) effV;
        float zOff = isDirectional ? (float) dirVec.z : (float) effZ;
        int count = isDirectional ? 0 : Math.max(1, amount);

        List<Packet<? super ClientGamePacketListener>> packets = new ArrayList<>(positions.size());
        for (Vec3 pos : positions) {
            packets.add(new ClientboundLevelParticlesPacket(options, false, pos.x, pos.y, pos.z, xOff, yOff, zOff, pSpeed, count));
        }

        if (!packets.isEmpty()) {
            var bundle = new ClientboundBundlePacket(packets);
            viewers.forEach(x -> x.connection.send(bundle));
        }
    }

    double randomOffset(double spread) {
        return (Math.random() * 2.0 - 1.0) * spread;
    }

    Vec3 applyFixedRotation(Vec3 origin, Vec3 pos, double yawDeg, double pitchDeg) {
        Vec3 offset = pos.subtract(origin);

        if (yawDeg != -1111) {
            double yawRad = Math.toRadians(yawDeg);
            double x = offset.x * Math.cos(yawRad) - offset.z * Math.sin(yawRad);
            double z = offset.x * Math.sin(yawRad) + offset.z * Math.cos(yawRad);
            offset = new Vec3(x, offset.y, z);
        }

        if (pitchDeg != -1111) {
            double pitchRad = Math.toRadians(pitchDeg);
            double y = offset.y * Math.cos(pitchRad) - offset.z * Math.sin(pitchRad);
            double z = offset.y * Math.sin(pitchRad) + offset.z * Math.cos(pitchRad);
            offset = new Vec3(offset.x, y, z);
        }

        return origin.add(offset);
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.PARTICLE_EFFECT;
    }

    public static ParticleOptions readParticle(StringReader stringReader, HolderLookup.Provider provider) throws CommandSyntaxException {
        ParticleType<?> particleType = readParticleType(stringReader, provider.lookupOrThrow(Registries.PARTICLE_TYPE));
        return readParticle(stringReader, particleType, provider);
    }

    protected static ParticleType<?> readParticleType(StringReader stringReader, HolderLookup<ParticleType<?>> holderLookup) throws CommandSyntaxException {
        ResourceLocation resourceLocation = ResourceLocation.read(stringReader);
        ResourceKey<ParticleType<?>> resourceKey = ResourceKey.create(Registries.PARTICLE_TYPE, resourceLocation);
        var res = holderLookup.get(resourceKey);
        if (res.isEmpty()) {
            SkillCore.LOGGER.error("INVALID: {}", resourceLocation);
            return ParticleTypes.EFFECT;
        }
        return res.orElseThrow().value();
    }

    protected static <T extends ParticleOptions> T readParticle(StringReader stringReader, ParticleType<T> particleType, HolderLookup.Provider provider) throws CommandSyntaxException {
        CompoundTag compoundTag = stringReader.canRead() && stringReader.peek() == '{' ? (new TagParser(stringReader)).readStruct() : new CompoundTag();
        DataResult<T> result = particleType.codec().codec().parse(provider.createSerializationContext(NbtOps.INSTANCE), compoundTag);
        return result.getOrThrow();
    }
}
