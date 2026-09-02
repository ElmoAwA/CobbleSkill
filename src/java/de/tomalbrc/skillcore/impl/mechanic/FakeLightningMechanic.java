package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import eu.pb4.polymer.virtualentity.api.VirtualEntityUtils;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

public class FakeLightningMechanic extends AbstractMechanic {
    @SerializedName(value = "localized", alternate = {"l"})
    boolean localized = false;

    @SerializedName(value = "localizedradius", alternate = {"lr", "r"})
    float localizedradius = 128f;

    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        if (targets != null) {
            for (Target target : targets) {
                var pos = target.getPosition();
                var packet = new ClientboundAddEntityPacket(
                        VirtualEntityUtils.requestEntityId(),
                        UUID.randomUUID(),
                        pos.x(), pos.y(), pos.z(), 0f, 0f,
                        EntityType.LIGHTNING_BOLT,
                        0,
                        Vec3.ZERO,
                        0
                );

                if (localized) {
                    for (ServerPlayer player : PlayerLookup.tracking(tree.caster())) {
                        if (player.position().distanceTo(pos) <= localizedradius) player.connection.send(packet);
                    }
                } else {
                    for (Player player : tree.level().players()) {
                        if (player instanceof ServerPlayer serverPlayer) serverPlayer.connection.send(packet);
                    }
                }
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.FAKELIGHTNING;
    }
}