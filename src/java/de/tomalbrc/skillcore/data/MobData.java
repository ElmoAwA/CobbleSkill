package de.tomalbrc.skillcore.data;

import com.cobblemon.mod.common.CobblemonEntities;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.google.common.base.Predicates;
import com.google.gson.annotations.SerializedName;
import com.mojang.authlib.GameProfile;
import de.tomalbrc.skillcore.api.Skill;
import de.tomalbrc.skillcore.io.ParseUtil;
import de.tomalbrc.skillcore.mixin.accessor.AttributeMapAccessor;
import de.tomalbrc.skillcore.mixin.accessor.MobAccessor;
import de.tomalbrc.skillcore.registry.DropTableRegistry;
import de.tomalbrc.skillcore.registry.ItemRegistry;
import de.tomalbrc.skillcore.util.BukkitIdConverter;
import de.tomalbrc.skillcore.util.DisguiseSyntaxParser;
import de.tomalbrc.skillcore.util.RangedValue;
import de.tomalbrc.skillcore.util.TextUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Variant;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.disguiselib.api.EntityDisguise;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public record MobData(
        String identifier,
        @SerializedName(value = "Type", alternate = "MobType") String type,
        @SerializedName("Display") String display,
        @SerializedName("Health") Float health,
        @SerializedName("Damage") Float damage,
        @SerializedName("Armor") Float armor,
        @SerializedName("Equipment") List<EquipmentEntry> equipment,
        @SerializedName("Drops") List<String> drops,
        @SerializedName(value = "LevelModifiers", alternate = "LevelModifier") Map<String, Double> levelModifier,
        @SerializedName(value = "DamageModifiers", alternate = "DamageModifier") List<String> damageModifier,
        @SerializedName(value = "ElementalDamageModifiers", alternate = "ElementalDamageModifier") boolean elementalModifier,

        @SerializedName("Disguise") String disguise,

        @SerializedName("AIGoalSelectors") List<String> aiGoals,
        @SerializedName("AITargetSelectors") List<String> aiTargets,

        @SerializedName("ExtraOptions") Map<String, String> cobblemonOptions,
        @SerializedName("DisplayOptions") DisplayOptions displayOptions,
        @SerializedName("Options") MobOptions options,
        @SerializedName("Modules") Modules modules,
        @SerializedName("BossBar") BossBarOptions bossBar,
        @SerializedName("Skills") List<Skill> skills
) {
    @Nullable
    public Entity spawn(ServerLevel level, Vec3 pos) {
        return spawn(level, pos, true);
    }

    @Nullable
    public Entity spawn(ServerLevel level, Vec3 pos, boolean add) {
        var typeId = BukkitIdConverter.entityType(type).orElse(ResourceLocation.parse(type.toLowerCase(Locale.ROOT)));
        var entityType = BuiltInRegistries.ENTITY_TYPE.get(typeId);

        if (entityType == CobblemonEntities.POKEMON && cobblemonOptions != null) {
            var props = cobblemonOptions.get("pokemon");
            var pokemon = PokemonProperties.Companion.parse(props);
            var entity = pokemon.createEntity(level);

            entity.mobId(identifier);
            this.setup(entity);
            entity.moveTo(pos);
            if (add) level.addFreshEntity(entity);

            return entity;
        } else {
            var entity = entityType.create(level, null, BlockPos.containing(pos), MobSpawnType.TRIGGERED, false, false);
            if (entity != null) {
                entity.mobId(identifier);
                this.setup(entity);
                entity.moveTo(pos);
                if (add) level.addFreshEntity(entity);

                return entity;
            }
        }

        return null;
    }
    public void setup(Entity entity) {
        setup(entity, false);
    }

    public void setup(Entity entity, boolean loaded) {
        var living = entity.asLivingEntity();
        if (!loaded && entity instanceof AbstractPiglin abstractPiglin) {
            abstractPiglin.setImmuneToZombification(true);
        }
        if (!loaded && entity instanceof Warden warden) {
            warden.getBrain().setMemoryWithExpiry(MemoryModuleType.DIG_COOLDOWN, Unit.INSTANCE, 1200);
            if (options != null && options.despawn() != null && options.despawn()) {
                warden.getBrain().setMemoryWithExpiry(MemoryModuleType.DIG_COOLDOWN, Unit.INSTANCE, 72000 * 10);
            }
        }

        if (!loaded && living != null) {
            if (damage != null) {
                setAttributeForced(entity.asLivingEntity(), Attributes.ATTACK_DAMAGE, damage);
            }
            if (health != null) {
                setAttributeForced(living, Attributes.MAX_HEALTH, health);
                living.setHealth(living.getMaxHealth());
            }

            if (armor != null) setAttributeForced(entity.asLivingEntity(), Attributes.ARMOR, armor);;

            if (display != null) {
                entity.setCustomName(TextUtil.formatText(display));
            }

            if (options != null) {
                if (options.movementSpeed() != null) setAttributeForced(living, Attributes.MOVEMENT_SPEED, options.movementSpeed());
                if (options.followRange() != null) setAttributeForced(living, Attributes.FOLLOW_RANGE, options.followRange());
                if (options.scale() != null) setAttributeForced(living, Attributes.SCALE, options.scale());
                if (options.maxCombatDistance() != null) setAttributeForced(living, Attributes.ENTITY_INTERACTION_RANGE, options.maxCombatDistance());
                if (options.knockbackResistance() != null) setAttributeForced(living, Attributes.KNOCKBACK_RESISTANCE, options.knockbackResistance());
                if (options.attackSpeed() != null) setAttributeForced(living, Attributes.ATTACK_SPEED, options.attackSpeed());

                entity.setCustomNameVisible(options.alwaysShowName());

                if (entity instanceof Mob mob) {
                    mob.setNoAi(options.noAI());
                    if (options.preventItemPickup()) mob.setCanPickUpLoot(false);
                    mob.setAggressive(options.angry());

                    if (options.despawn() != null && !options.despawn()) mob.setPersistenceRequired();
                }

                if (entity instanceof AgeableMob ageableMob) {
                    ageableMob.setBaby(options.baby());
                }

                if (entity instanceof Zombie abstractPiglin) {
                    abstractPiglin.setBaby(options.baby());
                }

                if (entity instanceof AbstractPiglin abstractPiglin) {
                    abstractPiglin.setBaby(options.baby());
                    abstractPiglin.setImmuneToZombification(options.preventTransformation());
                }

                if (entity instanceof EquipmentUser mob) {
                    if (options.preventRandomEquipment()) {
                        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                            mob.setItemSlot(equipmentSlot, Items.AIR.getDefaultInstance());
                        }
                    }

                    if (options.preventMobKillDrops()) {
                        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                            mob.setDropChance(equipmentSlot, 0f);
                        }
                    }
                }
            }
        }

        if (!loaded && entity instanceof EquipmentUser mob && equipment != null) {
            for (EquipmentEntry entry : equipment) {
                mob.setItemSlot(entry.slot(), entry.item().asItemStack());
            }
        }

        if (!loaded && options != null) {
            if (entity instanceof Slime slime && options.size() != null) {
                slime.setSize(options.size(),true);
            }

            entity.setSilent(options.silent());
            entity.setGlowingTag(options.glowing());
            entity.setForceInvisible(options.invisible());

            entity.setInvulnerable(options.invincible());
            entity.setNoGravity(options.noGravity());

            if (options.scale() != null && entity.asLivingEntity() != null) {
                setAttributeForced(entity.asLivingEntity(), Attributes.SCALE, options().scale());
            }
        }

        setupGoals(entity);

        if (!loaded && disguise != null) {
            DisguiseSyntaxParser.DisguiseCommand disguiseCommand = DisguiseSyntaxParser.parse(disguise);
            ResourceLocation entityTypeId = BukkitIdConverter.entityType(disguiseCommand.type().toUpperCase(Locale.ROOT)).orElseGet(() -> ResourceLocation.parse(disguiseCommand.type().toLowerCase(Locale.ROOT)));
            var entityType = BuiltInRegistries.ENTITY_TYPE.getOptional(entityTypeId);
            entityType.ifPresent(x -> {
                ((EntityDisguise) entity).disguiseAs(x);

                if (disguiseCommand.options().containsKey("skin")) ((EntityDisguise) entity).setGameProfile(new GameProfile(UUID.randomUUID(), disguiseCommand.options().get("skin")));

                Entity de = ((EntityDisguise) entity).getDisguiseEntity();
                if (de instanceof Horse horse && disguiseCommand.options().containsKey("variant")) {
                    horse.setVariant(Variant.valueOf(disguiseCommand.options().get("variant").toUpperCase()));
                }
            });
        }
    }

    public void setupGoals(Entity entity) {
        if (entity instanceof PathfinderMob mob) {
            if (aiGoals != null) runFor(mob, ((MobAccessor)mob).getGoalSelector(), aiGoals);
            if (aiTargets != null) runFor(mob, ((MobAccessor)mob).getTargetSelector(), aiTargets);
        }
    }

    public static void setAttributeForced(LivingEntity living, Holder<Attribute> attr, double val) {
        if (!living.getAttributes().hasAttribute(attr)) {
            var ai = new AttributeInstance(attr, x -> {
                x.setBaseValue(val);
            });
            ai.setBaseValue(val);

            ((AttributeMapAccessor)living.getAttributes()).getAttributes().put(attr, ai);
            living.getAttributes().getAttributesToUpdate().add(ai);
            if ((attr.value()).isClientSyncable()) {
                living.getAttributes().getAttributesToSync().add(ai);
            }
        } else {
            var instance = living.getAttributes().getInstance(attr);
            if (instance != null) instance.setBaseValue(val);
        }
    }

    public static void runFor(PathfinderMob mob, GoalSelector goalSelector, List<String> ai) {
        for (String goal : ai) {
            var t = ParseUtil.tokenizeTopLevel(goal);
            int prio = 0;
            if (t.size() > 1) {
                prio = Integer.parseInt(t.getFirst());
            }

            var goalToken = t.getLast();
            var map = ParseUtil.buildTypeMap(goalToken);
            if (goalToken.equalsIgnoreCase("clear") || goalToken.equalsIgnoreCase("reset")) {
                goalSelector.removeAllGoals(Predicates.alwaysTrue());
                if (map.containsKey("brain") || map.containsKey("b"))
                    mob.getBrain().removeAllBehaviors();
            }

            var g = getGoal(mob, goalToken);
            if (g != null)
                goalSelector.addGoal(prio, g);
        }
    }

    public static Goal getGoal(PathfinderMob mob, String goalToken) {
        var map = ParseUtil.buildTypeMap(goalToken);
        String v = map.get("type").toString();
        return switch (v.toLowerCase(Locale.ROOT)) {
            case "movetowardstarget", "followtarget" -> new MoveTowardsTargetGoal(mob, 1, 24);
            case "meleeattack" -> new MeleeAttackGoal(mob, 1, true);
            case "bowattack" -> {
                if (mob instanceof Monster && mob instanceof RangedAttackMob) {
                    yield new RangedBowAttackGoal<>((Monster & RangedAttackMob)mob, 1, 20, 15);
                }
                yield null;
            }
            case "rangedattack" -> {
                if (mob instanceof RangedAttackMob rangedAttackMob) {
                    yield new RangedAttackGoal(rangedAttackMob, 1, 20, 15);
                }
                yield null;
            }
            case "crossbowattack" -> {
                if (mob instanceof CrossbowAttackMob && mob instanceof Monster) {
                    yield new RangedCrossbowAttackGoal<>((Monster & CrossbowAttackMob)mob, 1, 10);
                }
                yield null;
            }
            case "creeperswell" -> {
                if (mob instanceof Creeper creeper) {
                    yield new SwellGoal(creeper);
                }
                yield null;
            }
            case "breed" -> {
                if (mob instanceof Animal creeper) {
                    yield new BreedGoal(creeper, 1);
                }
                yield null;
            }
            case "fleeplayers", "runfromplayers" -> new AvoidEntityGoal<>(mob, Player.class, 16, 1, 1.2);
            case "fleegolems", "runfromgolems" -> new AvoidEntityGoal<>(mob, IronGolem.class, 16, 1, 1.2);
            case "fleevillagers", "runfromvillagers" -> new AvoidEntityGoal<>(mob, Player.class, 16, 1, 1.2);
            case "fleewolf", "runfromwolves" -> new AvoidEntityGoal<>(mob, Player.class, 16, 1, 1.2);
            case "fleesun" -> new FleeSunGoal(mob,1);
            case "restrictsun" -> new RestrictSunGoal(mob);
            case "leapattarget" -> new LeapAtTargetGoal(mob, 1.2f);
            case "movetolava" -> new MoveToBlockGoal(mob, 1, 1) {
                @Override
                protected boolean isValidTarget(LevelReader levelReader, BlockPos blockPos) {
                    return levelReader.getFluidState(blockPos).is(FluidTags.LAVA);
                }
            };
            case "movetowater" -> new MoveToBlockGoal(mob, 1, 1) {
                @Override
                protected boolean isValidTarget(LevelReader levelReader, BlockPos blockPos) {
                    return levelReader.getFluidState(blockPos).is(FluidTags.WATER);
                }
            };
            case "horrified", "panic" -> new PanicGoal(mob, 1);
            case "randomstroll" -> new RandomStrollGoal(mob, 1);
            case "randomlookaround", "lookaround" -> new RandomLookAroundGoal(mob);
            case "float", "swim" -> new FloatGoal(mob);
            case "opendoor", "opendoors" -> new OpenDoorGoal(mob, true);
            case "eatgrass" -> new EatBlockGoal(mob);
            case "lookatplayer", "lookatplayers" -> new LookAtPlayerGoal(mob, Player.class, 5);
            case "lookattarget" -> new LookAtPlayerGoal(mob, Player.class, 5); // TODO: custom goal impl.
            case "avoidcreepers", "avoidcreeper" -> new AvoidEntityGoal<>(mob, Creeper.class, 16f, 1.0, 1.2);

            // target
            case "players", "player" -> new NearestAttackableTargetGoal<>(mob, Player.class, true);
            case "villager", "villagers" -> new NearestAttackableTargetGoal<>(mob, Villager.class, true);
            case "golem", "golems", "iron_golems", "iron_golem" -> new NearestAttackableTargetGoal<>(mob, IronGolem.class, true);
            case "monster", "monsters" -> new NearestAttackableTargetGoal<>(mob, Monster.class, true);
            case "attacker", "hurtbytarget", "damager" -> new HurtByTargetGoal(mob);
            case "ownerattacker", "ownerhurtby", "ownerhurtbytarget", "ownerdamager" -> {
                if (mob instanceof TamableAnimal tamableAnimal) {
                    yield  new OwnerHurtByTargetGoal(tamableAnimal);
                }
                yield null;
            }
            case "ownertarget", "ownerattack", "ownerhurt" -> {
                if (mob instanceof TamableAnimal tamableAnimal) {
                    yield new OwnerHurtTargetGoal(tamableAnimal);
                }
                yield null;
            }

            // TODO: parent related goals
            default -> null;
        };

    }

    public void applyLevelModifier(LivingEntity living) {
        if (levelModifier != null) {
//            Map<String, Double> asMap = new HashMap<>();
//            for (String s : levelModifier) {
//                var split = s.split(" ");
//                asMap.put(split[0], Double.parseDouble(split.length > 1 ? split[1] : "1"));
//            }

            for (Map.Entry<String, Double> entry : levelModifier.entrySet()) {
                var key = entry.getKey().toLowerCase(Locale.ROOT);
                var value = entry.getValue();

                var health = living.getHealth();
                var maxhealth = living.getMaxHealth();
                var perc = maxhealth / health;

                Holder<Attribute> a = switch (key) {
                    case "health" -> Attributes.MAX_HEALTH;
                    case "damage" -> Attributes.ATTACK_DAMAGE;
                    case "knockbackresistance" -> Attributes.KNOCKBACK_RESISTANCE;
                    //case "Power" -> Attributes.; // TODO: power scaling / custom attribute
                    case "armor" -> Attributes.ARMOR;
                    case "movementspeed" -> Attributes.MOVEMENT_SPEED;
                    default -> null;
                };

                if (a != null) {
                    var attr = living.getAttributes().getInstance(a);
                    if (attr != null)
                        attr.addOrReplacePermanentModifier(new AttributeModifier(ResourceLocation.parse(key), value * living.getCustomLevel(), AttributeModifier.Operation.ADD_VALUE));
                }

                // adjust current health with new level
                living.setHealth(living.getMaxHealth()*perc);
            }
        }
    }

    public void lootDrop(Entity entity, Entity killer) {
        if (drops == null || drops.isEmpty())
            return;

        for (String drop : drops) {
            var tokens = ParseUtil.tokenizeTopLevel(drop);
            if (!tokens.isEmpty()) {
                var root = tokens.getFirst();
                var map = ParseUtil.buildTypeMap(root.toLowerCase(Locale.ROOT));
                var t = map.get("type").toString();

                switch (t) {
                    case "exp": {
                        var amount = RangedValue.parse(tokens.get(1)).getAsInteger();
                        var chance = Double.parseDouble(tokens.size() > 2 ? tokens.get(2) : "1");
                        if (Math.random() <= chance) {
                            ExperienceOrb.award((ServerLevel)entity.level(), entity.position(), amount);
                        }
                    }
                    case "money": {
                        // TODO: currency provider
                    }
                    case "mmoitem": {
                        // TODO: what do we do here
                        // not supported
                    }
                    default: {
                        // parse item
                        var customLoot = DropTableRegistry.get(t);
                        if (customLoot != null) {
                            // TODO
                        } else {
                            var customItem = ItemRegistry.get(t);
                            if (customItem != null) {
                                var stack = customItem.asItemStack();
                                stack.setCount(tokens.size() > 1 ? RangedValue.parse(tokens.get(1)).getAsInteger(): 1);
                                var chance = Double.parseDouble(tokens.size() > 2 ? tokens.get(2) : "1");
                                if (Math.random() <= chance) {
                                    entity.spawnAtLocation(stack);
                                }
                            } else {
                                var item = BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(t));
                                if (item.isPresent()) {
                                    var amount = tokens.size() > 1 ? RangedValue.parse(tokens.get(1)).getAsInteger(): 1;
                                    var stack = new ItemStack(item.get(), amount);

                                    // TODO: support more fields
                                    if (map.containsKey("display")) {
                                        stack.set(DataComponents.ITEM_NAME, TextUtil.formatText(map.get("display").toString(), entity));
                                    }

                                    var chance = Double.parseDouble(tokens.size() > 2 ? tokens.get(2) : "1");
                                    if (Math.random() <= chance) {
                                        entity.spawnAtLocation(stack);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
