package de.tomalbrc.skillcore.impl.mechanic;

import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;

import java.util.List;

public class DecapitateMechanic extends AbstractMechanic {
    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        for (Target target : targets) {
            Entity entity = target.getEntity();
            if (entity instanceof ServerPlayer player) {
                dropPlayerHead(player);
            }
        }
        return ExecutionResult.NULL;
    }

    private void dropPlayerHead(ServerPlayer player) {
        ItemStack head = new ItemStack(Items.PLAYER_HEAD, 1);
        head.set(DataComponents.PROFILE, new ResolvableProfile(player.getGameProfile()));
        player.spawnAtLocation(head);
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.DECAPITATE;
    }
}