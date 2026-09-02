package de.tomalbrc.skillcore.data;

import com.google.gson.annotations.SerializedName;

public record MobOptions(
        @SerializedName("AlwaysShowName") boolean alwaysShowName,
        @SerializedName("Despawn") Boolean despawn,
        @SerializedName("AttackSpeed") Float attackSpeed,
        @SerializedName("VisibleByDefault") boolean visibleByDefault,
        @SerializedName("Invisible") boolean invisible,
        @SerializedName("Collidable") boolean collidable,
        @SerializedName("DigOutOfGround") boolean digOutOfGround,
        @SerializedName("FollowRange") Float followRange,
        @SerializedName("Glowing") boolean glowing,
        @SerializedName("HealOnReload") boolean healOnReload,
        @SerializedName("Invincible") boolean invincible,
        @SerializedName("Interactable") boolean interactable,
        @SerializedName("LockPitch") boolean lockPitch,
        @SerializedName("KnockbackResistance") Float knockbackResistance,
        @SerializedName("MaxCombatDistance") Float maxCombatDistance,
        @SerializedName("MovementSpeed") Float movementSpeed,
        @SerializedName("NoAI") boolean noAI,
        @SerializedName("NoDamageTicks") int noDamageTicks,
        @SerializedName("NoGravity") boolean noGravity,
        @SerializedName("PassthroughDamage") boolean passthroughDamage,
        @SerializedName("PreventItemPickup") boolean preventItemPickup,
        @SerializedName("PreventLeashing") boolean preventLeashing,
        @SerializedName("PreventMobKillDrops") boolean preventMobKillDrops,
        @SerializedName("PreventOtherDrops") boolean preventOtherDrops,
        @SerializedName("PreventRandomEquipment") boolean preventRandomEquipment,
        @SerializedName("PreventRenaming") boolean preventRenaming,
        @SerializedName("PreventSunburn") boolean preventSunburn,
        @SerializedName("PreventTransformation") boolean preventTransformation,
        @SerializedName("PreventVanillaDamage") boolean preventVanillaDamage,
        @SerializedName("RepeatAllSkills") boolean repeatAllSkills,
        @SerializedName("ReviveHealth") Float reviveHealth,
        @SerializedName("Scale") Float scale,
        @SerializedName("Size") Integer size,
        @SerializedName("ShowHealth") boolean showHealth,
        @SerializedName("Silent") boolean silent,
        @SerializedName("UseThreatTable") boolean useThreatTable,
        @SerializedName("RandomizeProperties") boolean randomizeProperties,

        @SerializedName("Age") Integer age,
        @SerializedName("AgeLock") boolean ageLock,
        @SerializedName("Adult") boolean adult,
        @SerializedName("Baby") boolean baby,
        //color
        @SerializedName("Angry") boolean angry,
        @SerializedName("PreventSlimeSplit") boolean preventSlimeSplit
) {
}
