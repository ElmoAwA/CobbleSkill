package de.tomalbrc.skillcore.util;

import de.tomalbrc.skillcore.SkillCore;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

public class TextUtil {
    public static Component formatText(String text) {
        return formatText(text, null);
    }

    public static Component formatText(String text, Entity caster) {
        if (text == null || text.isBlank())
            return Component.empty();

        text = MiniMessage.miniMessage().serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(text));
        text = text.replace("\\<", "<");

        if (caster != null) {
            text = text.replace("<mob.name>", MiniMessage.miniMessage().serialize(caster.getName().asComponent()));
            text = text.replace("<caster.name>", MiniMessage.miniMessage().serialize(caster.getName().asComponent()));

            var living = caster.asLivingEntity();
            if (living != null) {
                var hp = String.valueOf((int)Math.ceil(living.getHealth()));
                var mhp = String.valueOf((int)Math.ceil(living.getMaxHealth()));

                text = text.replace("<mob.hp>", hp);
                text = text.replace("<mob.health>", hp);
                text = text.replace("<mob.mhp>", mhp);
                text = text.replace("<mob.maxhealth>", mhp);

                text = text.replace("<caster.hp>", hp);
                text = text.replace("<caster.health>", hp);
                text = text.replace("<caster.mhp>", mhp);
                text = text.replace("<caster.maxhealth>", mhp);
                text = text.replace("<caster.level>", String.valueOf(living.getCustomLevel()));
            }
        }

        var parsed = MiniMessage.miniMessage().deserialize(text);
        return SkillCore.adventure().toNative(parsed);
    }
//
//    public static net.minecraft.network.chat.Component formatText(String text, Entity caster) {
//        if (text == null || text.isBlank())
//            return net.minecraft.network.chat.Component.empty();
//
//        text = MiniMessage.miniMessage().serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(text));
//        text = text.replace("\\<", "<");
//
//        TagResolver.Builder builder = TagResolver.builder();
//        if (caster != null) {
//            builder.tag("caster.name", (args, ctx) -> Tag.selfClosingInserting(caster.getName()));
//            builder.tag("mob.name", (args, ctx) -> Tag.selfClosingInserting(caster.getName()));
//            builder.tag("caster.var.", (args, ctx) -> {
//                String varName = args.popOr("expected var name").value();
//                String val = caster.getVariables().get(varName).asString();
//                return Tag.selfClosingInserting(val == null ? Component.empty() : Component.text(val));
//            });
//
//            var living = caster.asLivingEntity();
//            if (living != null) {
//                builder.tag("caster.hp", (args, ctx) -> Tag.selfClosingInserting(Component.text(living.getHealth())));
//                builder.tag("caster.health", (args, ctx) -> Tag.selfClosingInserting(Component.text(living.getHealth())));
//                builder.tag("caster.mhp", (args, ctx) -> Tag.selfClosingInserting(Component.text(living.getMaxHealth())));
//                builder.tag("caster.maxhealth", (args, ctx) -> Tag.selfClosingInserting(Component.text(living.getMaxHealth())));
//                builder.tag("caster.level", (args, ctx) -> Tag.selfClosingInserting(Component.text(caster.getCustomLevel())));
//            }
//        }
//
//        var customResolver = builder.build();
//
//        MiniMessage mm = MiniMessage.builder()
//                .tags(TagResolver.builder()
//                        .resolver(StandardTags.defaults())
//                        .resolver(customResolver)
//                        .build())
//                .build();
//
//        var parsed = mm.deserialize(text);
//        return SkillCore.adventure().toNative(parsed);
//    }
}
