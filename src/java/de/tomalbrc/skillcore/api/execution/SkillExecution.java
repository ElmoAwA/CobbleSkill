package de.tomalbrc.skillcore.api.execution;

import de.tomalbrc.skillcore.api.Skill;

// execution state for a skill (from sequence and trigger)
public class SkillExecution {
    public final Skill skill;
    public int delayRemaining = 0;
    public int repeatsRemaining = 0;
    public int repeatInterval = 0;
    public int targetInterval = 0;
    public int targetIntervalRemaining = 0;

    public SkillExecution(Skill skill) {
        this.skill = skill;

        int repeat = skill.mechanic().repeat();
        int repeatInterval = skill.mechanic().repeatInterval();

        this.delayRemaining = skill.mechanic().delay();
        this.repeatInterval = repeatInterval;
        this.repeatsRemaining = repeat;

        this.targetInterval = skill.mechanic().targetInterval();
        this.targetIntervalRemaining = this.targetInterval;
    }
}