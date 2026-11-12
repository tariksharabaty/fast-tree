package com.oidioomstudio.fasttree.client;

import com.oidioomstudio.fasttree.FastTreeMod;
import com.oidioomstudio.fasttree.config.FastTreeConfig;
import com.oidioomstudio.olib.api.OConfigCategory;
import com.oidioomstudio.olib.api.OConfigRegistry;
import com.oidioomstudio.olib.api.OModConfig;
import com.oidioomstudio.olib.api.options.BooleanOption;
import com.oidioomstudio.olib.api.options.EnumOption;
import com.oidioomstudio.olib.api.options.FloatSliderOption;
import com.oidioomstudio.olib.api.options.IntegerSliderOption;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public class FastTreeClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FastTreeMod.LOGGER.info("[Fast Tree] Registering O Lib configuration interface with category hierarchy...");
        registerConfig();
    }

    public static void registerConfig() {
        // 1. Ağaçlar (Trees) Category
        OConfigCategory treesCategory = OConfigCategory.builder("trees", Component.translatable("fasttree.category.trees"))
            // Standalone master toggle at top
            .add(BooleanOption.builder("enabled", Component.translatable("fasttree.config.enabled"))
                .description(Component.translatable("fasttree.config.enabled.desc"))
                .defaultValue(true)
                .getter(() -> FastTreeConfig.enabled)
                .setter(val -> FastTreeConfig.enabled = val)
                .build())
            // Group: Ağaç Kesme Parametreleri
            .group("felling", Component.translatable("fasttree.group.felling"), group -> {
                group.add(IntegerSliderOption.builder("max_logs", Component.translatable("fasttree.config.max_logs"))
                    .description(Component.translatable("fasttree.config.max_logs.desc"))
                    .range(16, 1024, 16)
                    .defaultValue(128)
                    .formatter(val -> Component.translatable("fasttree.config.logs_count", String.valueOf(val)))
                    .getter(() -> FastTreeConfig.maxLogs)
                    .setter(val -> FastTreeConfig.maxLogs = val)
                    .build());
                group.add(BooleanOption.builder("break_leaves", Component.translatable("fasttree.config.break_leaves"))
                    .description(Component.translatable("fasttree.config.break_leaves.desc"))
                    .defaultValue(true)
                    .getter(() -> FastTreeConfig.breakLeaves)
                    .setter(val -> FastTreeConfig.breakLeaves = val)
                    .build());
                group.add(BooleanOption.builder("auto_replant", Component.translatable("fasttree.config.auto_replant"))
                    .description(Component.translatable("fasttree.config.auto_replant.desc"))
                    .defaultValue(false)
                    .getter(() -> FastTreeConfig.autoReplant)
                    .setter(val -> FastTreeConfig.autoReplant = val)
                    .build());
            })
            // Group: Koruma ve Özel Bloklar
            .group("protection", Component.translatable("fasttree.group.protection"), group -> {
                group.add(BooleanOption.builder("leaf_protection", Component.translatable("fasttree.config.leaf_protection"))
                    .description(Component.translatable("fasttree.config.leaf_protection.desc"))
                    .defaultValue(true)
                    .getter(() -> FastTreeConfig.leafProtection)
                    .setter(val -> FastTreeConfig.leafProtection = val)
                    .build());
                group.add(BooleanOption.builder("break_mangrove_roots", Component.translatable("fasttree.config.break_mangrove_roots"))
                    .description(Component.translatable("fasttree.config.break_mangrove_roots.desc"))
                    .defaultValue(true)
                    .getter(() -> FastTreeConfig.breakMangroveRoots)
                    .setter(val -> FastTreeConfig.breakMangroveRoots = val)
                    .build());
                group.add(BooleanOption.builder("break_nether_warts", Component.translatable("fasttree.config.break_nether_warts"))
                    .description(Component.translatable("fasttree.config.break_nether_warts.desc"))
                    .defaultValue(true)
                    .getter(() -> FastTreeConfig.breakNetherWarts)
                    .setter(val -> FastTreeConfig.breakNetherWarts = val)
                    .build());
                group.add(BooleanOption.builder("teleport_drops", Component.translatable("fasttree.config.teleport_drops"))
                    .description(Component.translatable("fasttree.config.teleport_drops.desc"))
                    .defaultValue(false)
                    .labels(Component.translatable("fasttree.config.drops.inventory"), Component.translatable("fasttree.config.drops.ground"))
                    .getter(() -> FastTreeConfig.teleportDrops)
                    .setter(val -> FastTreeConfig.teleportDrops = val)
                    .build());
            })
            .build();

        // 2. Aletler (Tools) Category
        OConfigCategory toolsCategory = OConfigCategory.builder("tools", Component.translatable("fasttree.category.tools"))
            // Group: Balta Gereksinimleri
            .group("axe", Component.translatable("fasttree.group.axe"), group -> {
                group.add(BooleanOption.builder("require_axe", Component.translatable("fasttree.config.require_axe"))
                    .description(Component.translatable("fasttree.config.require_axe.desc"))
                    .defaultValue(true)
                    .labels(Component.translatable("fasttree.config.axe.only"), Component.translatable("fasttree.config.axe.any"))
                    .getter(() -> FastTreeConfig.requireAxe)
                    .setter(val -> FastTreeConfig.requireAxe = val)
                    .build());
                group.add(BooleanOption.builder("prevent_tool_break", Component.translatable("fasttree.config.prevent_tool_break"))
                    .description(Component.translatable("fasttree.config.prevent_tool_break.desc"))
                    .defaultValue(true)
                    .labels(Component.translatable("fasttree.config.tool_protect.on"), Component.translatable("fasttree.config.tool_protect.off"))
                    .getter(() -> FastTreeConfig.preventToolBreak)
                    .setter(val -> FastTreeConfig.preventToolBreak = val)
                    .build());
            })
            // Group: Dayanıklılık Tüketimi
            .group("durability", Component.translatable("fasttree.group.durability"), group -> {
                group.add(FloatSliderOption.builder("durability_multiplier", Component.translatable("fasttree.config.durability_multiplier"))
                    .description(Component.translatable("fasttree.config.durability_multiplier.desc"))
                    .range(0.2f, 5.0f, 0.2f)
                    .defaultValue(1.0f)
                    .formatter(val -> Component.literal(String.format(Locale.ROOT, "%.1fx", val)))
                    .getter(() -> FastTreeConfig.durabilityMultiplier)
                    .setter(val -> FastTreeConfig.durabilityMultiplier = val)
                    .build());
            })
            .build();

        // 3. Oyuncu & Oynanış (Player & Gameplay) Category
        OConfigCategory playerCategory = OConfigCategory.builder("player", Component.translatable("fasttree.category.player"))
            // Group: Kontroller ve Tetikleme
            .group("controls", Component.translatable("fasttree.group.controls"), group -> {
                group.add(EnumOption.builder("sneak_mode", Component.translatable("fasttree.config.sneak_mode"), FastTreeConfig.SneakMode.class)
                    .description(Component.translatable("fasttree.config.sneak_mode.desc"))
                    .nameMapper(mode -> Component.translatable("fasttree.config.sneak_mode." + mode.name().toLowerCase(Locale.ROOT)))
                    .defaultValue(FastTreeConfig.SneakMode.ALWAYS_ACTIVE)
                    .getter(() -> FastTreeConfig.sneakMode)
                    .setter(val -> FastTreeConfig.sneakMode = val)
                    .build());
            })
            // Group: Oynanış ve Mekanikler
            .group("gameplay", Component.translatable("fasttree.group.gameplay"), group -> {
                group.add(BooleanOption.builder("break_in_creative", Component.translatable("fasttree.config.break_in_creative"))
                    .description(Component.translatable("fasttree.config.break_in_creative.desc"))
                    .defaultValue(false)
                    .getter(() -> FastTreeConfig.breakInCreative)
                    .setter(val -> FastTreeConfig.breakInCreative = val)
                    .build());
                group.add(FloatSliderOption.builder("hunger_multiplier", Component.translatable("fasttree.config.hunger_multiplier"))
                    .description(Component.translatable("fasttree.config.hunger_multiplier.desc"))
                    .range(0.0f, 5.0f, 0.5f)
                    .defaultValue(1.0f)
                    .formatter(val -> val <= 0.0f ? Component.literal("0.0x (Kapalı)") : Component.literal(String.format(Locale.ROOT, "%.1fx", val)))
                    .getter(() -> FastTreeConfig.hungerMultiplier)
                    .setter(val -> FastTreeConfig.hungerMultiplier = val)
                    .build());
            })
            .build();

        // Register Fast Tree in OConfigRegistry
        OConfigRegistry.register(
            OModConfig.builder(FastTreeMod.MOD_ID, Component.translatable("modmenu.nameTranslation.fasttree"), FastTreeMod.MOD_VERSION)
                .category(treesCategory)
                .category(toolsCategory)
                .category(playerCategory)
                .onSave(FastTreeConfig::save)
                .onResetAll(FastTreeConfig::resetDefaults)
                .build()
        );
    }
}
