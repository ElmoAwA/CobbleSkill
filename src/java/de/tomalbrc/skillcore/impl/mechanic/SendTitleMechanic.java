package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import de.tomalbrc.skillcore.util.TextUtil;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public class SendTitleMechanic extends AbstractMechanic {
    @SerializedName(value = "title", alternate = {"t"})
    protected Resolvable<String> title;

    @SerializedName(value = "subtitle", alternate = {"st"})
    protected Resolvable<String> subtitle;

    @SerializedName(value = "duration", alternate = {"d"})
    protected Resolvable<Integer> duration = Resolvable.literal(1);

    @SerializedName(value = "fadein", alternate = {"fi"})
    protected Resolvable<Integer> fadeIn = Resolvable.literal(1);

    @SerializedName(value = "fadeout", alternate = {"fo"})
    protected Resolvable<Integer> fadeOut = Resolvable.literal(1);


    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        if (targets != null) {
            for (Target target : targets) {
                if (target.getEntity() instanceof ServerPlayer player) {
                    var list = new ArrayList<Packet<? super ClientGamePacketListener>>();
                    list.add(new ClientboundSetTitlesAnimationPacket(fadeIn.resolve(tree), duration.resolve(tree), fadeOut.resolve(tree)));
                    if (title != null) list.add(new ClientboundSetTitleTextPacket(TextUtil.formatText(this.title.resolve(tree, target), tree.caster())));
                    if (subtitle != null) list.add(new ClientboundSetSubtitleTextPacket(TextUtil.formatText(this.subtitle.resolve(tree, target), tree.caster())));
                    player.connection.send(new ClientboundBundlePacket(list));
                }
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.SEND_TITLE;
    }
}