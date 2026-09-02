package de.tomalbrc.skillcore.impl.mechanic.cobblemon;

import com.cobblemon.mod.common.CobblemonNetwork;
import com.cobblemon.mod.common.net.messages.client.effect.SpawnSnowstormEntityParticlePacket;
import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.mechanic.AbstractMechanic;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class BedrockParticleMechanic extends AbstractMechanic {
    @SerializedName(value = "effect", alternate = {"e"})
    Resolvable<String> effect = Resolvable.literal("fireblast");
    @SerializedName(value = "locator", alternate = {"l"})
    Resolvable<String> loc = Resolvable.literal("special");

    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        if (targets != null) {
            for (Target target : targets) {
                var p = new SpawnSnowstormEntityParticlePacket(ResourceLocation.parse(effect.resolve(tree, target)), tree.caster().getId(), List.of(loc.resolve(tree, target)), target.isEntity() ? target.getEntity().getId() : null, List.of("target"));
                for (ServerPlayer player : PlayerLookup.tracking(tree.caster())) {
                    CobblemonNetwork.INSTANCE.sendPacketToPlayer(player, p);
                }
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.BEDROCK_PARTICLE;
    }
}