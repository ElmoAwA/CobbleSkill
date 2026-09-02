package de.tomalbrc.skillcore.impl.mechanic.projectile;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.condition.Condition;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.api.target.Targeter;
import de.tomalbrc.skillcore.impl.MetaSkillRef;
import de.tomalbrc.skillcore.impl.mechanic.AbstractMechanic;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public abstract class AbstractProjectileMechanic extends AbstractMechanic {
    public @SerializedName(value = "onstartskill", alternate = {"onstart", "os"}) MetaSkillRef onStartSkill = new MetaSkillRef();
    public @SerializedName(value = "ontickskill", alternate = {"ontick", "ot", "m", "meta", "s", "skill"}) MetaSkillRef onTickSkill = new MetaSkillRef();
    public @SerializedName(value = "onhitskill", alternate = {"onhit", "oh"}) MetaSkillRef onHitSkill = new MetaSkillRef();
    public @SerializedName(value = "onendskill", alternate = {"onend", "oe"}) MetaSkillRef onEndSkill = new MetaSkillRef();
    public @SerializedName(value = "onbounceskill", alternate = {"onbounce"}) MetaSkillRef onBounceSkill = new MetaSkillRef();
    public @SerializedName(value = "onhitblockskill", alternate = {"onhitblock", "ohb"}) MetaSkillRef onHitBlockSkill = new MetaSkillRef();
    public @SerializedName(value = "oninteractskill", alternate = {"oninteract"}) MetaSkillRef onInteractSkill = new MetaSkillRef();
    public @SerializedName(value = "bullettype", alternate = {"bullet", "b"}) String bulletType;

    // bulletType: ARROW
    public @SerializedName(value = "arrowtype", alternate = {"bulletarrowtype"}) String arrowtype = "";
    public @SerializedName(value = "bulletmodel", alternate = {"model"}) String bulletModel;

    public @SerializedName(value = "interval", alternate = {"int", "i"}) Integer interval = 1;
    public @SerializedName(value = "horizontalradius", alternate = {"hradius", "r", "hr"}) Resolvable<Float> horizontalRadius = Resolvable.literal(1.25f);
    public @SerializedName(value = "verticalradius", alternate = {"vradius", "vr"}) Float verticalRadius = 1.25f;
    public @SerializedName(value = "duration", alternate = {"maxduration", "md", "d"}) Integer duration = 400;
    public @SerializedName(value = "maxrange", alternate = {"mr"}) Float maxRange = 40f;
    public @SerializedName(value = "velocity", alternate = {"v"}) Float velocity = 5f;
    public @SerializedName(value = "deathdelay", alternate = {"death", "dd"}) Integer deathDelay = 2;
    public @SerializedName(value = "startyoffset", alternate = {"syo"}) Float startYOffset = 1f;
    public @SerializedName(value = "startfoffset", alternate = {"forwardoffset", "sfo"}) Float startFOffset = 1f;
    public @SerializedName(value = "targetyoffset", alternate = {"tyo", "targety"}) Float targetYOffset = 0f;
    public @SerializedName(value = "sideoffset", alternate = {"soffset", "so"}) Float sideOffset;
    public @SerializedName(value = "startsideoffset", alternate = {"ssoffset", "sso"}) Float startSideOffset;
    public @SerializedName(value = "endsideoffset", alternate = {"endoffset", "esoffset", "eso"}) Float endSideOffset;
    public @SerializedName(value = "startingdirection", alternate = {"startingdir", "startdir", "sdir"}) Object startingdirection;
    public @SerializedName(value = "horizontaloffset", alternate = {"ho"}) Float horizontalOffset = 0f;
    public @SerializedName(value = "verticaloffset", alternate = {"vo"}) Float verticalOffset = 0f;
    public @SerializedName(value = "accuracy", alternate = {"ac", "a"}) Float accuracy = 1f;
    public @SerializedName(value = "horizontalnoise", alternate = {"hn"}) Float horizontalNoise = 0f;
    public @SerializedName(value = "verticalnoise", alternate = {"vn"}) Float verticalNoise = 0f;
    public @SerializedName(value = "stopatentity", alternate = {"se"}) boolean stopAtEntity = true;
    public @SerializedName(value = "stopatblock", alternate = {"sb"}) boolean stopAtBlock = true;
    public @SerializedName(value = "poweraffectsrange", alternate = {"par"}) boolean powerAffectsRange = true;
    public @SerializedName(value = "poweraffectsvelocity", alternate = {"pav"}) boolean powerAffectsVelocity = true;
    public @SerializedName(value = "interactable") boolean interactable = false;
    public @SerializedName(value = "hitself") boolean hitSelf = false;
    public @SerializedName(value = "hitplayers", alternate = {"hp"}) boolean hitPlayers = true;
    public @SerializedName(value = "hitnonplayers", alternate = {"hnp"}) boolean hitNonPlayers = false;
    public @SerializedName(value = "hittarget", alternate = {"ht"}) boolean hitTarget = true;
    public @SerializedName(value = "hittargetonly", alternate = {"hto"}) boolean hitTargetOnly = false;
    public @SerializedName(value = "immunedelay", alternate = {"immune", "id"}) Integer immuneDelay = 2000;
    public @SerializedName(value = "hitconditions", alternate = {"conditions", "cond", "c"}) List<Condition> hitConditions = List.of();
    public @SerializedName(value = "stopconditions", alternate = {"stpcond"}) List<Condition> stopconditions = List.of();
    public @SerializedName(value = "doendskillonhit", alternate = {"esoh"}) boolean doEndSkillOnHit = true;
    public @SerializedName(value = "fromorigin", alternate = {"fo"}) boolean fromorigin = false;
    public @SerializedName(value = "requirelineofsight", alternate = {"rlos", "los", "requirelos"}) boolean requireLineOfSight;
    public @SerializedName(value = "drawhitbox") boolean drawHitbox = false;
    public @SerializedName(value = "tickinterpolation", alternate = {"interpolation", "ti"}) Integer tickinterpolation = 0;
    public @SerializedName(value = "sharesubhitboxcooldown", alternate = {"shcd"}) boolean shareSubHitboxCooldown = true;
    public @SerializedName(value = "hittargeter", alternate = {"htr"}) Targeter hitTargeter;

    public float verticalRadius(SkillTree tree, Target target) {
        return verticalRadius == null ? horizontalRadius.resolve(tree, target) : verticalRadius;
    }

    public boolean hasLineOfSight(Entity caster, Target target) {
        if (target.level() != caster.level()) {
            return false;
        } else {
            Vec3 eyePos = caster.getEyePosition();
            Vec3 tpos = new Vec3(target.getPosition().x, target.getPosition().y, target.getPosition().z);
            if (tpos.distanceTo(eyePos) > 128.) {
                return false;
            } else {
                return caster.level().clip(new ClipContext(eyePos, tpos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, caster)).getType() == HitResult.Type.MISS;
            }
        }
    }
}
