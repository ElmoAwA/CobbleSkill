package de.tomalbrc.skillcore.api.target;

import de.tomalbrc.skillcore.impl.target.*;
import de.tomalbrc.skillcore.io.RuntimeTypeAdapterFactoryWithAliases;
import de.tomalbrc.skillcore.util.Util;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@SuppressWarnings("unused")
public class Targeters {
    public static RuntimeTypeAdapterFactoryWithAliases<Targeter> TYPE_ADAPTER_FACTORY = RuntimeTypeAdapterFactoryWithAliases.of(Targeter.class, "type");

    public static ResourceLocation register(ResourceLocation id, Class<? extends Targeter> type) {
        register(id, type, id.getPath());
        return id;
    }

    public static ResourceLocation register(ResourceLocation id, Class<? extends Targeter> type, String... aliases) {
        Set<String> al = new HashSet<>(Arrays.stream(aliases).toList());
        for (String alias : aliases) {
            var l = alias.toLowerCase(Locale.ROOT);
            al.add(l);
        }
        al.add(id.getPath().replace("_", ""));
        al.removeIf(x -> x.equals(id.getPath()));

        TYPE_ADAPTER_FACTORY.registerSubtypeWithAliases(type, id.getPath(), al.toArray(new String[0]));
        return id;
    }

    public static final ResourceLocation NONE = register(Util.id("none"), NoneTargeter.class, "None");
    public static final ResourceLocation TARGET = register(Util.id("target"), TargetTargeter.class, "T", "Target");
    public static final ResourceLocation TRIGGER = register(Util.id("trigger"), TriggerTargeter.class, "Trigger");
    public static final ResourceLocation VEHICLE = register(Util.id("vehicle"), VehicleTargeter.class, "Vehicle");

    public static final ResourceLocation SELF = register(Util.id("self"), SelfTargeter.class, "Self", "Boss", "Mob");
    public static final ResourceLocation CASTER = register(Util.id("caster"), CasterTargeter.class);

    public static final ResourceLocation SELF_LOCATION = register(Util.id("self_location"), SelfLocationTargeter.class, "SelfLocation", "casterLocation", "bossLocation", "mobLocation");
    public static final ResourceLocation SELF_EYE_LOCATION = register(Util.id("self_eye_location"), SelfEyeLocationTargeter.class, "SelfEyeLocation", "eyeDirection", "casterEyeLocation", "bossEyeLocation", "mobEyeLocation");
    public static final ResourceLocation PLAYERS_IN_RADIUS = register(Util.id("players_in_radius"), PlayersInRadiusTargeter.class, "PlayersInRadius", "PIR");
    public static final ResourceLocation PLAYERS_IN_RING = register(Util.id("players_in_ring"), PlayersInRingTargeter.class, "PlayersInRing");
    public static final ResourceLocation PLAYERS_IN_WORLD = register(Util.id("players_in_world"), PlayersInWorldTargeter.class, "world", "PlayersInWorld");
    public static final ResourceLocation TRACKED = register(Util.id("tracked_players"), TrackedTargeter.class, "tracked", "TrackedPlayers");
    public static final ResourceLocation OWNER = register(Util.id("owner"), OwnerTargeter.class, "Owner");
    public static final ResourceLocation MOBS_IN_RADIUS = register(Util.id("mobs_in_radius"), MobsInRadiusTargeter.class, "MobsInRadius", "MIR");
    public static final ResourceLocation ORIGIN = register(Util.id("origin"), OriginTargeter.class, "Origin");
    public static final ResourceLocation ITEMS_IN_RADIUS = register(Util.id("items_in_radius"), ItemsInRadiusTargeter.class, "ItemsInRadius", "IIR");
    public static final ResourceLocation ENTITIES_IN_RADIUS = register(Util.id("entities_in_radius"), EntitiesInRadiusTargeter.class, "EntitiesInRadius", "livingEntitiesInRadius", "LivingEntitiesInRadius", "livingInRadius", "allInRadius", "EIR", "a"); // TODO: figure out which targeter @a actually is
    public static final ResourceLocation ENTITIES_IN_RING = register(Util.id("entities_in_ring"), EntitiesInRingTargeter.class, "EntitiesInRing", "EIRR");
    public static final ResourceLocation LIVING_IN_LINE = register(Util.id("living_in_line"), LivingInLineTargeter.class, "LivingInLine", "entitiesInLine", "livingEntitiesInLine", "LEIL", "EIL");
    public static final ResourceLocation ENTITIES_NEAR_ORIGIN = register(Util.id("entities_near_origin"), EntitiesNearOriginTargeter.class, "EntitiesNearOrigin");
    public static final ResourceLocation PLAYERS_NEAR_ORIGIN = register(Util.id("players_near_origin"), PlayersNearOriginTargeter.class, "PlayersNearOrigin");
    public static final ResourceLocation NEAREST_PLAYER = register(Util.id("nearest_player"), NearestPlayerTargeter.class, "NearestPlayer");
    public static final ResourceLocation FORWARD = register(Util.id("forward"), ForwardTargeter.class, "Forward");
    public static final ResourceLocation RING = register(Util.id("ring"), RingTargeter.class, "Ring");
    public static final ResourceLocation THREAT_TABLE = register(Util.id("threattable"), ThreatTableTargeter.class, "TT");
    public static final ResourceLocation THREAT_TABLE_PLAYERS = register(Util.id("threattableplayers"), ThreatTablePlayersTargeter.class);
    public static final ResourceLocation RECTANGLE = register(Util.id("rectangle"), RectangleTargeter.class, "Rectangle");
    public static final ResourceLocation TARGET_LOCATION = register(Util.id("targetlocation"), TargetLocationTargeter.class, "targetLoc", "TL");
    public static final ResourceLocation RANDOM_LOCATIONS_NEAR_TARGETS = register(Util.id("random_locations_near_target"), RandomLocationsNearTargetsTargeter.class, "RandomLocationsNearTargets", "randomLocationsNearTarget", "randomLocationsNearTargetEntities", "randomLocationsNearTargetLocations", "RLNT", "RLNTE", "RLNTL");
    public static final ResourceLocation RANDOM_LOCATIONS_NEAR_ORIGIN = register(Util.id("random_locations_near_origin"), RandomLocationsNearOriginTargeter.class, "RandomLocationsNearOrigin", "RLO", "randomLocationsOrigin", "RLNO");
    public static final ResourceLocation RANDOM_LOCATIONS_NEAR_CASTER = register(Util.id("random_locations_near_caster"), RandomLocationsNearCasterTargeter.class, "RandomLocationsNearCaster", "RLC", "randomLocationsCaster", "RLNC");
    public static final ResourceLocation OBSTRUCTING_BLOCK = register(Util.id("obstructingblock"), ObstructingBlockTargeter.class);
    public static final ResourceLocation WOLF_OWNER = register(Util.id("wolfowner"), WolfOwnerTargeter.class);

    public static final ResourceLocation MODEL_PASSENGERS = register(Util.id("modelpassengers"), ModelPassengersTargeter.class);
    public static final ResourceLocation MODEL_PART = register(Util.id("modelpart"), ModelPartTargeter.class);
}
