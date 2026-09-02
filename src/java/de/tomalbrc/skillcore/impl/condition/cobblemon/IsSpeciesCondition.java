package de.tomalbrc.skillcore.impl.condition.cobblemon;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.condition.AbstractCondition;

import java.util.List;

public class IsSpeciesCondition extends AbstractCondition {
    @SerializedName(value = "species", alternate = {"name", "s"})
    List<String> name;

    public boolean test(SkillTree tree, Target target) {
        if (target.getEntity() instanceof PokemonEntity pokemonEntity) {
            for (String s : name) {
                var pkmn = pokemonEntity.getPokemon();
                 if (pkmn.getSpecies().getName().equalsIgnoreCase(s))
                    return true;
            }
        }

        return false;
    }
}