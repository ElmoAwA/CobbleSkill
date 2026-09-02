package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.data.EquipmentEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class EquipMechanic extends AbstractMechanic {
    @SerializedName(value = "item", alternate = {"items", "i", "equipment", "equip", "e"})
    EquipmentEntry data;

    @Override
    public ExecutionResult execute(SkillTree tree) {
        if (tree.caster() instanceof LivingEntity living) {
            living.setItemSlot(data.slot(), data.item().asItemStack());
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.EQUIP;
    }
}