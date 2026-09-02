package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.food.FoodProperties;

import java.util.List;
import java.util.Optional;

public class FeedMechanic extends AbstractMechanic {
    @SerializedName(value = "amount", alternate = {"a"})
    int amount;
    @SerializedName(value = "saturation", alternate = {"s"})
    float saturation;
    @SerializedName(value = "overfeed", alternate = {"o", "of"})
    boolean overfeed;

    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        if (targets != null) {
            for (Target target : targets) {
                if (target.getEntity() instanceof ServerPlayer player) {
                    player.getFoodData().eat(new FoodProperties(amount, saturation, overfeed, 0, Optional.empty(), List.of()));
                }
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.FEED;
    }
}