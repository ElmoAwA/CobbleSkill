package de.tomalbrc.skillcore.impl.condition.cobblemon;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.condition.AbstractCondition;

public class WillDefendCondition extends AbstractCondition {
    public boolean test(SkillTree tree, Target target) {
        return target.getEntity() instanceof PokemonEntity pokemonEntity && (pokemonEntity.getPokemon().getSpecies().getBehaviour().getCombat().getWillDefendOwner() || pokemonEntity.getPokemon().getSpecies().getBehaviour().getCombat().getWillDefendSelf());
    }
}