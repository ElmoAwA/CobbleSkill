package de.tomalbrc.skillcore.api;

import de.tomalbrc.skillcore.impl.variable.Variable;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GlobalStates {
    private static final ExecutorService EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

    private static final Map<String, Variable> GLOBAL_VARIABLES = new ConcurrentHashMap<>();
    private static final Map<ResourceKey<Level>, Map<String, Variable>> LEVEL_VARIABLES = new ConcurrentHashMap<>();

    private GlobalStates() {}

    public static ExecutorService executorService() {
        return EXECUTOR;
    }

    public static void shutdown() {
        EXECUTOR.shutdownNow();
    }

    public static void execute(Runnable command) {
        EXECUTOR.execute(command);
    }

    public static Map<String, Variable> getWorldVariables(ResourceKey<Level> world) {
        return LEVEL_VARIABLES.computeIfAbsent(world, (x) -> new Object2ObjectOpenHashMap<>());
    }

    public static Map<String, Variable> getGlobalVariables() {
        return GLOBAL_VARIABLES;
    }
}
