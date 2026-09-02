package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.minecraft.resources.ResourceLocation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogMechanic extends AbstractMechanic {
    @SerializedName(value = "message", alternate = {"m", "msg"})
    protected Resolvable<String> message;

    @Override
    public ExecutionResult execute(SkillTree tree) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String currentTime = LocalDateTime.now().format(formatter);

        SkillCore.LOGGER.info("Log-Mechanic [{}]: {}", currentTime, message.resolve(tree));
        //Thread.dumpStack();
        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.MESSAGE;
    }
}