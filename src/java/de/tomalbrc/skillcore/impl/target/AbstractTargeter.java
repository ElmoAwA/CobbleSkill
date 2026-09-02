package de.tomalbrc.skillcore.impl.target;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.condition.Condition;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.api.target.Targeter;
import de.tomalbrc.skillcore.util.ThreatTable;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class AbstractTargeter implements Targeter {
    @SerializedName(value = "sort", alternate = {"sorttype"})
    protected Sorting sorting = Sorting.NONE;
    @SerializedName(value = "skiptargetsuptoindex", alternate = {"skip-targets-up-to-index", "stuti"})
    protected int skipTargetsUpToIndex = 0;
    protected int limit = 1;

    List<Condition> conditions; // inline target conditions

    @Override
    public List<Target> sort(SkillTree tree, @Nullable ThreatTable threatTable, Vec3 origin, List<Target> targets) {
        if (targets == null || targets.isEmpty())
            return List.of();

        if (conditions != null) {
            targets = new ArrayList<>(targets);
            targets.removeIf(x-> conditions != null && !conditions.isEmpty() && !conditions.stream().allMatch(c -> c.testWithTrigger(tree, x)));
        }

        if (sorting == Sorting.NONE)
            return targets;

        return switch (sorting) {
            case RANDOM -> {
                List<Target> shuffled = new ArrayList<>(targets);
                Collections.shuffle(shuffled);
                yield shuffled.subList(skipTargetsUpToIndex, limit);
            }

            case NEAREST -> targets.stream()
                    .sorted(Comparator.comparingDouble(t -> t.getPosition().distanceTo(origin)))
                    .toList().subList(skipTargetsUpToIndex, limit);

            case FURTHEST -> targets.stream()
                    .sorted(Comparator.comparingDouble((Target t) -> t.getPosition().distanceTo(origin)).reversed())
                    .toList().subList(skipTargetsUpToIndex, limit);

            case HIGHEST_HEALTH -> targets.stream()
                    .sorted(Comparator.comparingDouble((Target t) -> !t.isEntity() || t.getEntity().asLivingEntity() == null ? 0 : t.getEntity().asLivingEntity().getHealth()).reversed())
                    .toList().subList(skipTargetsUpToIndex, limit);

            case LOWEST_HEALTH -> targets.stream()
                    .sorted(Comparator.comparingDouble(t -> !t.isEntity() || t.getEntity().asLivingEntity() == null ? 0 : t.getEntity().asLivingEntity().getHealth()))
                    .toList().subList(skipTargetsUpToIndex, limit);

            case HIGHEST_THREAT -> {
                if (threatTable == null) yield targets;
                yield targets.stream()
                        .sorted(Comparator.comparingDouble((Target t) -> !t.isEntity() ? 0 : threatTable.getThreat(t.getEntity().asLivingEntity())).reversed())
                        .toList();
            }

            case LOWEST_THREAT -> {
                if (threatTable == null) yield targets;
                yield targets.stream()
                        .sorted(Comparator.comparingDouble(t -> !t.isEntity() ? 0 : threatTable.getThreat(t.getEntity().asLivingEntity())))
                        .toList().subList(skipTargetsUpToIndex, limit);
            }
            default -> throw new IllegalStateException("Unexpected value: " + sorting);
        };
    }

//    public List<Condition> parsed(String id) {
//        List<Condition> list = new ArrayList<>();
//        if (id.startsWith("[ ") && id.endsWith(" ]")) {
//            var skillList = id.substring(2, id.length() - 4).trim();
//            var split = skillList.split(" - ");
//            for (String s : split) {
//                var trimmed = s.trim();
//                if (!trimmed.isBlank()) {
//                    var skill = Json.GSON.fromJson(new JsonPrimitive(trimmed), Condition.class);
//                    if (skill != null) {
//                        list.add(skill);
//                    }
//                }
//            }
//        }
//
//        return list;
//    }
}
