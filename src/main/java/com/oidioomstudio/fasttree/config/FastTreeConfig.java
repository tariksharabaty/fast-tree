package com.oidioomstudio.fasttree.config;

import com.oidioomstudio.olib.config.OConfigHolder;

public class FastTreeConfig {
    public enum SneakMode {
        ALWAYS_ACTIVE,
        SNEAK_ONLY,
        SNEAK_DISABLE
    }

    public static class Data {
        public boolean enabled = true;
        public int maxLogs = 128;
        public boolean breakLeaves = true;
        public boolean autoReplant = false;
        public boolean leafProtection = true;
        public boolean breakNetherWarts = true;
        public boolean breakMangroveRoots = true;
        public boolean teleportDrops = false;

        public boolean requireAxe = true;
        public boolean preventToolBreak = true;
        public float durabilityMultiplier = 1.0f;

        public SneakMode sneakMode = SneakMode.ALWAYS_ACTIVE;
        public boolean breakInCreative = false;
        public float hungerMultiplier = 1.0f;
    }

    private static final OConfigHolder<Data> HOLDER = new OConfigHolder<>(
        "fasttree",
        Data.class,
        Data::new
    );

    // 1. Ağaçlar (Trees)
    public static boolean enabled = true;
    public static int maxLogs = 128;
    public static boolean breakLeaves = true;
    public static boolean autoReplant = false;
    public static boolean leafProtection = true;
    public static boolean breakNetherWarts = true;
    public static boolean breakMangroveRoots = true;
    public static boolean teleportDrops = false;

    // 2. Aletler (Tools)
    public static boolean requireAxe = true;
    public static boolean preventToolBreak = true;
    public static float durabilityMultiplier = 1.0f;

    // 3. Oyuncu & Oynanış (Player & Gameplay)
    public static SneakMode sneakMode = SneakMode.ALWAYS_ACTIVE;
    public static boolean breakInCreative = false;
    public static float hungerMultiplier = 1.0f;

    public static void load() {
        Data data = HOLDER.load();
        enabled = data.enabled;
        maxLogs = data.maxLogs;
        breakLeaves = data.breakLeaves;
        autoReplant = data.autoReplant;
        leafProtection = data.leafProtection;
        breakNetherWarts = data.breakNetherWarts;
        breakMangroveRoots = data.breakMangroveRoots;
        teleportDrops = data.teleportDrops;

        requireAxe = data.requireAxe;
        preventToolBreak = data.preventToolBreak;
        durabilityMultiplier = data.durabilityMultiplier;

        sneakMode = data.sneakMode != null ? data.sneakMode : SneakMode.ALWAYS_ACTIVE;
        breakInCreative = data.breakInCreative;
        hungerMultiplier = data.hungerMultiplier;
    }

    public static void save() {
        Data data = HOLDER.get();
        data.enabled = enabled;
        data.maxLogs = maxLogs;
        data.breakLeaves = breakLeaves;
        data.autoReplant = autoReplant;
        data.leafProtection = leafProtection;
        data.breakNetherWarts = breakNetherWarts;
        data.breakMangroveRoots = breakMangroveRoots;
        data.teleportDrops = teleportDrops;

        data.requireAxe = requireAxe;
        data.preventToolBreak = preventToolBreak;
        data.durabilityMultiplier = durabilityMultiplier;

        data.sneakMode = sneakMode;
        data.breakInCreative = breakInCreative;
        data.hungerMultiplier = hungerMultiplier;

        HOLDER.save();
    }

    public static void resetDefaults() {
        Data def = new Data();
        enabled = def.enabled;
        maxLogs = def.maxLogs;
        breakLeaves = def.breakLeaves;
        autoReplant = def.autoReplant;
        leafProtection = def.leafProtection;
        breakNetherWarts = def.breakNetherWarts;
        breakMangroveRoots = def.breakMangroveRoots;
        teleportDrops = def.teleportDrops;

        requireAxe = def.requireAxe;
        preventToolBreak = def.preventToolBreak;
        durabilityMultiplier = def.durabilityMultiplier;

        sneakMode = def.sneakMode;
        breakInCreative = def.breakInCreative;
        hungerMultiplier = def.hungerMultiplier;

        save();
    }
}
