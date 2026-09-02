package de.tomalbrc.skillcore.impl.mechanic.aura;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.impl.MetaSkillRef;
import de.tomalbrc.skillcore.impl.mechanic.AbstractMechanic;
import de.tomalbrc.skillcore.impl.variable.Resolvable;

public abstract class AbstractAuraMechanic extends AbstractMechanic {
    @SerializedName(value="auraname", alternate={"aura","b","buff","buffname","debuff","debuffname","n","name"})
    public Resolvable<String> auraName = Resolvable.literal("");

    @SerializedName(value="auratype", alternate={"auragroup","group","g"})
    public Resolvable<String> auraType = Resolvable.literal("");

    @SerializedName(value="onstartskill", alternate={"onstart","os"})
    public MetaSkillRef onStartSkill = new MetaSkillRef();

    @SerializedName(value="ontickskill", alternate={"ontick","ot","m","meta","s","skill"})
    public MetaSkillRef onTickSkill = new MetaSkillRef();

    @SerializedName(value="onendskill", alternate={"onend","oe"})
    public MetaSkillRef onEndSkill = new MetaSkillRef();

    @SerializedName(value="showbartimer", alternate={"bartimer","bt"})
    public boolean showBar;

    @SerializedName(value="charges", alternate={"c", "ch"})
    public int charges = 0;

    @SerializedName(value="duration", alternate={"ticks","t","d","time"})
    public Resolvable<Integer> duration = Resolvable.literal(200);

    @SerializedName(value="interval", alternate={"i"})
    public Resolvable<Integer> interval = Resolvable.literal(1);

    @SerializedName(value="maxstacks", alternate={"ms"})
    public Resolvable<Integer> maxStacks = Resolvable.literal(1);

    @SerializedName(value="refreshduration", alternate={"rd"})
    public boolean refreshDuration = true;

    @SerializedName(value="mergesamecaster", alternate={"msc","mc"})
    public boolean mergeSameCaster;

    @SerializedName(value="mergeall", alternate={"ma"})
    public boolean mergeAll;

    @SerializedName(value="overwritesamecaster", alternate={"osc","oc"})
    public boolean overwriteSameCaster;

    @SerializedName(value="overwriteall", alternate={"overwrite","ow"})
    public boolean overwriteAll;

    @SerializedName(value="cancelongivedamage", alternate={"cogd"})
    public boolean cancelOnGiveDamage;

    @SerializedName(value="cancelontakedamage", alternate={"cotd"})
    public boolean cancelOnTakeDamage;

    @SerializedName(value="cancelondeath", alternate={"cod"})
    public boolean cancelOnDeath = true;

    @SerializedName(value="canceloncasterdeath", alternate={"cocd"})
    public boolean cancelOnCasterDeath;

    @SerializedName(value="cancelonteleport", alternate={"cot"})
    public boolean cancelOnTeleport;

    @SerializedName(value="cancelonchangeworld", alternate={"cocw"})
    public boolean cancelOnChangeWorld;

    @SerializedName(value="cancelonskilluse", alternate={"cosu"})
    public boolean cancelOnSkillUse;

    @SerializedName(value="cancelonquit", alternate={"coq"})
    public boolean cancelOnQuit = true;

    @SerializedName(value="doendskillonterminate", alternate={"desot","ares","alwaysrunendskill"})
    public boolean runEndSkillOnTerminate = true;
}
