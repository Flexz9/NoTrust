package flexzcraft.notrust.forge;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import flexzcraft.notrust.entity.Log;
import flexzcraft.notrust.manager.LogManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.TradeWithVillagerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static flexzcraft.notrust.forge.CommandHandler.printTable;

@Mod.EventBusSubscriber(modid = NoTrust.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
public class PlayerInteractionHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int LOG_INTERVAL = 20*30;
    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        LogManager.getInstance().switchAndProcessQueue();
        if (event.phase == TickEvent.Phase.START) {
            // Increment the tick counter
            tickCounter++;

            // Check if it's time to log player positions
            if (tickCounter >= LOG_INTERVAL) {
                logPlayerPositions(event.getServer());
                tickCounter = 0; // Reset the tick counter
            }
        }
    }

    private static void logPlayerPositions(MinecraftServer server) {

        // Iterate through the list of players and log their positions
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            String playerName = player.getName().getString();
            String dimension = player.level().dimension().location().toString();
            double x = player.getOnPos().getX();
            double y = player.getOnPos().getY();
            double z = player.getOnPos().getZ();

            Log blockBreak = new Log(playerName, x, y, z, dimension, "PlayerPos", "");
            LogManager.getInstance().log(blockBreak);
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer() != null) {
            int x = event.getPos().getX();
            int y = event.getPos().getY();
            int z = event.getPos().getZ();
            String dimension = event.getPlayer().level().dimension().location().toString();
            String playerName = event.getPlayer().getName().getString();

            Log blockBreak = new Log(playerName, x, y, z, dimension, "BlockBreak", event.getState().getBlock().getDescriptionId());
            LogManager.getInstance().log(blockBreak);
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() != null) {
            int x = event.getPos().getX();
            int y = event.getPos().getY();
            int z = event.getPos().getZ();
            String dimension = event.getEntity().level().dimension().location().toString();
            String playerName = event.getEntity().getName().getString();

            Log blockBreak = new Log(playerName, x, y, z, dimension, "BlockPlace", event.getState().getBlock().getDescriptionId());
            LogManager.getInstance().log(blockBreak);
        }
    }

    @SubscribeEvent
    public static void onVillagerTrade(TradeWithVillagerEvent event) {
        double x = event.getAbstractVillager().getX();
        double y = event.getAbstractVillager().getY();
        double z = event.getAbstractVillager().getZ();
        String dimension = event.getAbstractVillager().level().dimension().location().toString();
        String playerName = event.getEntity().getName().getString();

        Log villagerTrade = new Log(playerName, x, y, z, dimension, "VillagerTrade", "Villager="+event.getAbstractVillager().getId()+";Trade="+event.getMerchantOffer().getResult().getItem().getDescriptionId());
        LogManager.getInstance().log(villagerTrade);
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if(!event.getHand().equals(InteractionHand.MAIN_HAND) || event.getLevel().isClientSide) {
            if (event.getEntity().isShiftKeyDown()
                    && event.getEntity().getItemInHand(InteractionHand.MAIN_HAND).getItem().getDescriptionId().contains("stick")
                    && event.getEntity().getItemInHand(InteractionHand.OFF_HAND).getItem().getDescriptionId().contains("sword"))
                event.setCanceled(true);
            return;
        }

        String dimension = event.getLevel().dimension().location().toString();

        if (event.getEntity().isShiftKeyDown()
                && event.getEntity().getItemInHand(InteractionHand.MAIN_HAND).getItem().getDescriptionId().contains("stick")
                && event.getEntity().getItemInHand(InteractionHand.OFF_HAND).getItem().getDescriptionId().contains("sword")){
            int range = 3;

            int x1 = event.getEntity().getBlockX() - range;
            int y1 = event.getEntity().getBlockY() - range;
            int z1 = event.getEntity().getBlockZ() - range;

            int x2 = event.getEntity().getBlockX() + range;
            int y2 = event.getEntity().getBlockY() + range;
            int z2 = event.getEntity().getBlockZ() + range;

            StringBuilder sb = new StringBuilder();
            sb.append(String.format(ChatFormatting.RED + "Log range of %s : \n\n" + ChatFormatting.WHITE, range));

            List<Log> listOfLogs = LogManager.getInstance().selectLogsByPosition(x1, y1, z1, x2, y2, z2, dimension, 10);
            printTable(sb, listOfLogs, true);

            event.getEntity().sendSystemMessage(Component.literal(sb.toString()));

            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
        if(!event.getHand().equals(InteractionHand.MAIN_HAND) || event.getLevel().isClientSide) {
            if (event.getEntity().isShiftKeyDown()
                    && event.getEntity().getItemInHand(InteractionHand.MAIN_HAND).getItem().getDescriptionId().contains("stick")
                    && event.getEntity().getItemInHand(InteractionHand.OFF_HAND).getItem().getDescriptionId().contains("sword"))
                event.setCanceled(true);
            return;
        }

        int x = event.getPos().getX();
        int y = event.getPos().getY();
        int z = event.getPos().getZ();
        String dimension = event.getLevel().dimension().location().toString();

        if (event.getEntity().isShiftKeyDown()
                && event.getEntity().getItemInHand(InteractionHand.MAIN_HAND).getItem().getDescriptionId().contains("stick")
                && event.getEntity().getItemInHand(InteractionHand.OFF_HAND).getItem().getDescriptionId().contains("sword")){

            StringBuilder sb = new StringBuilder();
            sb.append(String.format(ChatFormatting.RED + "Log for block %s, %s, %s: \n\n" + ChatFormatting.WHITE, x, y, z));

            List<Log> listOfLogs = LogManager.getInstance().selectLogsByPosition(x, y, z, dimension, 10);
            printTable(sb, listOfLogs);

            event.getEntity().sendSystemMessage(Component.literal(sb.toString()));

            event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if(!event.getHand().equals(InteractionHand.MAIN_HAND) || event.getLevel().isClientSide) {
            if (event.getEntity().isShiftKeyDown()
                    && event.getEntity().getItemInHand(InteractionHand.MAIN_HAND).getItem().getDescriptionId().contains("stick")
                    && event.getEntity().getItemInHand(InteractionHand.OFF_HAND).getItem().getDescriptionId().contains("sword"))
                event.setCanceled(true);
            return;
        }

        int x = event.getPos().getX();
        int y = event.getPos().getY();
        int z = event.getPos().getZ();
        String dimension = event.getLevel().dimension().location().toString();
        String playerName = event.getEntity().getName().getString();

        if (event.getEntity().isShiftKeyDown()
                && event.getEntity().getItemInHand(InteractionHand.MAIN_HAND).getItem().getDescriptionId().contains("stick")
                && event.getEntity().getItemInHand(InteractionHand.OFF_HAND).getItem().getDescriptionId().contains("sword")){

            StringBuilder sb = new StringBuilder();
            sb.append(String.format(ChatFormatting.RED + "Log for block %s, %s, %s: \n\n" + ChatFormatting.WHITE, x, y, z));

            List<Log> listOfLogs = LogManager.getInstance().selectLogsByPosition(x, y, z, dimension, 10);
            printTable(sb, listOfLogs);

            event.getEntity().sendSystemMessage(Component.literal(sb.toString()));

            event.setCanceled(true);
            return;
        }

        if ((event.getLevel().getBlockState(event.getPos()).getBlock() instanceof ChestBlock
                || event.getLevel().getBlockState(event.getPos()).getBlock() instanceof BarrelBlock
                || event.getLevel().getBlockState(event.getPos()).getBlock() instanceof ShulkerBoxBlock
                || event.getLevel().getBlockState(event.getPos()).getBlock() instanceof HopperBlock)
                && event.getLevel().getBlockEntity(event.getHitVec().getBlockPos()) != null) {
            RandomizableContainerBlockEntity chestBlock = (RandomizableContainerBlockEntity) event.getLevel().getBlockEntity(event.getHitVec().getBlockPos());

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < chestBlock.getContainerSize(); i++) {
                sb.append(chestBlock.getItem(i).getItem().getDescriptionId()).append(chestBlock.getItem(i).getCount()).append("-");
            }

            Log rightClickEvent = new Log(playerName, x, y, z, dimension, "RightClickContainer", event.getLevel().getBlockState(event.getPos()).getBlock().getDescriptionId() + "("+generateHash(sb.toString())+")");
            LogManager.getInstance().log(rightClickEvent);
        } else if (event.getLevel().getBlockState(event.getPos()).getBlock() instanceof DoorBlock
                || event.getLevel().getBlockState(event.getPos()).getBlock() instanceof TrapDoorBlock
                || event.getLevel().getBlockState(event.getPos()).getBlock() instanceof LeverBlock
                || event.getLevel().getBlockState(event.getPos()).getBlock() instanceof ButtonBlock
                || event.getLevel().getBlockState(event.getPos()).getBlock() instanceof ChiseledBookShelfBlock) {

            Log rightClickEvent = new Log(playerName, x, y, z, dimension, "RightClick", event.getLevel().getBlockState(event.getPos()).getBlock().getDescriptionId());
            LogManager.getInstance().log(rightClickEvent);
        }
    }

    public static String generateHash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());

            // Convert the byte array to a fixed character string (hexadecimal)
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return "null";
        }
    }
}