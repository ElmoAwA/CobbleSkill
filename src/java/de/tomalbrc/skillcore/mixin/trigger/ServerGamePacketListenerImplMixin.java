package de.tomalbrc.skillcore.mixin.trigger;

import de.tomalbrc.skillcore.SkillCoreComponents;
import net.minecraft.network.protocol.game.ServerboundPickItemPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @Unique boolean sc$lastJump = false;
    @Unique boolean sc$lastCrouch = false;

    @Shadow
    public ServerPlayer player;

    @Inject(method = "handlePlayerCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;setShiftKeyDown(Z)V", ordinal = 0))
    private void sc$onInputCrouch(ServerboundPlayerCommandPacket serverboundPlayerCommandPacket, CallbackInfo ci) {
        if (!sc$lastCrouch) {
            var o = this.player.playerOverlay();
            if (o != null) {
                o.getTriggerHandler().onCrouch();
            }
        }
        sc$lastCrouch = true;
    }

    @Inject(method = "handlePlayerCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;setShiftKeyDown(Z)V", ordinal = 1))
    private void sc$onInputUnCrouch(ServerboundPlayerCommandPacket serverboundPlayerCommandPacket, CallbackInfo ci) {
        if (sc$lastCrouch) {
            var o = this.player.playerOverlay();
            if (o != null) {
                o.getTriggerHandler().onUnCrouch();
            }
        }
        sc$lastCrouch = false;
    }


    @Inject(method = "handlePlayerInput", at = @At("TAIL"))
    private void sc$onInput(ServerboundPlayerInputPacket serverboundPlayerInputPacket, CallbackInfo ci) {
        if (sc$lastJump != serverboundPlayerInputPacket.isJumping() && serverboundPlayerInputPacket.isJumping()) {
            var o = this.player.playerOverlay();
            if (o != null) {
                o.getTriggerHandler().onJump();
            }
        }

        sc$lastJump = serverboundPlayerInputPacket.isJumping();
    }

    @Inject(method = "handlePickItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;pickSlot(I)V"))
    private void sc$onPick(ServerboundPickItemPacket serverboundPickItemPacket, CallbackInfo ci) {
        var i = serverboundPickItemPacket.getSlot();
        if (Inventory.isHotbarSlot(i) && i != player.getInventory().selected && player.getInventory().getItem(i).has(SkillCoreComponents.SKILLS)) {
            var sel = player.getInventory().getSelected();
            if (sel.has(SkillCoreComponents.SKILLS) && player.overlay() != null) {
                var o = player.playerOverlay();
                if (o != null)
                    o.getTriggerHandler().onUnHeld(sel);
            }

            var selNew = player.getInventory().getItem(i);
            if (selNew.has(SkillCoreComponents.SKILLS) && player.overlay() != null) {
                var o = player.playerOverlay();
                if (o != null)
                    o.getTriggerHandler().onHeld(selNew);
            }
        }
    }
}
