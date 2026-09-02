package de.tomalbrc.skillcore.impl.mechanic.variable;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.impl.mechanic.AbstractMechanic;
import de.tomalbrc.skillcore.impl.variable.Variable;

public abstract class AbstractVariableMechanic extends AbstractMechanic {
    @SerializedName(value = "variable", alternate = {"name", "n", "var", "key", "k"})
    protected String key;
    @SerializedName(value = "scope", alternate = "s")
    protected Variable.Scope scope;
    @SerializedName(value = "duration", alternate = {"d", "e", "expire"})
    protected Integer duration; // in ticks

    protected boolean save;
}
