package de.tomalbrc.skillcore.impl.mechanic.model.holder;

import com.mojang.math.Transformation;
import de.tomalbrc.bil.core.holder.wrapper.Bone;
import de.tomalbrc.bil.core.holder.wrapper.DisplayWrapper;
import de.tomalbrc.bil.core.holder.wrapper.Locator;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.bil.core.model.Node;
import de.tomalbrc.bil.core.model.Pose;
import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.impl.mechanic.model.StateMachineHandler;
import de.tomalbrc.skillcore.mixin.accessor.Haha;
import eu.pb4.polymer.virtualentity.api.VirtualEntityUtils;
import eu.pb4.polymer.virtualentity.api.elements.MobAnchorElement;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class LivingEntityHolder<T extends LivingEntity> extends EntityHolder<T> {
    protected float deathAngle;

    protected float entityScale = 1F;
    public boolean lockHead = true;
    public boolean lockBody = false;
    StateMachineHandler stateMachineHandler = null;
    final Map<String, Attachment> attachments = new HashMap<>();

    Map<Bone, List<Bone>> family = null;

    public @Nullable Bone getBone(Node name) {
        for (int i = 0; i < this.getBones().length; i++) {
            var bone = this.getBones()[i];
            if (bone.node() == name)
                return bone;
        }
        return null;
    }

    public Map<Bone, List<Bone>> parentToChildrenMap(Bone[] allNodes) {
        Map<Bone, List<Bone>> parentToChildren = new HashMap<>();

        for (Bone child : allNodes) {
            Node parent = child.node().parent();
            if (parent != null) {
                parentToChildren.computeIfAbsent(getBone(parent), k -> new ArrayList<>()).add(child);
            }
        }

        return parentToChildren;
    }

    public void setForceHidden(String partid, boolean visibility, boolean exactmatch, boolean children) {
        if (family == null)
            family = parentToChildrenMap(bones);

        for (Bone bone : this.bones) {
            if (exactmatch ? bone.name().equals(partid) : bone.name().contains(partid)) {
                setForceHidden(bone, visibility, children);
            }
        }
    }

    public void setForceHidden(Bone bone, boolean visibility, boolean children) {
        if (family == null)
            family = parentToChildrenMap(bones);

        if (visibility) {
            forceHiddenBones.remove(bone);
            bone.setInvisible(false);
            if (children && family.containsKey(bone)) family.get(bone).forEach((x) -> {
                forceHiddenBones.remove(x);
                x.setInvisible(false);
            });
        } else {
            forceHiddenBones.add(bone);
            bone.setInvisible(true);
            if (children && family.containsKey(bone)) family.get(bone).forEach((x) -> {
                forceHiddenBones.add(x);
                x.setInvisible(true);
            });
        }
    }

    protected record Attachment(
            Bone bone,
            MobAnchorElement seat,
            Entity entity
    ) { }

    public StateMachineHandler stateMachineHandler() {
        return stateMachineHandler;
    }

    public LivingEntityHolder(T parent, Model model) {
        super(parent, model);

        ((Haha)this).setElementsInitialized(true);
        this.initializeElements();
    }

    public void setupStateMachine() {
        this.stateMachineHandler = new StateMachineHandler(parent, this);
    }

    @Override
    protected void onAsyncTick() {
        if (this.parent.deathTime > 0) {
            this.deathAngle = Math.min((float) Math.sqrt((this.parent.deathTime) / 20.0F * 1.6F), 1.f);
        }

        if (stateMachineHandler != null) stateMachineHandler.tick();
        updateHurtColor();

        super.onAsyncTick();

        synchronized (attachments) {
            for (Attachment attachment : this.attachments.values()) {
                updateSeat(attachment);
            }
            attachments.entrySet().removeIf(x -> x.getValue().entity.isRemoved() || (x.getValue().entity.asLivingEntity() != null && x.getValue().entity.asLivingEntity().isDeadOrDying()));
        }
    }

    public Entity getPassenger(String boneName) {
        Attachment at = attachments.get(boneName);
        return at == null ? null : attachments.get(boneName).entity;
    }

    public boolean isPassenger(Entity entity) {
        for (Attachment value : attachments.values()) {
            if (value.entity == entity) {
                return true;
            }
        }
        return false;
    }

    public void removePassenger(Entity entity) {
        attachments.entrySet().removeIf(x -> {
            if (x.getValue().entity == entity) {
                SkillCore.SERVER.execute(() -> {
                    this.removeElement(x.getValue().seat);
                });
                return true;
            }

            return false;
        });
    }

    public void removePassenger(String boneName) {
        Attachment at = attachments.remove(boneName);
        if (at != null) {
            SkillCore.SERVER.execute(() -> {
                this.removeElement(at.seat);
            });
        }
    }

    public void addSeatAttachment(String boneName, Entity entity) {
        if (attachments.containsKey(boneName))
            return;

        Bone bone = getBone(boneName, true);
        if (bone != null) {
            MobAnchorElement seat = new MobAnchorElement() {
                @Override
                protected EntityType<? extends Entity> getEntityType() {
                    return EntityType.ARMOR_STAND;
                }
            };
            seat.setInvisible(true);
            var at = new Attachment(bone, seat, entity);
            this.attachments.put(boneName, at);

            SkillCore.SERVER.execute(() -> {
                this.addElement(seat);
                updateSeat(at);
                this.sendPacket(VirtualEntityUtils.createRidePacket(seat.getEntityId(), IntArrayList.of(entity.getId())));
            });

            entity.setVirtualSeat(bone);
        }
    }

    public Bone getBone(String name, boolean exact) {
        for (Bone bone : bones) {
            if (exact ? bone.name().equals(name) : bone.name().contains(name)) {
                return bone;
            }
        }
        return null;
    }

    public void updateHurtColor() {
        if (parent.hurtTime > 0 || parent.deathTime > 0)
            this.setColor(0xff7e7e);
        else
            this.clearColor();
    }

    @Override
    public void updateElement(DisplayWrapper<?> display, @Nullable Pose pose) {
        if (!this.lockBody) display.element().setYaw(this.parent.yBodyRot);
        if (pose == null) {
            this.applyPose(display.getLastPose(), display);
        } else {
            this.applyPose(pose, display);
        }
    }

    protected void updateSeat(Attachment at) {
        if (at != null) {
            var position = worldPos(at.bone, Vec3.ZERO);
            SkillCore.SERVER.execute(() -> at.entity.moveTo(position));
            at.seat.setOverridePos(position);
        }
    }

    public Vec3 worldPos(Bone bone, Vec3 offset) {
        Transformation transformation = new Transformation(
                bone.element().getTranslation().get(new Vector3f()),
                bone.element().getLeftRotation().get(new Quaternionf()),
                bone.element().getScale().get(new Vector3f()),
                bone.element().getRightRotation().get(new Quaternionf())
        );
        transformation.getMatrix().translateLocal(offset.toVector3f());
        var p = transformation.getMatrix().getTranslation(new Vector3f());
        p.rotateY(Mth.wrapDegrees(-bone.element().getYaw()) * Mth.DEG_TO_RAD);
        return this.currentPos.add(p.x, p.y + 1.0, p.z); // TODO: offset might be incorrect
    }

    @Override
    protected void updateLocator(Locator locator) {
        if (locator.requiresUpdate()) {
            Pose pose = this.animationComponent.findPose(locator);
            if (pose == null) {
                locator.updateListeners(this, locator.getLastPose());
            } else {
                locator.updateListeners(this, pose);
            }
        }
    }

    @Override
    protected void applyPose(Pose pose, DisplayWrapper<?> display) {
        Vector3f translation = pose.translation();
        boolean isHead = display.isHead();
        boolean isDead = this.parent.deathTime > 0;

        if (isHead || isDead) {
            Quaternionf bodyRotation = new Quaternionf();
            if (!lockBody && isDead) {
                bodyRotation.rotateZ(-this.deathAngle * Mth.HALF_PI);
                translation.rotate(bodyRotation);
            }

            if (!lockHead && isHead) {
                bodyRotation.rotateY(Mth.DEG_TO_RAD * -Mth.rotLerp(0.5f, this.parent.yHeadRotO - this.parent.yBodyRotO, this.parent.yHeadRot - this.parent.yBodyRot));
                bodyRotation.rotateX(Mth.DEG_TO_RAD * Mth.lerp(0.5f, this.parent.xRotO, this.parent.getXRot()));
            }

            if (!lockBody) display.element().setLeftRotation(bodyRotation.mul(pose.readOnlyLeftRotation()));
        } else {
            display.element().setLeftRotation(pose.readOnlyLeftRotation());
        }

        if (this.entityScale != 1F) {
            translation.mul(this.entityScale);
            display.element().setScale(pose.scale().mul(this.entityScale));
        } else {
            display.element().setScale(pose.readOnlyScale());
        }

        display.element().setTranslation(translation.sub(0, this.dimensions.height() - 0.01f, 0));
        display.element().setRightRotation(pose.readOnlyRightRotation());

        display.element().startInterpolationIfDirty();
    }

    @Override
    protected void startWatchingExtraPackets(ServerGamePacketListenerImpl player, Consumer<Packet<ClientGamePacketListener>> consumer) {
        super.startWatchingExtraPackets(player, consumer);
        consumer.accept(VirtualEntityUtils.createRidePacket(this.parent.getId(), getDisplayIds()));

        for (Attachment attachment : this.attachments.values()) {
            player.send(VirtualEntityUtils.createRidePacket(attachment.seat.getEntityId(), IntArrayList.of(attachment.entity.getId())));
        }
    }

    @Override
    protected void updateCullingBox() {
        float scale = this.getScale();
        float width = scale * (this.dimensions.width() * 2);
        float height = -this.dimensions.height() - 1;

        for (Bone bone : this.bones) {
            bone.element().setDisplaySize(width, height);
        }
    }

    @Override
    public void onDimensionsUpdated(EntityDimensions dimensions) {
        this.updateEntityScale(this.scale);
        super.onDimensionsUpdated(dimensions);
    }

    @Override
    public float getScale() {
        return this.entityScale;
    }

    @Override
    public void setScale(float scale) {
        this.updateEntityScale(scale);
        super.setScale(scale);
    }

    protected void updateEntityScale(float scalar) {
        this.entityScale = this.parent.getScale() * scalar;
    }

    @Override
    protected void onDataLoaded() {
        super.onDataLoaded();
        VirtualEntityUtils.addVirtualPassenger(parent, getEntityIds().toIntArray());
    }

    @Override
    public void destroy() {
        VirtualEntityUtils.removeVirtualPassenger(parent, getEntityIds().toIntArray());
        super.destroy();
    }
}
