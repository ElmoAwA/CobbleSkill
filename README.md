# SkillCore
MythicMobs-like skill implementation for fabric

Supports custom Mobs with Spawns as well as MythicCrucibe items with skills.

Uses BIL as MEG alternative

Cobblemon additions:
<details>
<summary>Mechanics</summary>

| Mechanic            | Alias                                      |
|---------------------|--------------------------------------------|
| randombattleskill   | battleSkill, pokeSkill                     |
| cobblemon_animation | cobblemonAnimation, cAnimation, cobbleanim |
| bedrock_particle    | bedrockParticle, e:bp, bparticle           |

</details>

<details>
<summary>Conditions</summary>

| Condition  | Alias   |
|------------|---------|
| hasTrainer |         |
| isSpecies  | species |
| hasAspect  | aspect  |
| isPokemon  |         |
| hasAspect  | aspect  |
| willDefend | aspect  |

</details>

<details>
<summary>Targeters</summary>
None
</details>

Implemented mechanics, targeters and conditions:

<details>
<summary>Mechanics</summary>

| Mechanic               | Alias                                                                                       |
|------------------------|---------------------------------------------------------------------------------------------|
| delay                  |
| cancel_event           | cancelevent                                                                                 |
| cancel_skill           | cancel, cancelskill, return                                                                 |
| damage                 |
| basedamage             |
| percentdamage          | damagepercent                                                                               |
| consume                |
| state                  | animation                                                                                   |
| message                |
| log                    | console, prinft, print                                                                      |
| sendactionmessage      | actionmessage, am                                                                           |
| sendtitle              | title                                                                                       |
| potion                 |
| skill                  |
| sudoskill              |
| for_each               | foreach                                                                                     |
| randomskill            | randommeta                                                                                  |
| set_variable           | setvariable                                                                                 |
| variableadd            | addvar, addvariable, incrementvariable, varadd                                              |
| sound                  | e:s, e:sound, effect:sound, s                                                               |
| ender_effect           | e:ender, effect:ender, ender, endereffect                                                   |
| totem                  |
| orbital                | o                                                                                           |
| projectile             |
| particle               | e:p, e:particle, e:particles, effect:particle, effect:particles, particles                  |
| particle_ring          | e:pr, effect:particlering, particlering, pr                                                 |
| particle_line          | e:pl, effect:particleline, particleline, pl                                                 |
| particle_line_helix    | effect:particlelinehelix, particlehelixline, particlelinehelix                              |
| particle_sphere        | e:ps, effect:particlesphere, particlesphere, ps                                             |
| particle_box           | e:pb, effect:particlebox, particlebox, pb                                                   |
| particle_tornado       | e:pt, effect:particletornado, particletornado                                               |
| feed                   |
| freeze                 |
| heal                   |
| setai                  | ai                                                                                          |
| resetai                | resetaigoals                                                                                |
| oxygen                 |
| geyser                 | e:geyser, effect:geyser                                                                     |
| ignite                 |
| extinguish             | removefire                                                                                  |
| lightning              |
| fake_lightning         | fakelightning                                                                               |
| bonemeal               |
| clearexperience        | clearexp                                                                                    |
| clearexperiencelevels  | clearexplevels                                                                              |
| giveexperiencelevels   | giveexplevels                                                                               |
| suicide                |
| remove                 | delete                                                                                      |
| run_ai_goal_selector   | aigoal, aigoals, runaigoalselector                                                          |
| run_ai_target_selector | aitarget, runaitargetselector                                                               |
| swap                   | tpswap                                                                                      |
| settarget              | target                                                                                      |
| setstance              | stance                                                                                      |
| pull                   |
| forcepull              |
| teleport               |
| teleportin             | tpdir, tpi, tpin                                                                            |
| propel                 |
| leap                   |
| lunge                  |
| throw                  |
| stun                   |
| jump                   |
| fakeexplosion          | e:explosion, effect:explode, effect:explosion, fakeexplode                                  |
| explosion              | explode                                                                                     |
| shootfireball          | fireball                                                                                    |
| setblocktype           | setblock                                                                                    |
| modifymobscore         | mms                                                                                         |
| breakblock             | blockbreak                                                                                  |
| smokeswirl             | e:smokeswirl                                                                                |
| tagadd                 | addscoreboardtag, addtag                                                                    |
| tagremove              | removescoreboardtag, removetag                                                              |
| setgliding             |
| setgravity             | setusegravity                                                                               |
| aura                   | buff, debuff                                                                                |
| auraremove             | removeaura, removebuff, removedebuff                                                        |
| spin                   | e:spin, effect:spin                                                                         |
| itemspray              | e:itemspray, effect:itemspray                                                               |
| blockmask              | e:blockmask, effect:blockmask                                                               |
| blockunmask            | e:blockunmask, effect:blockunmask                                                           |
| mounttarget            |
| dismountall            | dismountallmodel                                                                            |
| summon                 | piratesummon, spawnmob, spawnmobs                                                           |
| decapitate             | drophead                                                                                    |
| cleartarget            | resettarget                                                                                 |
| clearthreat            | threatclear                                                                                 |
| threat                 | threatchange, threatmod                                                                     |
| taunt                  |
| shoot                  |
| equip                  |
| look                   |
| setspeed               |
| bloody_screen          | bloodyscreen, e:bloodyscreen, e:redscreen, effect:bloodyscreen, effect:redscreen, redscreen |
| set_level              | setLevel, setlevel                                                                          |
| signal                 | sendsignal                                                                                  |
| globalcooldown         | gcd, setgcd, setglobalcooldown                                                              |
| barset                 |
| baradd                 |
| barremove              |
| arrowvolley            |
| slash                  |
| goto                   |
| wolfsit                |
| model                  |
| lockmodel              | lockrotation                                                                                |
| mountmodel             |
| partvis                | partvisibility                                                                              |
| defaultstate           | defaultanimation                                                                            |
</details>

<details>
<summary>Conditions</summary>

| Condition                      | Alias                                           |
|--------------------------------|-------------------------------------------------|
| alwaystrue                     |
| any                            |
| all                            |
| altitude                       | heightfromsurface                               |
| biome                          |
| y_diff                         | ydiff                                           |
| block_type                     | blocktype, inblock, insideblock                 |
| is_filament_mob                | isfilamentmob                                   |
| mounted                        |
| is_in_survival_mode            | isinsurvivalmode                                |
| blocking                       | isblocking                                      |
| food_level                     | food, foodlevel, hunger, hungerlevel            |
| string_not_empty               | stringnotempty                                  |
| food_saturation                | foodsaturation, hungersaturation                |
| distance_from_tracked_location | distancefromtrackedlocation                     |
| is_invulnerable                | isinvincible, isinvulnerable                    |
| raining                        | israining                                       |
| players_online                 | onlineplayercount, onlineplayers, playersonline |
| bounding_boxes_overlap         | bbsoverlap, boundingboxesoverlap                |
| is_player                      | isplayer                                        |
| variable_contains              | variablecontains                                |
| string_empty                   | stringempty                                     |
| night                          |
| trigger_block_type             | triggerblocktype                                |
| damage_tag                     | damagetag                                       |
| is_leashed                     | isleashed                                       |
| target_in_line_of_sight        | targetinlineofsight                             |
| trigger_item_type              | triggeritemtype                                 |
| height_above                   | heightabove                                     |
| sunny                          |
| target_not_within              | targetnotwithin                                 |
| looking_at                     | lookingat                                       |
| healthpercent                  | hppercent                                       |
| motion_z                       | motionz                                         |
| has_passenger                  | haspassenger                                    |
| z_diff                         | zdiff                                           |
| motion_y                       | motiony                                         |
| height_below                   | heightbelow                                     |
| day                            |
| skill_on_cooldown              | skilloncooldown                                 |
| line_of_sight                  | lineofsight                                     |
| holding                        |
| entity_type                    | entitytype                                      |
| is_baby                        | isbaby                                          |
| is_caster                      | iscaster                                        |
| fall_speed                     | fallingspeed, fallspeed                         |
| world                          |
| health                         | hp                                              |
| biome_type                     | biomecategory, biometype                        |
| light_level                    | lightlevel                                      |
| on_ground                      | onground                                        |
| is_monster                     | ismonster                                       |
| dusk                           |
| x_diff                         | xdiff                                           |
| target_within                  | targetwithin                                    |
| is_living                      | isliving                                        |
| dawn                           |
| distance                       |
| sprinting                      |
| dimension                      |
| inside                         |
| in_combat                      | incombat                                        |
| damage_amount                  | damageamount                                    |
| directional_velocity           | directionalvelocity                             |
| moving                         | ismoving                                        |
| on_block                       | onblock                                         |
| target_not_in_line_of_sight    | targetnotinlineofsight                          |
| thundering                     |
| has_free_inventory_slot        | hasfreeinventoryslot                            |
| metaskill                      |
| gliding                        |
| distance_from_location         | distancefromlocation                            |
| burning                        |
| world_time                     | worldtime                                       |
| wearing                        |
| is_climbing                    | isclimbing                                      |
| name                           |
| height                         |
| chance                         |
| bow_tension                    | bowtension                                      |
| variable_is_set                | variableisset                                   |
| motion_x                       | motionx                                         |
| vehicle_is_dead                | vehicleisdead                                   |
| score                          |
| size                           | mobsize                                         |
| variable_in_range              | variableinrange                                 |
| enchanting_experience          | enchantingexperience                            |
| outside                        |
| enchanting_level               | enchantinglevel                                 |
| is_using_spyglass              | isusingspyglass                                 |
| field_of_view                  | fieldofview                                     |
| distance_from_spawn            | distancefromspawn                               |
| has_tag                        | hasScoreboardTag, hasscoreboardtag, hastag      |
| block_type_in_radius           | blocktypeinradius                               |
| yaw                            |
| has_item                       | hasitem                                         |
| velocity                       |
| has_offhand                    | hasoffhand                                      |
| crouching                      |
| variable_equals                | variableequals                                  |
| last_damage_cause              | lastdamagecause                                 |
| offgcd                         |
| stance                         |
| playerwithin                   | playerswithin                                   |
| playersinradius                | pir, playerinradius                             |
| hasaura                        | hasbuff, hasdebuff                              |
| modelhaspassengers             |
| samefaction                    | factionsame                                     |
| hastarget                      |
</details>

<details>
<summary>Targeters</summary>

| Targeter                     | Alias                                                                                                                                            |
|------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------|
| none                         |
| target                       | t                                                                                                                                                |
| trigger                      |
| vehicle                      |
| self                         | Boss, Mob                                                                                                                                        |
| caster                       |
| self_location                | SelfLocation, bossLocation, casterLocation, mobLocation                                                                                          |
| self_eye_location            | SelfEyeLocation, bossEyeLocation, casterEyeLocation, eyeDirection, mobEyeLocation                                                                |
| players_in_radius            | PIR, PlayersInRadius                                                                                                                             |
| players_in_ring              | PlayersInRing                                                                                                                                    |
| players_in_world             | PlayersInWorld, world                                                                                                                            |
| tracked_players              | TrackedPlayers, tracked                                                                                                                          |
| owner                        |
| mobs_in_radius               | MIR, MobsInRadius                                                                                                                                |
| origin                       |
| items_in_radius              | IIR, ItemsInRadius                                                                                                                               |
| entities_in_radius           | EIR, EntitiesInRadius, LivingEntitiesInRadius, a, allInRadius, livingInRadius                                                                    |
| entities_in_ring             | EIRR, EntitiesInRing                                                                                                                             |
| living_in_line               | EIL, LEIL, LivingInLine, eil, entitiesInLine, entitiesinline, leil, livingEntitiesInLine                                                         |
| entities_near_origin         | EntitiesNearOrigin                                                                                                                               |
| players_near_origin          | PlayersNearOrigin                                                                                                                                |
| nearest_player               | NearestPlayer                                                                                                                                    |
| forward                      |
| ring                         |                                                                                                                                                  |
| threattable                  | TT                                                                                                                                               |
| threattableplayers           |
| rectangle                    |
| targetlocation               | TL, targetLoc                                                                                                                                    |
| random_locations_near_target | RLNT, RLNTE, RLNTL, RandomLocationsNearTargets, randomLocationsNearTarget, randomLocationsNearTargetEntities, randomLocationsNearTargetLocations |
| random_locations_near_origin | RLNO, RLO, RandomLocationsNearOrigin, randomLocationsOrigin                                                                                      |
| random_locations_near_caster | RLC, RLNC, RandomLocationsNearCaster, randomLocationsCaster                                                                                      |
| obstructingblock             |
| wolfowner                    |
| modelpassengers              |
| modelpart                    |
</details>
