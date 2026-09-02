package de.tomalbrc.skillcore.core;

import de.tomalbrc.skillcore.api.overlay.EntityOverlay;
import de.tomalbrc.skillcore.registry.MobRegistry;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.entity.EntityDamageEvent;
import xyz.nucleoid.stimuli.event.entity.EntityDeathEvent;
import xyz.nucleoid.stimuli.event.entity.EntitySpawnEvent;
import xyz.nucleoid.stimuli.event.entity.EntityUseEvent;
import xyz.nucleoid.stimuli.event.item.ItemPickupEvent;
import xyz.nucleoid.stimuli.event.item.ItemUseEvent;
import xyz.nucleoid.stimuli.event.player.PlayerAttackEntityEvent;
import xyz.nucleoid.stimuli.event.player.PlayerSwapWithOffhandEvent;
import xyz.nucleoid.stimuli.event.player.PlayerSwingHandEvent;

import java.util.concurrent.atomic.AtomicReference;

public class EntityManager {
    public static void registerEventHandler() {
        Stimuli.global().listen(EntityDamageEvent.EVENT, (entity, damageSource, a) -> {
            var e = entity.overlay();
            if (e != null)
                return e.getTriggerHandler().onDamage(damageSource, a);

            return InteractionResult.PASS;
        });

        Stimuli.global().listen(EntityUseEvent.EVENT, (serverPlayer, entity, hand, hitResult) -> {
            var e = entity.overlay();
            if (e != null)
                return e.getTriggerHandler().onInteract(serverPlayer, hand);

            return InteractionResult.PASS;
        });

        Stimuli.global().listen(EntityDeathEvent.EVENT, (entity, damageSource) -> {
            var e = entity.overlay();
            if (e != null)
                return e.getTriggerHandler().onDeath(damageSource);

            return InteractionResult.PASS;
        });

        Stimuli.global().listen(EntitySpawnEvent.EVENT, (entity) -> {
            AtomicReference<EntityOverlay<? extends Entity>> e = new AtomicReference<>(entity.overlay());

            if (e.get() == null && entity.mobId() != null) {
                MobRegistry.getOptional(entity.mobId()).ifPresent(x -> {
                    e.set(new EntityOverlay<>(entity, x));
                });
            }

            if (e.get() != null) // TODO: cancel spawn for CancelEvent mechanic?
                e.get().getTriggerHandler().onSpawn();

            return InteractionResult.PASS;
        });

        ServerEntityEvents.ENTITY_UNLOAD.register((entity, serverLevel) -> {
            var overlay = entity.overlay();
            if (overlay != null) {
                overlay.onDespawn();
                SkillEngine.getInstance().onDespawn(entity, serverLevel);
            }
        });

        registerPlayerEvents();
    }

    public static void registerPlayerEvents() {
        Stimuli.global().listen(PlayerSwingHandEvent.EVENT, (serverPlayer, interactionHand) -> {
            var o = serverPlayer.playerOverlay();
            if (o != null) {
                o.getTriggerHandler().onSwing();
            }
        });

        Stimuli.global().listen(PlayerAttackEntityEvent.EVENT, (serverPlayer, interactionHand, entity, l) -> {
            var o = serverPlayer.playerOverlay();
            if (o != null) {
                var res = o.getTriggerHandler().onAttack(serverPlayer.serverLevel(), entity);
                if (res.consumesAction())
                    return res;
            }
            return InteractionResult.PASS;
        });

        Stimuli.global().listen(PlayerSwapWithOffhandEvent.EVENT, (serverPlayer) -> {
            var o = serverPlayer.playerOverlay();
            if (o != null) {
                var res = o.getTriggerHandler().onSwap();
                if (res.consumesAction())
                    return res;
            }
            return InteractionResult.PASS;
        });

        Stimuli.global().listen(EntityUseEvent.EVENT, (serverPlayer, entity, hand, entityHitResult) -> {
            var o = serverPlayer.playerOverlay();
            if (o != null) {
                var res = o.getTriggerHandler().onInteractAsPlayer(serverPlayer, entity);
                if (res.consumesAction())
                    return res;
            }

            return InteractionResult.PASS;
        });

        Stimuli.global().listen(ItemUseEvent.EVENT, (player, interactionHand) -> {
            var o = player.playerOverlay();
            if (o != null) {
                InteractionResult res = o.getTriggerHandler().onUse(player, interactionHand);
                if (res.consumesAction())
                    return InteractionResultHolder.consume(player.getItemInHand(interactionHand));
            }

            return InteractionResultHolder.pass(player.getItemInHand(interactionHand));
        });

        // TODO: check behaviour compared to MM
        Stimuli.global().listen(ItemPickupEvent.EVENT, (player, item, stack) -> {
            var o = player.playerOverlay();
            if (o != null) {
                InteractionResult res = o.getTriggerHandler().onItemPickup(item, stack);
                if (res.consumesAction())
                    return res;
            }

            return InteractionResult.PASS;
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            if (newPlayer.overlay() != null) {
                newPlayer.playerOverlay().getTriggerHandler().onRespawn();
            }
        });

        ServerPlayerEvents.JOIN.register(player -> {
            if (player.overlay() != null) {
                player.playerOverlay().getTriggerHandler().onJoin();
            }
        });
    }
}
