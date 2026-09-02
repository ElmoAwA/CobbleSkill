package de.tomalbrc.skillcore.impl.mechanic.model;

import de.tomalbrc.bil.api.AnimatedHolder;
import de.tomalbrc.bil.api.Animator;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class StateMachineHandler {
    private final LivingEntity entity;
    private final AnimatedHolder holder;
    private final Map<ModelState, DefaultProperty> defaultProperties = Collections.synchronizedMap(new EnumMap<>(ModelState.class));
    private final TreeMap<Integer, AnimationStateMachine> stateMachines = new TreeMap<>();
    private final Queue<Runnable> actionQueue = new ConcurrentLinkedQueue<>();

    private final Map<Integer, String> playingByPriority = new HashMap<>();
    private final Map<String, Set<Integer>> prioritiesByName = new HashMap<>();

    public StateMachineHandler(LivingEntity entity, AnimatedHolder holder) {
        this.entity = Objects.requireNonNull(entity);
        this.holder = Objects.requireNonNull(holder);

        setDefaultProperty(new DefaultProperty(ModelState.IDLE, "idle"));
        setDefaultProperty(new DefaultProperty(ModelState.WALK, "walk"));
        setDefaultProperty(new DefaultProperty(ModelState.STRAFE, "strafe"));
        setDefaultProperty(new DefaultProperty(ModelState.JUMP_START, "jump_start"));
        setDefaultProperty(new DefaultProperty(ModelState.JUMP, "jump"));
        setDefaultProperty(new DefaultProperty(ModelState.JUMP_END, "jump_end"));
        setDefaultProperty(new DefaultProperty(ModelState.HOVER, "hover"));
        setDefaultProperty(new DefaultProperty(ModelState.FLY, "fly"));
        setDefaultProperty(new DefaultProperty(ModelState.SWIM, "swim"));
        setDefaultProperty(new DefaultProperty(ModelState.SPAWN, "spawn"));
        setDefaultProperty(new DefaultProperty(ModelState.DEATH, "death"));

        configureAnimation();
    }

    public record DefaultProperty(ModelState state, String animationName) {}

    public enum ModelState {
        IDLE, WALK, STRAFE, JUMP_START, JUMP, JUMP_END, HOVER, FLY, SPAWN, DEATH, SWIM
    }

    public void setDefaultProperty(DefaultProperty p) {
        this.defaultProperties.put(p.state(), p);
    }

    public DefaultProperty getDefaultProperty(ModelState s) {
        return this.defaultProperties.get(s);
    }

    private boolean hasAnimation(ModelState s) {
        String anim = Optional.ofNullable(getDefaultProperty(s)).map(DefaultProperty::animationName).orElse(null);
        return anim != null && holder.getModel().animations().containsKey(anim);
    }

    private String createStateAnimation(ModelState state) {
        DefaultProperty def = getDefaultProperty(state);
        return def == null ? null : def.animationName();
    }

    private static int toAnimatorPriority(int handlerPriority) {
        return Math.max(Animator.DEFAULT_PRIORITY, handlerPriority);
    }

    private void configureAnimation() {
        AnimationStateMachine sm = new AnimationStateMachine(false, 0);
        StateNode<LivingEntity> root = sm.getRootNode();

        StateNode<LivingEntity> spawn = sm.createAnimationNode(() -> createStateAnimation(ModelState.SPAWN));
        StateNode<LivingEntity> idle = sm.createAnimationNode(() -> createStateAnimation(ModelState.IDLE));
        StateNode<LivingEntity> walk = sm.createAnimationNode(() -> createStateAnimation(ModelState.WALK));
        StateNode<LivingEntity> strafe = sm.createAnimationNode(() -> createStateAnimation(ModelState.STRAFE));
        StateNode<LivingEntity> jumpStart = sm.createAnimationNode(() -> createStateAnimation(ModelState.JUMP_START));
        StateNode<LivingEntity> jumpLoop = sm.createAnimationNode(() -> createStateAnimation(ModelState.JUMP));
        StateNode<LivingEntity> jumpEnd = sm.createAnimationNode(() -> createStateAnimation(ModelState.JUMP_END));
        StateNode<LivingEntity> hover = sm.createAnimationNode(() -> createStateAnimation(ModelState.HOVER));
        StateNode<LivingEntity> fly = sm.createAnimationNode(() -> createStateAnimation(ModelState.FLY));
        StateNode<LivingEntity> swim = sm.createAnimationNode(() -> createStateAnimation(ModelState.SWIM));
        StateNode<LivingEntity> death = sm.createAnimationNode(() -> {
            forceStopAllAnimations();
            return createStateAnimation(ModelState.DEATH);
        });

        Predicate<LivingEntity> isInWater = LivingEntity::isUnderWater;
        Predicate<LivingEntity> isAlive = LivingEntity::isAlive;
        Predicate<LivingEntity> isFlying = LivingEntity::isFallFlying;
        Predicate<LivingEntity> isJumping = e -> !e.onGround() && e.getDeltaMovement().y > 0.01;
        Predicate<LivingEntity> isStrafing = e -> false;
        Predicate<LivingEntity> isWalking = e -> {
            double vx = e.getDeltaMovement().x;
            double vz = e.getDeltaMovement().z;
            return Math.hypot(vx, vz) > 0.01;
        };

        root.addConnectedNode(e -> !isAlive.test(e), death);
        root.addConnectedNode(e -> hasAnimation(ModelState.SPAWN), spawn);
        root.addConnectedNode(e -> isFlying.test(e) && isWalking.test(e) && hasAnimation(ModelState.FLY), fly);
        root.addConnectedNode(e -> isFlying.test(e) && hasAnimation(ModelState.HOVER), hover);
        root.addConnectedNode(e -> isJumping.test(e) && hasAnimation(ModelState.JUMP_START), jumpStart);
        root.addConnectedNode(e -> isJumping.test(e) && hasAnimation(ModelState.JUMP), jumpLoop);
        root.addConnectedNode(e -> isStrafing.test(e) && hasAnimation(ModelState.STRAFE), strafe);
        root.addConnectedNode(e -> isInWater.test(e), swim);
        root.addConnectedNode(e -> isWalking.test(e), walk);
        root.addConnectedNode(e -> !isWalking.test(e), idle);

        spawn.addConnectedNode(e -> !isAlive.test(e), death);
        spawn.setCommonPredicate(e -> sm.hasFinishedPlaying(ModelState.SPAWN));
        spawn.addConnectedNode(e -> isFlying.test(e) && isWalking.test(e) && hasAnimation(ModelState.FLY), fly);
        spawn.addConnectedNode(e -> isFlying.test(e) && hasAnimation(ModelState.HOVER), hover);
        spawn.addConnectedNode(e -> isJumping.test(e) && hasAnimation(ModelState.JUMP_START), jumpStart);
        spawn.addConnectedNode(e -> isJumping.test(e) && hasAnimation(ModelState.JUMP), jumpLoop);
        spawn.addConnectedNode(e -> isStrafing.test(e) && hasAnimation(ModelState.STRAFE), strafe);
        spawn.addConnectedNode(e -> isInWater.test(e), swim);
        spawn.addConnectedNode(e -> isWalking.test(e), walk);
        spawn.addConnectedNode(e -> !isWalking.test(e), idle);

        idle.addConnectedNode(e -> !isAlive.test(e), death);
        idle.addConnectedNode(e -> isFlying.test(e) && isWalking.test(e) && hasAnimation(ModelState.FLY), fly);
        idle.addConnectedNode(e -> isFlying.test(e) && hasAnimation(ModelState.HOVER), hover);
        idle.addConnectedNode(e -> isJumping.test(e) && hasAnimation(ModelState.JUMP_START), jumpStart);
        idle.addConnectedNode(e -> isJumping.test(e) && hasAnimation(ModelState.JUMP), jumpLoop);
        idle.addConnectedNode(e -> isStrafing.test(e) && hasAnimation(ModelState.STRAFE), strafe);
        idle.addConnectedNode(e -> isInWater.test(e), swim);
        idle.addConnectedNode(e -> isWalking.test(e), walk);

        swim.addConnectedNode(e -> !isAlive.test(e), death);
        swim.addConnectedNode(e -> isFlying.test(e) && isWalking.test(e) && hasAnimation(ModelState.FLY), fly);
        swim.addConnectedNode(e -> isFlying.test(e) && hasAnimation(ModelState.HOVER), hover);
        swim.addConnectedNode(e -> isJumping.test(e) && hasAnimation(ModelState.JUMP_START), jumpStart);
        swim.addConnectedNode(e -> isJumping.test(e) && hasAnimation(ModelState.JUMP), jumpLoop);
        swim.addConnectedNode(e -> isStrafing.test(e) && hasAnimation(ModelState.STRAFE), strafe);
        swim.addConnectedNode(e -> !isWalking.test(e), idle);

        walk.addConnectedNode(e -> isInWater.test(e), swim);
        walk.addConnectedNode(e -> !isAlive.test(e), death);
        walk.addConnectedNode(e -> isFlying.test(e) && isWalking.test(e) && hasAnimation(ModelState.FLY), fly);
        walk.addConnectedNode(e -> isFlying.test(e) && hasAnimation(ModelState.HOVER), hover);
        walk.addConnectedNode(e -> isJumping.test(e) && hasAnimation(ModelState.JUMP_START), jumpStart);
        walk.addConnectedNode(e -> isJumping.test(e) && hasAnimation(ModelState.JUMP), jumpLoop);
        walk.addConnectedNode(e -> isStrafing.test(e) && hasAnimation(ModelState.STRAFE), strafe);
        walk.addConnectedNode(e -> !isWalking.test(e), idle);

        swim.addConnectedNode(e -> !isAlive.test(e), death);
        swim.addConnectedNode(e -> isFlying.test(e) && isWalking.test(e) && hasAnimation(ModelState.FLY), fly);
        swim.addConnectedNode(e -> isFlying.test(e) && hasAnimation(ModelState.HOVER), hover);
        swim.addConnectedNode(e -> isJumping.test(e) && hasAnimation(ModelState.JUMP_START), jumpStart);
        swim.addConnectedNode(e -> isJumping.test(e) && hasAnimation(ModelState.JUMP), jumpLoop);
        swim.addConnectedNode(e -> isStrafing.test(e) && hasAnimation(ModelState.STRAFE), strafe);
        swim.addConnectedNode(e -> !isWalking.test(e), idle);

        strafe.addConnectedNode(e -> !isAlive.test(e), death);
        strafe.addConnectedNode(e -> isFlying.test(e) && isWalking.test(e) && hasAnimation(ModelState.FLY), fly);
        strafe.addConnectedNode(e -> isFlying.test(e) && hasAnimation(ModelState.HOVER), hover);
        strafe.addConnectedNode(e -> isJumping.test(e) && hasAnimation(ModelState.JUMP_START), jumpStart);
        strafe.addConnectedNode(e -> isJumping.test(e) && hasAnimation(ModelState.JUMP), jumpLoop);
        strafe.addConnectedNode(e -> isInWater.test(e), swim);
        strafe.addConnectedNode(e -> isWalking.test(e), walk);
        strafe.addConnectedNode(e -> !isWalking.test(e), idle);

        jumpStart.addForceConnectedNode(e -> !isAlive.test(e), death);
        jumpStart.setCommonPredicate(e -> sm.hasFinishedPlaying(ModelState.JUMP_START));
        jumpStart.addConnectedNode(e -> isFlying.test(e) && isWalking.test(e) && hasAnimation(ModelState.FLY), fly);
        jumpStart.addConnectedNode(e -> isFlying.test(e) && hasAnimation(ModelState.HOVER), hover);
        jumpStart.addConnectedNode(e -> !isJumping.test(e) && hasAnimation(ModelState.JUMP_END), jumpEnd);
        jumpStart.addConnectedNode(e -> isInWater.test(e), swim);
        jumpStart.addConnectedNode(e -> !isJumping.test(e) && isWalking.test(e), walk);
        jumpStart.addConnectedNode(e -> !isJumping.test(e) && !isWalking.test(e), idle);

        jumpLoop.addConnectedNode(e -> !isAlive.test(e), death);
        jumpLoop.addConnectedNode(e -> isFlying.test(e) && isWalking.test(e) && hasAnimation(ModelState.FLY), fly);
        jumpLoop.addConnectedNode(e -> isFlying.test(e) && hasAnimation(ModelState.HOVER), hover);
        jumpLoop.addConnectedNode(e -> !isJumping.test(e) && hasAnimation(ModelState.JUMP_END), jumpEnd);
        jumpLoop.addConnectedNode(e -> isInWater.test(e), swim);
        jumpLoop.addConnectedNode(e -> !isJumping.test(e) && isWalking.test(e), walk);
        jumpLoop.addConnectedNode(e -> !isJumping.test(e) && !isWalking.test(e), idle);

        jumpEnd.addForceConnectedNode(e -> !isAlive.test(e), death);
        jumpEnd.setCommonPredicate(e -> sm.hasFinishedPlaying(ModelState.JUMP_END));
        jumpEnd.addConnectedNode(e -> isFlying.test(e) && isWalking.test(e) && hasAnimation(ModelState.FLY), fly);
        jumpEnd.addConnectedNode(e -> isFlying.test(e) && hasAnimation(ModelState.HOVER), hover);
        jumpEnd.addConnectedNode(e -> isJumping.test(e) && hasAnimation(ModelState.JUMP_START), jumpStart);
        jumpEnd.addConnectedNode(e -> isStrafing.test(e) && hasAnimation(ModelState.STRAFE), strafe);
        jumpEnd.addConnectedNode(e -> isInWater.test(e), swim);
        jumpEnd.addConnectedNode(e -> isWalking.test(e), walk);
        jumpEnd.addConnectedNode(e -> !isWalking.test(e), idle);

        hover.addConnectedNode(e -> !isAlive.test(e), death);
        hover.addConnectedNode(e -> isFlying.test(e) && isWalking.test(e) && hasAnimation(ModelState.FLY), fly);
        hover.addConnectedNode(e -> !isFlying.test(e) && isStrafing.test(e) && hasAnimation(ModelState.STRAFE), strafe);
        hover.addConnectedNode(e -> isInWater.test(e), swim);
        hover.addConnectedNode(e -> !isFlying.test(e) && isWalking.test(e), walk);
        hover.addConnectedNode(e -> !isFlying.test(e) && !isWalking.test(e), idle);

        fly.addConnectedNode(e -> !isInWater.test(e) && !isAlive.test(e), death);
        fly.addConnectedNode(e -> !isInWater.test(e) && isFlying.test(e) && !isWalking.test(e) && hasAnimation(ModelState.HOVER), hover);
        fly.addConnectedNode(e -> !isInWater.test(e) && !isFlying.test(e) && isStrafing.test(e) && hasAnimation(ModelState.STRAFE), strafe);
        fly.addConnectedNode(e -> !isInWater.test(e) && !isFlying.test(e) && isWalking.test(e), walk);
        fly.addConnectedNode(e -> !isInWater.test(e) && !isFlying.test(e) && !isWalking.test(e), idle);
        fly.addConnectedNode(e -> isInWater.test(e), swim);

        stateMachines.put(0, sm);
    }

    public void tick() {
        while (!actionQueue.isEmpty()) {
            Runnable r = actionQueue.poll();
            if (r != null) r.run();
        }

        synchronized (stateMachines) {
            for (AnimationStateMachine m : stateMachines.values()) {
                m.execute(entity);
            }
        }
    }

    public boolean playAnimation(int priority, String animationName, boolean force) {
        if (animationName == null) return false;
        synchronized (stateMachines) {
            AnimationStateMachine machine = stateMachines.computeIfAbsent(priority, p -> new AnimationStateMachine(true, p));
            if (!force && machine.isPlaying(animationName)) return false;

            StateNode<LivingEntity> current = machine.getCurrentNode();
            StateNode<LivingEntity> node = machine.createAnimationNode(() -> animationName);
            current.addForceConnectedNode(e -> true, node);
            return true;
        }
    }

    public boolean playAnimation(String animationName, boolean force) {
        return playAnimation(0, animationName, force);
    }

    public void stopAnimation(String animationName) {
        if (animationName == null) return;
        holder.getAnimator().stopAnimation(animationName);

        synchronized (stateMachines) {
            Set<Integer> prios = prioritiesByName.remove(animationName);
            if (prios != null) {
                for (Integer p : prios) {
                    playingByPriority.remove(p);
                    AnimationStateMachine m = stateMachines.get(p);
                    if (m != null) m.getCurrentNode().addForceConnectedNode(e -> true, m.getRootNode());
                }
            }
        }
    }

    public void stopAnimation(int priority, String animationName) {
        synchronized (stateMachines) {
            AnimationStateMachine m = stateMachines.get(priority);
            if (m != null && m.isPlaying(animationName)) {
                holder.getAnimator().stopAnimation(animationName);
                removePriorityMapping(priority, animationName);
                m.getCurrentNode().addForceConnectedNode(e -> true, m.getRootNode());
            }
        }
    }

    public void stopAtPriority(int priority) {
        String name;
        synchronized (stateMachines) {
            name = playingByPriority.remove(priority);
            if (name != null) {
                Set<Integer> set = prioritiesByName.get(name);
                if (set != null) {
                    set.remove(priority);
                    if (set.isEmpty()) prioritiesByName.remove(name);
                }
            }
        }
        if (name != null) {
            holder.getAnimator().stopAnimation(name);
        }

        synchronized (stateMachines) {
            AnimationStateMachine m = stateMachines.get(priority);
            if (m != null) m.getCurrentNode().addForceConnectedNode(e -> true, m.getRootNode());
        }
    }

    public void forceStopAllAnimations() {
        actionQueue.add(() -> {
            synchronized (stateMachines) {
                for (String name : new HashSet<>(prioritiesByName.keySet())) {
                    holder.getAnimator().stopAnimation(name);
                }
                playingByPriority.clear();
                prioritiesByName.clear();
                AnimationStateMachine defaultMachine = stateMachines.get(0);
                stateMachines.clear();
                if (defaultMachine != null) stateMachines.put(0, defaultMachine);
            }
        });
    }

    private void removePriorityMapping(int priority, String name) {
        playingByPriority.remove(priority);
        Set<Integer> set = prioritiesByName.get(name);
        if (set != null) {
            set.remove(priority);
            if (set.isEmpty()) prioritiesByName.remove(name);
        }
    }

    public static class StateNode<T> {
        private final StateMachine<T> machine;
        private final LinkedHashMap<StateNode<T>, Predicate<T>> connected = new LinkedHashMap<>();
        private final LinkedHashMap<StateNode<T>, Predicate<T>> forceConnected = new LinkedHashMap<>();
        private Consumer<T> entry = t -> {};
        private Consumer<T> action = t -> {};
        private Consumer<T> exit = t -> {};
        private Predicate<T> commonPredicate = null;

        public StateNode(StateMachine<T> machine) {
            this.machine = machine;
        }

        public void setEntryAction(Consumer<T> c) {
            entry = c == null ? t -> {
            } : c;
        }

        public void setAction(Consumer<T> c) {
            action = c == null ? t -> {
            } : c;
        }

        public void setExitAction(Consumer<T> c) {
            exit = c == null ? t -> {
            } : c;
        }

        public void setCommonPredicate(Predicate<T> p) {
            commonPredicate = p;
        }

        public void addConnectedNode(Predicate<T> pred, StateNode<T> node) {
            connected.put(node, pred);
        }

        public void addForceConnectedNode(Predicate<T> pred, StateNode<T> node) {
            forceConnected.put(node, pred);
        }

        public void clearConnectedNodes() {
            connected.clear();
        }

        public void clearForceConnectedNodes() {
            forceConnected.clear();
        }

        public void acceptEntry(T ctx) {
            entry.accept(ctx);
            machine.setCurrentNode(this);
        }

        public void acceptExit(T ctx) {
            exit.accept(ctx);
        }

        public void runAction(T ctx) {
            action.accept(ctx);
        }

        public StateNode<T> evaluate(T ctx) {
            for (Map.Entry<StateNode<T>, Predicate<T>> e : forceConnected.entrySet()) {
                Predicate<T> p = e.getValue();
                if (p == null || p.test(ctx)) return e.getKey();
            }
            if (commonPredicate != null && !commonPredicate.test(ctx)) return null;
            for (Map.Entry<StateNode<T>, Predicate<T>> e : connected.entrySet()) {
                Predicate<T> p = e.getValue();
                if (p == null || p.test(ctx)) return e.getKey();
            }
            return null;
        }
    }

    public static class StateMachine<T> {
        protected StateNode<T> entryNode;
        protected StateNode<T> currentNode;

        public void setEntryNode(StateNode<T> n) {
            this.entryNode = n;
        }

        public StateNode<T> getEntryNode() {
            return entryNode;
        }

        public StateNode<T> getCurrentNode() {
            return this.currentNode == null ? getEntryNode() : this.currentNode;
        }

        protected void setCurrentNode(StateNode<T> n) {
            this.currentNode = n;
        }

        public void execute(T ctx) {
            StateNode<T> node = getCurrentNode();
            if (node == null) return;
            node.runAction(ctx);
            StateNode<T> target = node.evaluate(ctx);
            if (target != null && target != node) {
                node.acceptExit(ctx);
                target.acceptEntry(ctx);
            }
        }
    }

    public class AnimationStateMachine extends StateMachine<LivingEntity> {
        protected final boolean saved;
        protected StateNode<LivingEntity> rootNode;
        protected @Nullable String lastAnimation;
        protected @Nullable String currentAnimation;
        protected final int priority;

        public AnimationStateMachine(boolean saved, int priority) {
            this.saved = saved;
            this.priority = priority;
        }

        public StateNode<LivingEntity> getCurrentNode() {
            return this.currentNode == null ? getRootNode() : this.currentNode;
        }

        public StateNode<LivingEntity> getRootNode() {
            if (rootNode == null) {
                rootNode = new StateNode<>(this);
                rootNode.setEntryAction(le -> this.currentAnimation = null);
                rootNode.setExitAction(le -> {
                    rootNode.clearForceConnectedNodes();
                    rootNode.clearConnectedNodes();
                    this.lastAnimation = this.currentAnimation;
                });
                setEntryNode(rootNode);
            }
            return rootNode;
        }

        public StateNode<LivingEntity> createAnimationNode(Supplier<String> propertySupplier) {
            StateNode<LivingEntity> node = new StateNode<>(this);

            node.setEntryAction(le -> {
                String animName = propertySupplier.get();
                this.currentAnimation = animName;
                if (animName == null) return;

                synchronized (stateMachines) {
                    playingByPriority.put(this.priority, animName);
                    prioritiesByName.computeIfAbsent(animName, k -> new HashSet<>()).add(this.priority);
                }

                final StateNode<LivingEntity> captured = node;
                final int animatorPriority = toAnimatorPriority(this.priority);

                holder.getAnimator().playAnimation(animName, animatorPriority, false, null, () -> {
                    actionQueue.add(() -> {
                        synchronized (stateMachines) {
                            Set<Integer> set = prioritiesByName.get(animName);
                            if (set != null) {
                                set.remove(this.priority);
                                if (set.isEmpty()) prioritiesByName.remove(animName);
                            }
                            String mapped = playingByPriority.get(this.priority);
                            if (animName.equals(mapped)) playingByPriority.remove(this.priority);
                            if (this.getCurrentNode() == captured) {
                                captured.addForceConnectedNode(e -> true, this.getRootNode());
                            }
                        }
                    });
                });
            });

            node.setAction(le -> {
            });

            node.setExitAction(le -> {
                this.lastAnimation = this.currentAnimation;
                if (this.currentAnimation != null) {
                    holder.getAnimator().stopAnimation(this.currentAnimation);
                }
            });

            return node;
        }

        public boolean hasFinishedPlaying(ModelState modelState) {
            DefaultProperty def = getDefaultProperty(modelState);
            if (def == null) return true;
            return !isPlaying(def.animationName());
        }

        public boolean isPlaying(String animation) {
            return this.currentAnimation != null && this.currentAnimation.equals(animation);
        }

        public boolean isPlayingOrEnding(String animation) {
            return isPlaying(animation);
        }

        public @Nullable String getLastPlaying() {
            return lastAnimation;
        }

        public @Nullable String getCurrentPlaying() {
            return currentAnimation;
        }
    }
}
