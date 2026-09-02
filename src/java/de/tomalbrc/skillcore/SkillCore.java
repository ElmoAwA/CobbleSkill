package de.tomalbrc.skillcore;

import com.mojang.logging.LogUtils;
import de.tomalbrc.skillcore.api.GlobalStates;
import de.tomalbrc.skillcore.api.condition.Conditions;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Targeters;
import de.tomalbrc.skillcore.command.SkillCoreCommand;
import de.tomalbrc.skillcore.command.sub.ReloadCommand;
import de.tomalbrc.skillcore.core.EntityManager;
import de.tomalbrc.skillcore.core.SkillEngine;
import de.tomalbrc.skillcore.impl.mechanic.projectile.WorldAttachment;
import de.tomalbrc.skillcore.registry.Models;
import de.tomalbrc.skillcore.spawn.CustomSpawner;
import de.tomalbrc.skillcore.util.BukkitIdConverter;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;

public class SkillCore implements ModInitializer {
    public static final String MODID = "skillcore";
    public static MinecraftServer SERVER;
    public static Logger LOGGER = LogUtils.getLogger();

    public static FabricServerAudiences ADVENTURE;

    public static FabricServerAudiences adventure() {
        if (ADVENTURE == null) {
            throw new IllegalStateException("Tried to access Adventure without a running server!");
        }
        return ADVENTURE;
    }

    // /give @s minecraft:leather[skillcore:skills={skills:["skill{s=Slime_Ball} @self ~onTimer:3"]}]

    @Override
    public void onInitialize() {
        BukkitIdConverter.load();

        SkillCoreCommand.register();
        SkillCoreComponents.register();
        Models.load();
        WorldAttachment.registerEventHandler();
        CustomSpawner.registerEventHandler();
        EntityManager.registerEventHandler();

        ServerTickEvents.START_SERVER_TICK.register(world -> SkillEngine.getInstance().tick(world));

        ServerLifecycleEvents.SERVER_STARTING.register(x -> {
            ADVENTURE = FabricServerAudiences.of(x);
            SERVER = x;

            ReloadCommand.reload(null);
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            ADVENTURE.close();
            GlobalStates.shutdown();
        });

        if (false) {
            Mechanics.TYPE_ADAPTER_FACTORY.printLabels();
            Conditions.TYPE_ADAPTER_FACTORY.printLabels();
            Targeters.TYPE_ADAPTER_FACTORY.printLabels();
        }

//
//        ServerPlayNetworking.registerGlobalReceiver(new CustomPacketPayload.Type<>(ResourceLocation.parse("")), ((payload, context) -> {
//
//        }));
    }
}
