package de.tomalbrc.skillcore.util;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;

public class DamageMappingUtil {
    /**
     * Checks if a Vanilla DamageSource matches a Spigot DamageCause string.
     *
     * @param source The Vanilla DamageSource object (e.g. from LivingEntity#getLastDamageSource)
     * @param spigotEnumName The string name of the Spigot DamageCause (e.g. "ENTITY_ATTACK")
     * @return true if they are equivalent
     */
    public static boolean matches(DamageSource source, String spigotEnumName) {
        if (source == null || spigotEnumName == null) return false;

        return switch (spigotEnumName.toUpperCase()) {
            case "ENTITY_ATTACK" -> source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.PLAYER_ATTACK);
            case "ENTITY_SWEEP_ATTACK" -> source.is(DamageTypes.PLAYER_ATTACK);
            case "PROJECTILE" -> source.is(DamageTypeTags.IS_PROJECTILE);
            case "THORNS" -> source.is(DamageTypes.THORNS);
            case "SONIC_BOOM" -> source.is(DamageTypes.SONIC_BOOM);
            case "BLOCK_EXPLOSION" -> source.is(DamageTypeTags.IS_EXPLOSION) && source.getDirectEntity() == null;
            case "ENTITY_EXPLOSION" -> source.is(DamageTypeTags.IS_EXPLOSION) && source.getDirectEntity() != null;
            case "CONTACT" -> source.is(DamageTypes.CACTUS)
                    || source.is(DamageTypes.SWEET_BERRY_BUSH)
                    || source.is(DamageTypes.STALAGMITE);
            case "FALL" -> source.is(DamageTypeTags.IS_FALL);
            case "FALLING_BLOCK" -> source.is(DamageTypes.FALLING_BLOCK) || source.is(DamageTypes.FALLING_ANVIL);
            case "FLY_INTO_WALL" -> source.is(DamageTypes.FLY_INTO_WALL);
            case "HOT_FLOOR" -> source.is(DamageTypes.HOT_FLOOR); // Magma blocks
            case "SUFFOCATION" -> source.is(DamageTypes.IN_WALL);
            case "CRAMMING" -> source.is(DamageTypes.CRAMMING);
            case "DROWNING" -> source.is(DamageTypes.DROWN);
            case "DRYOUT" -> source.is(DamageTypes.DRY_OUT);
            case "FREEZE" -> source.is(DamageTypes.FREEZE);
            case "STARVATION" -> source.is(DamageTypes.STARVE);
            case "VOID" -> source.is(DamageTypes.FELL_OUT_OF_WORLD);
            case "WORLD_BORDER" -> source.is(DamageTypes.OUTSIDE_BORDER);
            case "FIRE" -> source.is(DamageTypes.IN_FIRE);
            case "FIRE_TICK" -> source.is(DamageTypes.ON_FIRE);
            case "LAVA" -> source.is(DamageTypes.LAVA);
            case "MELTING" -> source.is(DamageTypes.ON_FIRE);
            case "CAMPFIRE" -> source.is(DamageTypes.CAMPFIRE);
            case "MAGIC" -> source.is(DamageTypes.MAGIC) || source.is(DamageTypes.INDIRECT_MAGIC);
            case "POISON" -> source.is(DamageTypes.MAGIC);
            case "WITHER" -> source.is(DamageTypes.WITHER);
            case "DRAGON_BREATH" -> source.is(DamageTypes.DRAGON_BREATH);
            case "LIGHTNING" -> source.is(DamageTypes.LIGHTNING_BOLT);
            case "KILL" -> source.is(DamageTypes.GENERIC_KILL);
            case "SUICIDE" -> source.is(DamageTypes.GENERIC_KILL);
            case "CUSTOM" -> source.is(DamageTypes.GENERIC);
            default -> false;
        };
    }
}