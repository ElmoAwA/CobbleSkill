package de.tomalbrc.skillcore.api.overlay;

import de.tomalbrc.skillcore.data.MobData;
import de.tomalbrc.skillcore.impl.EquipmentTriggerHandler;
import de.tomalbrc.skillcore.impl.TriggerHandler;
import net.minecraft.server.level.ServerPlayer;

public class PlayerOverlay extends EntityOverlay<ServerPlayer> {
    private PlayerOverlay(ServerPlayer player, MobData data) {
        super(player, data);
    }

    public PlayerOverlay(ServerPlayer player) {
        super(player, null);
    }

    @Override
    protected TriggerHandler<ServerPlayer> createTriggerHandler() {
        return new EquipmentTriggerHandler(entity);
    }

    @Override
    public EquipmentTriggerHandler getTriggerHandler() {
        return (EquipmentTriggerHandler) triggerHandler;
    }
}
