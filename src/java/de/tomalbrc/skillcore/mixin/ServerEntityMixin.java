package de.tomalbrc.skillcore.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import eu.pb4.polymer.virtualentity.api.tracker.EntityTrackedData;
import eu.pb4.polymer.virtualentity.mixin.accessors.EntityAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ServerEntity.class)
public abstract class ServerEntityMixin {
    @Shadow
    @Final
    private Entity entity;

    @Inject(method = "sendDirtyEntityData", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/syncher/SynchedEntityData;getNonDefaultValues()Ljava/util/List;", ordinal = 0), cancellable = true)
    private void sc$onSendDirty(CallbackInfo ci, @Local LocalRef<List<SynchedEntityData.DataValue<?>>> list) {
        if (entity.isForceInvisible()){
            var l = new ArrayList<>(list.get());
            l.add(SynchedEntityData.DataValue.create(Entity.DATA_SHARED_FLAGS_ID, (byte)(entity.getEntityData().get(Entity.DATA_SHARED_FLAGS_ID) | 1 << EntityAccessor.getINVISIBLE_FLAG_INDEX())));
            list.set(l);
        }
    }

    @ModifyArg(method = "sendPairingData", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ClientboundSetEntityDataPacket;<init>(ILjava/util/List;)V"), index = 1)
    private List<SynchedEntityData.DataValue<?>> sc$onSendPairingData(List<SynchedEntityData.DataValue<?>> list) {
        if (entity.isForceInvisible()){
            list.add(SynchedEntityData.DataValue.create(EntityTrackedData.FLAGS, (byte)(entity.getEntityData().get(EntityTrackedData.FLAGS) | 1 << EntityAccessor.getINVISIBLE_FLAG_INDEX())));
        }
        return list;
    }
}
