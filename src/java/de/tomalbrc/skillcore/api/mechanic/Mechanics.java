package de.tomalbrc.skillcore.api.mechanic;

import de.tomalbrc.skillcore.impl.mechanic.*;
import de.tomalbrc.skillcore.impl.mechanic.aura.AuraMechanic;
import de.tomalbrc.skillcore.impl.mechanic.aura.AuraRemoveMechanic;
import de.tomalbrc.skillcore.impl.mechanic.cobblemon.BedrockParticleMechanic;
import de.tomalbrc.skillcore.impl.mechanic.cobblemon.CobblemonAnimationMechanic;
import de.tomalbrc.skillcore.impl.mechanic.cobblemon.RandomBattleSkillMechanic;
import de.tomalbrc.skillcore.impl.mechanic.effect.*;
import de.tomalbrc.skillcore.impl.mechanic.model.DefaultStateMechanic;
import de.tomalbrc.skillcore.impl.mechanic.model.ModelMechanic;
import de.tomalbrc.skillcore.impl.mechanic.model.PartVisibilityMechanic;
import de.tomalbrc.skillcore.impl.mechanic.model.StateMechanic;
import de.tomalbrc.skillcore.impl.mechanic.projectile.OrbitalMechanic;
import de.tomalbrc.skillcore.impl.mechanic.projectile.ProjectileMechanic;
import de.tomalbrc.skillcore.impl.mechanic.projectile.TotemMechanic;
import de.tomalbrc.skillcore.impl.mechanic.variable.SetVariableMechanic;
import de.tomalbrc.skillcore.impl.mechanic.variable.VariableAddMechanic;
import de.tomalbrc.skillcore.io.RuntimeTypeAdapterFactoryWithAliases;
import de.tomalbrc.skillcore.util.Util;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@SuppressWarnings("unused")
public class Mechanics {
    public static RuntimeTypeAdapterFactoryWithAliases<Mechanic> TYPE_ADAPTER_FACTORY = RuntimeTypeAdapterFactoryWithAliases.of(Mechanic.class, "type");

    public static ResourceLocation register(ResourceLocation id, Class<? extends Mechanic> type) {
        register(id, type, id.getPath());
        return id;
    }

    public static ResourceLocation register(ResourceLocation id, Class<? extends Mechanic> type, String... aliases) {
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

    public static ResourceLocation DELAY = register(Util.id("delay"), DelayMechanic.class);
    public static ResourceLocation CANCEL_EVENT = register(Util.id("cancel_event"), CancelEventMechanic.class, "cancelevent");
    public static ResourceLocation CANCEL_SKILL = register(Util.id("cancel_skill"), CancelSkillMechanic.class, "cancel", "return");
    public static ResourceLocation DAMAGE = register(Util.id("damage"), DamageMechanic.class);
    public static ResourceLocation BASEDAMAGE = register(Util.id("basedamage"), BaseDamageMechanic.class);
    public static ResourceLocation PERCENTDAMAGE = register(Util.id("percentdamage"), PercentDamageMechanic.class, "damagepercent");
    public static ResourceLocation CONSUME = register(Util.id("consume"), ConsumeMechanic.class);
    public static ResourceLocation STATE = register(Util.id("state"), StateMechanic.class, "animation");
    public static ResourceLocation MESSAGE = register(Util.id("message"), MessageMechanic.class);
    public static ResourceLocation LOG = register(Util.id("log"), LogMechanic.class, "console", "print", "prinft");
    public static ResourceLocation SEND_ACTION_MESSAGE = register(Util.id("sendactionmessage"), SendActionMessageMechanic.class, "am", "actionmessage");
    public static ResourceLocation SEND_TITLE = register(Util.id("sendtitle"), SendTitleMechanic.class, "title");
    public static ResourceLocation POTION = register(Util.id("potion"), PotionMechanic.class);
    public static ResourceLocation SKILL = register(Util.id("skill"), SkillMechanic.class);
    public static ResourceLocation SUDOSKILL = register(Util.id("sudoskill"), SudoSkillMechanic.class);
    public static ResourceLocation FOR_EACH = register(Util.id("for_each"), ForEachSkillMechanic.class, "foreach");
    public static ResourceLocation RANDOM_SKILL = register(Util.id("randomskill"), RandomSkillMechanic.class, "randommeta");
    public static ResourceLocation SET_VARIABLE = register(Util.id("set_variable"), SetVariableMechanic.class, "setvariable");
    public static ResourceLocation VARIABLE_ADD = register(Util.id("variableadd"), VariableAddMechanic.class, "addvariable", "varadd", "addvar", "incrementvariable");
    public static ResourceLocation SOUND = register(Util.id("sound"), SoundMechanic.class, "effect:sound", "s", "e:sound", "e:s");
    public static ResourceLocation ENDER_EFFECT = register(Util.id("ender_effect"), EnderEffectMechanic.class, "ender", "effect:ender", "e:ender");
    public static ResourceLocation TOTEM = register(Util.id("totem"), TotemMechanic.class);
    public static ResourceLocation ORBITAL = register(Util.id("orbital"), OrbitalMechanic.class, "o");
    public static ResourceLocation PROJECTILE = register(Util.id("projectile"), ProjectileMechanic.class);
    public static ResourceLocation PARTICLE_EFFECT = register(Util.id("particle"), ParticleEffectMechanic.class, "effect:particles", "effect:particle", "particles", "e:particles", "e:particle", "e:p");
    public static ResourceLocation PARTICLE_RING_EFFECT = register(Util.id("particle_ring"), ParticleRingEffectMechanic.class, "pr", "e:pr", "effect:particlering", "particlering");
    public static ResourceLocation PARTICLE_LINE_EFFECT = register(Util.id("particle_line"), ParticleLineEffectMechanic.class, "pl", "e:pl", "effect:particleline", "particleline");
    public static ResourceLocation PARTICLE_LINE_HELIX = register(Util.id("particle_line_helix"), ParticleLineHelix.class, "particlelinehelix", "effect:particlelinehelix", "particlehelixline");
    public static ResourceLocation PARTICLE_SPHERE = register(Util.id("particle_sphere"), ParticleSphereEffectMechanic.class, "effect:particlesphere", "e:ps", "ps", "particlesphere");
    public static ResourceLocation PARTICLE_BOX = register(Util.id("particle_box"), ParticleBoxMechanic.class, "effect:particlebox", "e:pb", "pb", "particlebox");
    public static ResourceLocation PARTICLE_TORNADO = register(Util.id("particle_tornado"), ParticleTornadoMechanic.class, "effect:particletornado", "e:pt");

    public static ResourceLocation FEED = register(Util.id("feed"), FeedMechanic.class);
    public static ResourceLocation FREEZE = register(Util.id("freeze"), FreezeMechanic.class);
    public static ResourceLocation HEAL = register(Util.id("heal"), HealMechanic.class);
    public static ResourceLocation SETAI = register(Util.id("setai"), SetAiMechanic.class, "ai");
    public static ResourceLocation RESETAI = register(Util.id("resetai"), ResetAiMechanic.class, "resetaigoals");
    public static ResourceLocation OXYGEN = register(Util.id("oxygen"), OxygenMechanic.class);
    public static ResourceLocation GEYSER = register(Util.id("geyser"), GeyserMechanic.class, "e:geyser", "effect:geyser");
    public static ResourceLocation IGNITE = register(Util.id("ignite"), IgniteMechanic.class);
    public static ResourceLocation EXTINGUISH = register(Util.id("extinguish"), ExtinguishMechanic.class, "removefire");
    public static ResourceLocation LIGHTNING = register(Util.id("lightning"), LightningMechanic.class);
    public static ResourceLocation FAKELIGHTNING = register(Util.id("fake_lightning"), FakeLightningMechanic.class, "fakelightning");
    public static ResourceLocation BONEMEAL = register(Util.id("bonemeal"), BoneMealMechanic.class);

    public static ResourceLocation CLEAR_EXPERIENCE = register(Util.id("clearexperience"), ClearExperienceMechanic.class, "clearexp");
    public static ResourceLocation CLEAR_EXPERIENCE_LEVELS = register(Util.id("clearexperiencelevels"), ClearExperienceLevelsMechanic.class, "clearexplevels");
    public static ResourceLocation GIVE_EXPERIENCE_LEVELS = register(Util.id("giveexperiencelevels"), GiveExperienceLevelsMechanic.class, "giveexplevels");
    public static ResourceLocation SUICIDE = register(Util.id("suicide"), SuicideMechanic.class);
    public static ResourceLocation REMOVE = register(Util.id("remove"), RemoveMechanic.class, "delete");
    public static ResourceLocation RUN_AI_GOAL_SELECTOR = register(Util.id("run_ai_goal_selector"), RunAiGoalSelectorMechanic.class, "runaigoalselector", "aigoal", "aigoals");
    public static ResourceLocation RUN_AI_TARGET_SELECTOR = register(Util.id("run_ai_target_selector"), RunAiTargetSelectorMechanic.class, "runaitargetselector", "aitarget");
    public static ResourceLocation SWAP = register(Util.id("swap"), SwapMechanic.class, "tpswap");
    public static ResourceLocation SET_TARGET = register(Util.id("settarget"), SetTargetMechanic.class, "target");
    public static ResourceLocation SET_STANCE = register(Util.id("setstance"), SetStanceMechanic.class, "stance");
    public static ResourceLocation PULL = register(Util.id("pull"), PullMechanic.class);
    public static ResourceLocation FORCEPULL = register(Util.id("forcepull"), ForcePullMechanic.class);
    public static ResourceLocation TELEPORT = register(Util.id("teleport"), TeleportMechanic.class);
    public static ResourceLocation TELEPORT_IN = register(Util.id("teleportin"), TeleportInMechanic.class, "tpin", "tpdir", "tpi");
    public static ResourceLocation PROPEL = register(Util.id("propel"), PropelMechanic.class);
    public static ResourceLocation LEAP = register(Util.id("leap"), LeapMechanic.class);
    public static ResourceLocation LUNGE = register(Util.id("lunge"), LungeMechanic.class);
    public static ResourceLocation THROW = register(Util.id("throw"), ThrowMechanic.class);
    public static ResourceLocation STUN = register(Util.id("stun"), StunMechanic.class);
    public static ResourceLocation JUMP = register(Util.id("jump"), JumpMechanic.class);
    public static ResourceLocation FAKEEXPLOSION = register(Util.id("fakeexplosion"), FakeExplosionMechanic.class, "effect:explosion", "e:explosion", "effect:explode", "fakeexplode");
    public static ResourceLocation EXPLOSION = register(Util.id("explosion"), ExplosionMechanic.class, "explode");
    public static ResourceLocation SHOOTFIREBALL = register(Util.id("shootfireball"), ShootFireballMechanic.class, "fireball");
    public static ResourceLocation SETBLOCKTYPE = register(Util.id("setblocktype"), SetBlockTypeMechanic.class, "setblock");
    public static ResourceLocation MODIFYMOBSCORE = register(Util.id("modifymobscore"), ModifyMobScoreMechanic.class, "mms");
    public static ResourceLocation BREAK_BLOCK = register(Util.id("breakblock"), BreakBlockMechanic.class, "blockbreak");
    public static ResourceLocation SMOKESWIRL = register(Util.id("smokeswirl"), SmokeSwirlMechanic.class, "smokeswirl", "e:smokeswirl");
    public static ResourceLocation TAG_ADD = register(Util.id("tagadd"), TagAddMechanic.class, "addscoreboardtag", "addtag");
    public static ResourceLocation TAG_REMOVE = register(Util.id("tagremove"), TagRemoveMechanic.class, "removescoreboardtag", "removetag");
    public static ResourceLocation SET_GLIDING = register(Util.id("setgliding"), SetGlidingMechanic.class);
    public static ResourceLocation SET_GRAVITY = register(Util.id("setgravity"), SetGravityMechanic.class, "setusegravity");

    public static ResourceLocation AURA = register(Util.id("aura"), AuraMechanic.class, "buff", "debuff");
    public static ResourceLocation AURAREMOVE = register(Util.id("auraremove"), AuraRemoveMechanic.class, "removeaura", "removebuff", "removedebuff");
    public static ResourceLocation SPIN = register(Util.id("spin"), SpinMechanic.class, "effect:spin", "e:spin");
    public static ResourceLocation ITEMSPRAY = register(Util.id("itemspray"), ItemSprayMechanic.class, "effect:itemspray", "e:itemspray");
    public static ResourceLocation BLOCKMASK = register(Util.id("blockmask"), BlockMaskMechanic.class, "effect:blockmask", "e:blockmask");
    public static ResourceLocation BLOCKUNMASK = register(Util.id("blockunmask"), BlockUnmaskMechanic.class, "effect:blockunmask", "e:blockunmask");
    public static ResourceLocation MOUNTTARGET = register(Util.id("mounttarget"), MountTargetMechanic.class);
    public static ResourceLocation DISMOUNT_ALL = register(Util.id("dismountall"), DismountAllModelMechanic.class, "dismountallmodel");
    public static ResourceLocation SUMMON = register(Util.id("summon"), SummonMechanic.class, "spawnmobs", "spawnmob", "piratesummon");
    public static ResourceLocation DECAPITATE = register(Util.id("decapitate"), DecapitateMechanic.class, "drophead");
    public static ResourceLocation CLEAR_TARGET = register(Util.id("cleartarget"), ClearTargetMechanic.class, "resettarget");
    public static ResourceLocation CLEAR_THREAT = register(Util.id("clearthreat"), ClearThreatMechanic.class, "threatclear");
    public static ResourceLocation THREAT = register(Util.id("threat"), ThreatMechanic.class, "threatchange", "threatmod");
    public static ResourceLocation TAUNT = register(Util.id("taunt"), TauntMechanic.class);

    public static ResourceLocation SHOOT = register(Util.id("shoot"), ShootMechanic.class);
    public static ResourceLocation EQUIP = register(Util.id("equip"), EquipMechanic.class);
    public static ResourceLocation LOOK = register(Util.id("look"), LookMechanic.class);
    public static ResourceLocation SETSPEED = register(Util.id("setspeed"), SetSpeedMechanic.class);
    public static ResourceLocation BLOODY_SCREEN = register(Util.id("bloody_screen"), BloodyScreenMechanic.class, "bloodyscreen", "effect:bloodyscreen", "e:bloodyscreen", "redscreen", "effect:redscreen", "e:redscreen");
    public static ResourceLocation SETLEVEL = register(Util.id("set_level"), SetLevelMechanic.class, "setLevel", "setlevel");
    public static ResourceLocation SIGNAL = register(Util.id("signal"), SignalMechanic.class, "sendsignal");
    public static ResourceLocation GLOBAL_COOLDOWN = register(Util.id("globalcooldown"), GlobalCooldownMechanic.class, "gcd", "setgcd", "setglobalcooldown");
    public static ResourceLocation BAR_SET = register(Util.id("barset"), BarSetMechanic.class);
    public static ResourceLocation BAR_ADD = register(Util.id("baradd"), BarAddMechanic.class);
    public static ResourceLocation BAR_REMOVE = register(Util.id("barremove"), BarRemoveMechanic.class);

    public static ResourceLocation ARROWVOLLEY = register(Util.id("arrowvolley"), ArrowVolleyMechanic.class);
    public static ResourceLocation SLASH = register(Util.id("slash"), SlashMechanic.class);
    public static ResourceLocation GOTO = register(Util.id("goto"), GotoMechanic.class);
    public static ResourceLocation WOLF_SIT = register(Util.id("wolfsit"), WolfSitMechanic.class);

    public static ResourceLocation MODEL = register(Util.id("model"), ModelMechanic.class);
    public static ResourceLocation LOCK_MODEL = register(Util.id("lockmodel"), LockModelMechanic.class, "lockrotation");
    public static ResourceLocation MOUNT_MODEL = register(Util.id("mountmodel"), MountModelMechanic.class);
    public static ResourceLocation PART_VISIBILITY = register(Util.id("partvis"), PartVisibilityMechanic.class, "partvisibility");
    public static ResourceLocation DEFAULT_STATE = register(Util.id("defaultstate"), DefaultStateMechanic.class, "defaultanimation");
    public static ResourceLocation RANDOM_BATTLE_SKILL = register(Util.id("randombattleskill"), RandomBattleSkillMechanic.class, "battleskill", "pokeskill");
    public static ResourceLocation COBBLEMON_ANIMATION = register(Util.id("cobblemon_animation"), CobblemonAnimationMechanic.class, "canimation", "cobbleanim");
    public static ResourceLocation BEDROCK_PARTICLE = register(Util.id("bedrock_particle"), BedrockParticleMechanic.class, "e:bp", "bparticle");
}
