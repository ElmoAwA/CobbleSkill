package de.tomalbrc.skillcore.impl.condition.cobblemon;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.condition.AbstractCondition;

public class HasAspectCondition extends AbstractCondition {
    @SerializedName(value = "a", alternate = {"aspect", "asp"})
    String aspect;

    public boolean test(SkillTree tree, Target target) {
        return target.getEntity() instanceof PokemonEntity pokemonEntity && pokemonEntity.getPokemon().getAspects().contains(aspect);
    }
}