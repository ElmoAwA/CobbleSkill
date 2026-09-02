package de.tomalbrc.skillcore.impl.mechanic.projectile;

import de.tomalbrc.bil.core.holder.positioned.PositionedHolder;
import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.mixin.accessor.AbstractArrowAccessor;
import de.tomalbrc.skillcore.mixin.accessor.ItemEntityAccessor;
import de.tomalbrc.skillcore.mixin.accessor.ThrowableItemProjectileAccessor;
import de.tomalbrc.skillcore.registry.Models;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.elements.GenericEntityElement;
import eu.pb4.polymer.virtualentity.api.elements.VirtualElement;
import eu.pb4.polymer.virtualentity.mixin.accessors.EntityAccessor;
import eu.pb4.polymer.virtualentity.mixin.accessors.ItemDisplayEntityAccessor;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import java.util.Locale;

public interface Bullet {
    void update(Vec3 pos, Vec3 posOld);
    void destroy();

    abstract class AbstractBullet implements Bullet {
        final ServerLevel level;
        Vec3 pos;

        protected AbstractBullet(ServerLevel level, Vec3 pos) {
            this.level = level;
            this.pos = pos;
        }
    }

    static Bullet from(AbstractProjectileMechanic mechanic, ServerLevel level, Vec3 pos) {
        var et = virtualEntityType(mechanic);
        if (et != null) {
            var e = new GenericEntityElement() {
                @Override
                protected EntityType<? extends Entity> getEntityType() {
                    return et;
                }
            };
            e.getDataTracker().set(ThrowableItemProjectileAccessor.getDATA_ITEM_STACK(), Items.SNOWBALL.getDefaultInstance());
            e.getDataTracker().set(EntityAccessor.getNO_GRAVITY(), true);

            e.getDataTracker().set(ItemEntityAccessor.getDATA_ITEM(), Items.MAGMA_BLOCK.getDefaultInstance());
            e.getDataTracker().set(ItemDisplayEntityAccessor.getITEM(), Items.MAGMA_BLOCK.getDefaultInstance());

            ElementHolder holder = new ElementHolder();
            holder.addElement(e);
            return new ModelBullet(level, pos, holder);
        }

        var ret = realEntityType(mechanic);
        if (ret != null) {
            var entity = ret.create(level);
            entity.setInvulnerable(true);
            entity.noPhysics = true;
            if (entity instanceof AbstractArrow arrow) {
                ((AbstractArrowAccessor)arrow).setPickup(AbstractArrow.Pickup.DISALLOWED);
                ((AbstractArrowAccessor)arrow).invokeSetPierceLevel((byte)125);
                ((AbstractArrowAccessor)arrow).setLife(Integer.MAX_VALUE);
            }
            return new Bullet.EntityBullet(level, pos, entity);
        }

        if (mechanic.bulletModel != null && mechanic.bulletModel.isBlank()) {
            var model = Models.getModel(mechanic.bulletModel);
            if (model != null) {
                var holder = new PositionedHolder(level, pos, model);
                return new ModelBullet(level, pos, holder);
            }
        }

        return null;
    }

    static EntityType<? extends Entity> virtualEntityType(AbstractProjectileMechanic mechanic) {
        return switch (mechanic.bulletType.toLowerCase(Locale.ROOT)) {
            case "arrow" -> switch (mechanic.arrowtype.toLowerCase(Locale.ROOT)) {
                case "spectral" -> EntityType.SPECTRAL_ARROW;
                case "trident" -> EntityType.TRIDENT;
                default -> EntityType.ARROW;
            };
            case "block" -> EntityType.FALLING_BLOCK;
            case "item" -> EntityType.SNOWBALL;
            case "tracking" -> EntityType.ITEM_DISPLAY;
            case "display" -> EntityType.BLOCK_DISPLAY;
            default -> null;
        };
    }

    static EntityType<? extends Entity> realEntityType(AbstractProjectileMechanic mechanic) {
        return switch (mechanic.bulletType.toLowerCase(Locale.ROOT)) {
            case "arrow" -> switch (mechanic.arrowtype.toLowerCase(Locale.ROOT)) {
                case "spectral" -> EntityType.SPECTRAL_ARROW;
                case "trident" -> EntityType.TRIDENT;
                default -> EntityType.ARROW;
            };
            default -> null;
        };
    }

    class EntityBullet extends AbstractBullet {
        Entity entity;

        public EntityBullet(ServerLevel level, Vec3 pos, Entity entity) {
            super(level, pos);
        }

        @Override
        public void update(Vec3 pos, Vec3 posOld) {
            this.entity.moveTo(pos);
        }

        @Override
        public void destroy() {
            SkillCore.SERVER.execute(() -> entity.discard());
        }
    }

    class ModelBullet extends AbstractBullet {
        WorldAttachment attachment;
        ElementHolder holder;

        protected ModelBullet(ServerLevel level, Vec3 pos, ElementHolder holder) {
            super(level, pos);

            this.holder = holder;
            this.attachment = new WorldAttachment(level, holder, pos);
        }

        public Vec3 pos() {
            return this.pos;
        }

        @Override
        public void update(Vec3 pos, Vec3 posOld) {
            this.pos = pos;
            for (VirtualElement element : this.holder.getElements()) {
                element.setOverridePos(pos);
                if (element instanceof GenericEntityElement element1) {
                    var motion = new ClientboundSetEntityMotionPacket(element1.getEntityId(), pos.subtract(posOld));
                    holder.sendPacket(motion);
                }
            }
        }

        @Override
        public void destroy() {
            holder.destroy();
        }
    }
}
