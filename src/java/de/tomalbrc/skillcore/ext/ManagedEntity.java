package de.tomalbrc.skillcore.ext;

import de.tomalbrc.bil.core.holder.wrapper.Bone;
import de.tomalbrc.skillcore.api.overlay.EntityOverlay;
import de.tomalbrc.skillcore.api.overlay.PlayerOverlay;
import de.tomalbrc.skillcore.impl.variable.Variable;
import eu.pb4.polymer.virtualentity.api.elements.GenericEntityElement;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface ManagedEntity {
    default String mobId() {
        throw new UnsupportedOperationException();
    }

    default void mobId(String id) {
        throw new UnsupportedOperationException();
    }

    default void setCustomLevel(int level) {
        throw new UnsupportedOperationException();
    }

    default int getCustomLevel() {
        throw new UnsupportedOperationException();
    }

    default void setFaction(String faction) {
        throw new UnsupportedOperationException();
    }

    default String getFaction() {
        throw new UnsupportedOperationException();
    }

    default void setStance(String stance) {
        throw new UnsupportedOperationException();
    }

    default String getStance() {
        throw new UnsupportedOperationException();
    }

    default void setPower(double power) {
        throw new UnsupportedOperationException();
    }

    default double getPower() {
        throw new UnsupportedOperationException();
    }

    default void setForceInvisible(boolean forceInvisible) {
        throw new UnsupportedOperationException();
    }

    default boolean isForceInvisible() {
        throw new UnsupportedOperationException();
    }

    default boolean isOnGlobalCooldown() {
        throw new UnsupportedOperationException();
    }

    default void setGlobalCooldown(int cooldown) {
        throw new UnsupportedOperationException();
    }

    default Map<String, Variable> getVariables() {
        throw new UnsupportedOperationException();
    }

    default @Nullable EntityOverlay<? extends Entity> overlay() {
        throw new UnsupportedOperationException();
    }

    default @Nullable PlayerOverlay playerOverlay() {
        return (PlayerOverlay) overlay();
    }

    default void overlay(EntityOverlay<? extends Entity> overlay) {
        throw new UnsupportedOperationException();
    }

    default @Nullable Bone getVirtualSeat() {
        throw new UnsupportedOperationException();
    }

    default void setVirtualSeat(Bone virtualSeat) {
        throw new UnsupportedOperationException();
    }

}
