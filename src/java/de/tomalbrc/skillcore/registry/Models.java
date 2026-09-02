package de.tomalbrc.skillcore.registry;

import com.google.common.collect.ImmutableList;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.bil.file.loader.AjBlueprintLoader;
import de.tomalbrc.bil.file.loader.AjModelLoader;
import de.tomalbrc.bil.file.loader.BbModelLoader;
import de.tomalbrc.skillcore.SkillCore;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Models {
    private static final Map<String, Model> MODELS = new Object2ObjectArrayMap<>();
    private static final List<String> SUPPORTED_EXTENSIONS = ImmutableList.of("bbmodel", "ajmodel", "ajblueprint");

    public static Model getModel(String name) {
        return MODELS.get(name);
    }
    public static void load() {
        loadFrom(FabricLoader.getInstance().getConfigDir().resolve("skillcore/models"));
        loadFrom(FabricLoader.getInstance().getConfigDir().resolve("MythicMobs/models"));
    }
    public static void loadFrom(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            return;
        }

        try (Stream<Path> files = Files.list(path)) {
            files.filter(p -> {
                String name = p.getFileName().toString().toLowerCase();
                return SUPPORTED_EXTENSIONS.stream().anyMatch(name::endsWith);
            }).forEach(Models::processModelFile);
        } catch (IOException e) {
            SkillCore.LOGGER.error("Error reading model files: {}", e.getMessage());
        }
    }

    private static void processModelFile(Path filePath) {
        Model model = null;
        String name = filePath.toString();
        if (FilenameUtils.isExtension(name, "bbmodel")) {
            model = BbModelLoader.load(name);
        } else if (FilenameUtils.isExtension(name, "ajmodel")) {
            model = AjModelLoader.load(name);
        } else if (FilenameUtils.isExtension(name, "ajblueprint")) {
            model = AjBlueprintLoader.load(name);
        }

        MODELS.put(FilenameUtils.getBaseName(name), model);
    }
}
