package de.tomalbrc.skillcore.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ThreatTable {
    private final Mob owner;
    private final Map<LivingEntity, Double> threats = new HashMap<>();
    
    private static final double AGGRO_SWITCH_THRESHOLD = 1.10; // 110%
    private static final double DECAY_RATE = 0.95;
    private static final double MAX_COMBAT_RANGE = 32.0;

    public ThreatTable(Mob owner) {
        this.owner = owner;
    }

    public void addThreat(LivingEntity source, double amount) {
        threats.merge(source, amount, Double::sum);
    }

    public void remove(LivingEntity source) {
        threats.remove(source);
    }

    public void setThreat(LivingEntity source, double amount) {
        threats.put(source, amount);
    }

    public double getThreat(LivingEntity source) {
        return getThreat(source, 0.);
    }

    public double getThreat(LivingEntity source, double def) {
        return threats.getOrDefault(source, def);
    }

    public Set<LivingEntity> getAll() {
        return threats.keySet();
    }

    /**
     * Calculates the best target.
     * Only switches from current target if new target has > 110% of current target's threat.
     */
    public LivingEntity getTopThreatTarget() {
        cleanInvalidTargets();

        if (threats.isEmpty()) return null;

        LivingEntity highestThreatEntity = null;
        double maxThreat = -1.0;

        for (Map.Entry<LivingEntity, Double> entry : threats.entrySet()) {
            if (entry.getValue() > maxThreat) {
                maxThreat = entry.getValue();
                highestThreatEntity = entry.getKey();
            }
        }

        LivingEntity currentTarget = owner.getTarget();

        if (currentTarget == null || !threats.containsKey(currentTarget)) {
            return highestThreatEntity;
        }

        double currentThreat = getThreat(currentTarget);

        // sticky target
        if (highestThreatEntity != currentTarget && maxThreat > (currentThreat * AGGRO_SWITCH_THRESHOLD)) {
            return highestThreatEntity;
        }

        return currentTarget;
    }

    public void tick() {
        if (owner.tickCount % 20 != 0) return;

        cleanInvalidTargets();

        for (Map.Entry<LivingEntity, Double> entry : threats.entrySet()) {
            LivingEntity target = entry.getKey();
            
            boolean outOfRange = target.distanceToSqr(owner) > (MAX_COMBAT_RANGE * MAX_COMBAT_RANGE);
            boolean noLineOfSight = !owner.getSensing().hasLineOfSight(target);

            if (outOfRange || noLineOfSight) {
                entry.setValue(entry.getValue() * DECAY_RATE);
            }
        }
    }

    private void cleanInvalidTargets() {
        threats.keySet().removeIf(e -> 
            e.isRemoved() || 
            e.isDeadOrDying() || 
            (e instanceof ServerPlayer p && p.hasDisconnected())
        );
    }
    
    public void clear() {
        threats.clear();
    }
}