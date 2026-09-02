package de.tomalbrc.skillcore.mixin.trigger;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractVillager.class)
public abstract class OnTrade extends Entity {
    @Shadow
    @Nullable
    private Player tradingPlayer;

    public OnTrade(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "notifyTrade", at = @At("HEAD"))
    private void sc$onTrade(MerchantOffer merchantOffer, CallbackInfo ci) {
        var o = this.overlay();
        if (this.tradingPlayer != null && o != null) {
            var th = o.getTriggerHandler();
            th.onTrade(tradingPlayer);
        }
    }
}
