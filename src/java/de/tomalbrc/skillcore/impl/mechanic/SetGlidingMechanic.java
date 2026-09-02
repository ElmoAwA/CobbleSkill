package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.mixin.accessor.EntityAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.Items;

public class SetGlidingMechanic extends AbstractMechanic {
    @SerializedName(value = "gliding", alternate = "g")
    boolean gliding = true;

    @Override
    public ExecutionResult execute(SkillTree tree) {
        if (tree.getCurrentTargets() != null) {
            for (Target target : tree.getCurrentTargets()) {
                if (gliding && target.getEntity() instanceof LivingEntity living && living.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA) && ElytraItem.isFlyEnabled(living.getItemBySlot(EquipmentSlot.CHEST)))
                    ((EntityAccessor)living).invokeSetSharedFlag(7, true);
                else if (!gliding && target.isEntity()) {
                    ((EntityAccessor)target.getEntity()).invokeSetSharedFlag(7, false);
                }
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.SET_GLIDING;
    }
}
