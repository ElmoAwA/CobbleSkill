package de.tomalbrc.skillcore.impl.mechanic.cobblemon;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.mechanic.AbstractMechanic;
import de.tomalbrc.skillcore.registry.MetaSkillRegistry;
import de.tomalbrc.skillcore.util.WeightedSkillList;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class RandomBattleSkillMechanic extends AbstractMechanic {
    @Override
    public ExecutionResult execute(SkillTree tree) {
        for (Target target : tree.getCurrentTargets()) {
            if (target.getEntity() instanceof PokemonEntity pokemon) {
                List<WeightedSkillList.SkillEntry> available = new ArrayList<>();
                for (Move move : pokemon.getPokemon().getMoveSet()) {
                    var name = move.getName();
                    var skill = MetaSkillRegistry.get(name);
                    if (skill != null) new WeightedSkillList.SkillEntry(name, 1);
                }

                WeightedSkillList list = new WeightedSkillList(available);
                var random = list.pickName();
                if (random != null) {
                    SkillCore.LOGGER.info("Running PokeSkill: {}", random);

                    var meta = MetaSkillRegistry.getOptional(random);
                    meta.ifPresent(metaSkill -> metaSkill.castAsync(tree));
                }
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.RANDOM_BATTLE_SKILL;
    }
}
