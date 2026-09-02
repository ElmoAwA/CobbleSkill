package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.mechanic.model.holder.LivingEntityHolder;
import net.minecraft.resources.ResourceLocation;

public class MountModelMechanic extends AbstractMechanic {
    enum Mode {
        WALKING,
        FORCE_WALKING,
        FLYING,
        FORCE_FLYING
    }

    @SerializedName(value = "driver", alternate = {"d", "drive"})
    boolean driver = true;

    @SerializedName(value = "force", alternate = {"f"})
    boolean force = false;

    @SerializedName(value = "seat", alternate = {"p", "pbone"})
    String boneName;

    @SerializedName(value = "autodismount", alternate = {"ad"})
    boolean autodismount = false;

    @SerializedName(value = "damagemount", alternate = {"dmg"})
    boolean damagemount = false;

    @SerializedName(value = "interactmount", alternate = {"int"})
    boolean interactmount = true;

    @SerializedName(value = "modelid", alternate = {"m", "mid", "model"})
    String modelid;

    @Override
    public ExecutionResult execute(SkillTree tree) {
        var o = tree.caster().overlay();
        if (o != null && o.customModel(modelid) != null) {
            var holder = o.customModel(modelid);
            for (Target target : tree.getCurrentTargets()) {
                if (target.isEntity()) {
                    boolean isPassenger = target.getEntity().isPassenger();
                    boolean isPassengerVirtual = target.getEntity().getVirtualSeat() != null;

                    if (isPassenger && !autodismount)
                        continue;

                    var oldPassenger = holder.getPassenger(boneName);
                    if (!force && oldPassenger != null)
                        continue;
                    else {
                        holder.removePassenger(boneName);
                    }

                    if (autodismount) {
                        if (isPassengerVirtual && target.getEntity().getVirtualSeat().element().getHolder() instanceof LivingEntityHolder<?> holder1) {
                            holder1.removePassenger(target.getEntity());
                        } else {
                            target.getEntity().stopRiding();
                        }
                    }

                    holder.addSeatAttachment(boneName, target.getEntity());
                }
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.MOUNT_MODEL;
    }
}
