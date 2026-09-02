package de.tomalbrc.skillcore.impl.condition;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

public class IsInSurvivalModeCondition extends AbstractCondition {
    public boolean test(SkillTree tree, Target target) {
        if (!(target.getEntity() instanceof ServerPlayer p)) return false;
        return p.gameMode.getGameModeForPlayer() == GameType.SURVIVAL;
    }
}
