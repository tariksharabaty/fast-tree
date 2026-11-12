package com.oidioomstudio.fasttree;

import com.oidioomstudio.fasttree.config.FastTreeConfig;
import com.oidioomstudio.olib.util.item.OInventoryHelper;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FastTreeMod implements ModInitializer {
    public static final String MOD_ID = "fasttree";
    public static final String MOD_NAME = "Fast Tree";
    public static final String MOD_VERSION = "1.1.2";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final ThreadLocal<Boolean> IS_FELLING = ThreadLocal.withInitial(() -> false);
    private static final Map<ResourceKey<Level>, Deque<BlockPos>> PENDING_LEAVES = new ConcurrentHashMap<>();
    private static final int LEAVES_PER_TICK = 6;

    @Override
    public void onInitialize() {
        LOGGER.info("[Fast Tree] Initializing Fast Tree mod by Oidioom & Tarek Sharabaty...");
        FastTreeConfig.load();

        PlayerBlockBreakEvents.BEFORE.register(this::onBlockBreak);
        ServerTickEvents.END_SERVER_TICK.register(FastTreeMod::onServerTick);

        LOGGER.info("[Fast Tree] Fast Tree initialized successfully.");
    }

    private boolean onBlockBreak(Level level, Player player, BlockPos originPos, BlockState originState, BlockEntity originEntity) {
        if (level.isClientSide() || !(player instanceof ServerPlayer serverPlayer)) {
            return true;
        }

        if (IS_FELLING.get()) {
            return true;
        }

        if (!FastTreeConfig.enabled) {
            return true;
        }

        if (player.isCreative() && !FastTreeConfig.breakInCreative) {
            return true;
        }

        boolean isSneaking = player.isShiftKeyDown();
        if (FastTreeConfig.sneakMode == FastTreeConfig.SneakMode.SNEAK_ONLY && !isSneaking) {
            return true;
        }
        if (FastTreeConfig.sneakMode == FastTreeConfig.SneakMode.SNEAK_DISABLE && isSneaking) {
            return true;
        }

        if (!isLogOrRoot(originState)) {
            return true;
        }

        ItemStack tool = player.getMainHandItem();
        if (FastTreeConfig.requireAxe && !isAxe(tool)) {
            return true;
        }

        // Search for connected logs and surrounding leaf canopy
        TreeScanResult scan = scanTree(level, originPos, FastTreeConfig.maxLogs);
        if (scan.logs.isEmpty()) {
            return true;
        }

        // Build protection: Ensure tree has leaves nearby (prevents felling wooden houses)
        if (FastTreeConfig.leafProtection && !scan.hasLeaves) {
            return true;
        }

        try {
            IS_FELLING.set(true);
            ServerLevel serverLevel = (ServerLevel) level;

            int logsMined = 0;
            BlockPos lowestLogPos = null;
            Block lowestLogBlock = null;

            for (BlockPos pos : scan.logs) {
                // Check axe breakage protection via O Lib helper
                if (FastTreeConfig.preventToolBreak && !OInventoryHelper.isSafeToDamage(tool)) {
                    break;
                }

                BlockState logState = level.getBlockState(pos);
                if (!logState.is(BlockTags.LOGS)) {
                    continue;
                }

                if (lowestLogPos == null || pos.getY() < lowestLogPos.getY()) {
                    lowestLogPos = pos;
                    lowestLogBlock = logState.getBlock();
                }

                // Award block mining stats to the player
                player.awardStat(Stats.BLOCK_MINED.get(logState.getBlock()));

                // Destroy block & drop items
                if (FastTreeConfig.teleportDrops) {
                    BlockEntity blockEntity = level.getBlockEntity(pos);
                    List<ItemStack> drops = Block.getDrops(logState, serverLevel, pos, blockEntity, player, tool);
                    level.destroyBlock(pos, false, player);
                    for (ItemStack drop : drops) {
                        OInventoryHelper.giveOrDrop(serverPlayer, drop);
                    }
                } else {
                    level.destroyBlock(pos, true, player);
                }

                // Damage axe per log according to multiplier
                if (tool.isDamageableItem()) {
                    int damage = Math.max(1, Math.round(FastTreeConfig.durabilityMultiplier));
                    tool.hurtAndBreak(damage, player, EquipmentSlot.MAINHAND);
                }

                logsMined++;
            }

            // Fast Leaf Decay: Leaves do NOT break immediately with the axe.
            // Instead, they are queued to decay rapidly with sound and particles.
            if (FastTreeConfig.breakLeaves && !scan.leaves.isEmpty()) {
                queueFastLeafDecay(serverLevel, scan.leaves);
            }

            // Auto-replant sapling at lowest log position if enabled
            if (FastTreeConfig.autoReplant && lowestLogPos != null && lowestLogBlock != null) {
                BlockPos groundPos = lowestLogPos.below();
                BlockState groundState = level.getBlockState(groundPos);
                if (groundState.is(BlockTags.DIRT) && level.getBlockState(lowestLogPos).isAir()) {
                    Block saplingBlock = getSaplingForLog(lowestLogBlock);
                    if (saplingBlock != null) {
                        level.setBlockAndUpdate(lowestLogPos, saplingBlock.defaultBlockState());
                    }
                }
            }

            // Hunger exhaustion
            if (logsMined > 0 && FastTreeConfig.hungerMultiplier > 0.0f) {
                player.causeFoodExhaustion(0.005F * FastTreeConfig.hungerMultiplier * logsMined);
            }

            // If originPos was processed, cancel vanilla's break event to avoid double-processing
            if (scan.logs.contains(originPos)) {
                return false;
            }

            return true;
        } finally {
            IS_FELLING.set(false);
        }
    }

    public static void queueFastLeafDecay(ServerLevel level, List<BlockPos> leaves) {
        if (leaves == null || leaves.isEmpty()) return;

        List<BlockPos> list = new ArrayList<>(leaves);
        Collections.shuffle(list);

        Deque<BlockPos> queue = PENDING_LEAVES.computeIfAbsent(level.dimension(), k -> new ArrayDeque<>());
        for (BlockPos pos : list) {
            if (!queue.contains(pos)) {
                queue.add(pos);
            }
        }
    }

    private static void onServerTick(MinecraftServer server) {
        if (PENDING_LEAVES.isEmpty()) return;

        for (Map.Entry<ResourceKey<Level>, Deque<BlockPos>> entry : PENDING_LEAVES.entrySet()) {
            ServerLevel level = server.getLevel(entry.getKey());
            if (level == null) continue;

            Deque<BlockPos> queue = entry.getValue();
            int processed = 0;
            while (!queue.isEmpty() && processed < LEAVES_PER_TICK) {
                BlockPos pos = queue.poll();
                if (pos == null) break;

                int chunkX = SectionPos.blockToSectionCoord(pos.getX());
                int chunkZ = SectionPos.blockToSectionCoord(pos.getZ());
                if (!level.hasChunk(chunkX, chunkZ)) {
                    continue;
                }

                BlockState state = level.getBlockState(pos);
                if (!isLeavesOrWart(state)) {
                    continue;
                }

                // Never decay player-placed leaves
                if (state.hasProperty(LeavesBlock.PERSISTENT) && state.getValue(LeavesBlock.PERSISTENT)) {
                    continue;
                }

                // If it's a standard leaf, verify it does not belong to another living log nearby
                if (state.is(BlockTags.LEAVES)) {
                    if (state.hasProperty(LeavesBlock.DISTANCE) && state.getValue(LeavesBlock.DISTANCE) == 7) {
                        // Leaf is disconnected from any log in vanilla, safe to decay immediately
                    } else if (hasSupportingLog(level, pos, 5)) {
                        continue;
                    }
                }

                // Natural fast decay: drops saplings/apples naturally without loud player block-breaking sound
                Block.dropResources(state, level, pos);
                level.removeBlock(pos, false);
                processed++;
            }
        }

        PENDING_LEAVES.entrySet().removeIf(e -> e.getValue().isEmpty());
    }

    private static boolean hasSupportingLog(Level level, BlockPos leafPos, int maxDist) {
        BlockPos.MutableBlockPos check = new BlockPos.MutableBlockPos();
        int lx = leafPos.getX();
        int ly = leafPos.getY();
        int lz = leafPos.getZ();

        for (int dy = -maxDist; dy <= maxDist; dy++) {
            for (int dx = -maxDist; dx <= maxDist; dx++) {
                for (int dz = -maxDist; dz <= maxDist; dz++) {
                    if (Math.abs(dx) + Math.abs(dy) + Math.abs(dz) > maxDist) continue;
                    check.set(lx + dx, ly + dy, lz + dz);
                    if (level.getBlockState(check).is(BlockTags.LOGS)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean isAxe(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.is(ItemTags.AXES);
    }

    private static boolean isLogOrRoot(BlockState state) {
        if (state.is(BlockTags.LOGS)) return true;
        if (FastTreeConfig.breakMangroveRoots && state.is(Blocks.MANGROVE_ROOTS)) return true;
        return false;
    }

    private static boolean isLeavesOrWart(BlockState state) {
        if (state.is(BlockTags.LEAVES)) return true;
        if (FastTreeConfig.breakNetherWarts) {
            if (state.is(Blocks.NETHER_WART_BLOCK) || state.is(Blocks.WARPED_WART_BLOCK) || state.is(Blocks.SHROOMLIGHT)) {
                return true;
            }
        }
        return false;
    }

    private record TreeScanResult(List<BlockPos> logs, List<BlockPos> leaves, boolean hasLeaves) {}

    private static TreeScanResult scanTree(Level level, BlockPos originPos, int maxLogs) {
        List<BlockPos> logs = new ArrayList<>();
        Set<BlockPos> visitedLogs = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();

        queue.add(originPos);
        visitedLogs.add(originPos);

        while (!queue.isEmpty() && logs.size() < maxLogs) {
            BlockPos current = queue.poll();
            logs.add(current);

            // Check 3x3x3 around current log
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;
                        BlockPos neighbor = current.offset(dx, dy, dz);
                        if (!visitedLogs.contains(neighbor)) {
                            BlockState state = level.getBlockState(neighbor);
                            if (isLogOrRoot(state)) {
                                visitedLogs.add(neighbor);
                                queue.add(neighbor);
                            }
                        }
                    }
                }
            }
        }

        // Scan for leaves surrounding the tree
        Set<BlockPos> leavesSet = new HashSet<>();
        Queue<BlockPos> leafQueue = new ArrayDeque<>();
        boolean foundLeaves = false;

        for (BlockPos logPos : logs) {
            for (int dx = -3; dx <= 3; dx++) {
                for (int dy = -1; dy <= 3; dy++) {
                    for (int dz = -3; dz <= 3; dz++) {
                        BlockPos checkPos = logPos.offset(dx, dy, dz);
                        if (!visitedLogs.contains(checkPos) && leavesSet.add(checkPos)) {
                            BlockState state = level.getBlockState(checkPos);
                            if (isLeavesOrWart(state)) {
                                foundLeaves = true;
                                leafQueue.add(checkPos);
                            } else {
                                leavesSet.remove(checkPos);
                            }
                        }
                    }
                }
            }
        }

        // Expand contiguous leaf canopy (up to 400 leaves)
        while (!leafQueue.isEmpty() && leavesSet.size() < 400) {
            BlockPos currentLeaf = leafQueue.poll();
            for (Direction dir : Direction.values()) {
                BlockPos neighbor = currentLeaf.relative(dir);
                if (!visitedLogs.contains(neighbor) && leavesSet.add(neighbor)) {
                    BlockState nState = level.getBlockState(neighbor);
                    if (isLeavesOrWart(nState)) {
                        leafQueue.add(neighbor);
                    } else {
                        leavesSet.remove(neighbor);
                    }
                }
            }
        }

        return new TreeScanResult(logs, new ArrayList<>(leavesSet), foundLeaves);
    }

    private static Block getSaplingForLog(Block logBlock) {
        String desc = logBlock.getDescriptionId().toLowerCase(Locale.ROOT);

        if (desc.contains("dark_oak")) {
            return Blocks.DARK_OAK_SAPLING;
        } else if (desc.contains("oak")) {
            return Blocks.OAK_SAPLING;
        } else if (desc.contains("spruce")) {
            return Blocks.SPRUCE_SAPLING;
        } else if (desc.contains("birch")) {
            return Blocks.BIRCH_SAPLING;
        } else if (desc.contains("jungle")) {
            return Blocks.JUNGLE_SAPLING;
        } else if (desc.contains("acacia")) {
            return Blocks.ACACIA_SAPLING;
        } else if (desc.contains("cherry")) {
            return Blocks.CHERRY_SAPLING;
        } else if (desc.contains("mangrove")) {
            return Blocks.MANGROVE_PROPAGULE;
        } else if (desc.contains("crimson")) {
            return Blocks.CRIMSON_FUNGUS;
        } else if (desc.contains("warped")) {
            return Blocks.WARPED_FUNGUS;
        }

        return Blocks.OAK_SAPLING;
    }
}
