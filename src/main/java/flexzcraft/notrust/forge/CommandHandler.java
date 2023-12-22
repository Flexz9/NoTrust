package flexzcraft.notrust.forge;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.logging.LogUtils;
import flexzcraft.notrust.entity.Log;
import flexzcraft.notrust.manager.LogManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

import java.util.Comparator;
import java.util.List;

public class CommandHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("notrust")
                        .requires((player) -> player.hasPermission(2))
                        .then(getBlockHistory())
                        .then(getInRangeHistory())
        );
    }

    public static ArgumentBuilder<CommandSourceStack, ?> getBlockHistory() {
        return Commands.literal("block")
                .then(Commands.argument("x", (ArgumentType) IntegerArgumentType.integer())
                        .then(Commands.argument("y", (ArgumentType) IntegerArgumentType.integer())
                                .then(Commands.argument("z", (ArgumentType) IntegerArgumentType.integer())
                                        .then(Commands.literal("player")
                                                .then(Commands.argument("player", (ArgumentType) StringArgumentType.string())
                                                    .then(Commands.argument("limit", (ArgumentType) IntegerArgumentType.integer())
                                                            .executes((command) -> {
                                                                getBlockForPlayerHistoryText(command, IntegerArgumentType.getInteger(command, "limit"));
                                                                return 1;
                                                            })
                                                    )
                                                    .executes((command) -> {
                                                        getBlockForPlayerHistoryText(command,  10);
                                                        return 1;
                                                    })
                                            )
                                        )
                                        .then(Commands.argument("limit", (ArgumentType) IntegerArgumentType.integer())
                                                .executes((command) -> {
                                                    getBlockHistoryText(command, IntegerArgumentType.getInteger(command, "limit"));
                                                    return 1;
                                                })
                                        )
                                        .executes((command) -> {
                                            getBlockHistoryText(command,  10);
                                            return 1;
                                        })
                                )
                        )
                );
    }

    private static void getBlockForPlayerHistoryText(CommandContext command, int limit) {
        ServerPlayer player = ((CommandSourceStack) command.getSource()).getPlayer();
        if (player != null) {
            int x = IntegerArgumentType.getInteger(command, "x");
            int y = IntegerArgumentType.getInteger(command, "y");
            int z = IntegerArgumentType.getInteger(command, "z");
            String dimension = ((CommandSourceStack) command.getSource()).getLevel().dimension().location().toString();
            String playerToFind = StringArgumentType.getString(command, "player");

            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Log for block %s, %s, %s for player %s: \n\n", x, y, z, playerToFind));

            List<Log> listOfLogs = LogManager.getInstance().selectLogsByPositionAndPlayer(x, y, z, dimension, playerToFind, limit);
            printTable(sb, listOfLogs);

            player.sendSystemMessage(Component.literal(sb.toString()));
        }
    }

    private static void getBlockHistoryText(CommandContext command, int limit) {
        ServerPlayer player = ((CommandSourceStack) command.getSource()).getPlayer();
        if (player != null) {
            int x = IntegerArgumentType.getInteger(command, "x");
            int y = IntegerArgumentType.getInteger(command, "y");
            int z = IntegerArgumentType.getInteger(command, "z");
            String dimension = ((CommandSourceStack) command.getSource()).getLevel().dimension().location().toString();

            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Log for block %s, %s, %s: \n\n", x, y, z));

            List<Log> listOfLogs = LogManager.getInstance().selectLogsByPosition(x, y, z, dimension, limit);
            printTable(sb, listOfLogs);

            player.sendSystemMessage(Component.literal(sb.toString()));
        }
    }

    private static ArgumentBuilder<CommandSourceStack, ?> getInRangeHistory() {
        return Commands.literal("range")
                .then(Commands.argument("range", (ArgumentType) IntegerArgumentType.integer())
                        .then(Commands.literal("player")
                            .then(Commands.argument("player", (ArgumentType) StringArgumentType.string())
                                    .then(Commands.argument("limit", (ArgumentType) IntegerArgumentType.integer())
                                            .executes((command) -> {
                                                getInRangeForPlayerHistoryText(command, IntegerArgumentType.getInteger(command, "limit"));
                                                return 1;
                                            })
                                    )
                                    .executes((command) -> {
                                        getInRangeForPlayerHistoryText(command,  10);
                                        return 1;
                                    })
                            )
                        )
                        .then(Commands.argument("limit", (ArgumentType) IntegerArgumentType.integer())
                                .executes((command) -> {
                                    getInRangeHistoryText(command, IntegerArgumentType.getInteger(command, "limit"));
                                    return 1;
                                })
                        )
                        .executes((command) -> {
                            getInRangeHistoryText(command,  10);
                            return 1;
                        })
                );
    }

    private static void getInRangeForPlayerHistoryText(CommandContext command, int limit) {
        ServerPlayer player = ((CommandSourceStack) command.getSource()).getPlayer();
        if (player != null) {
            int range = IntegerArgumentType.getInteger(command, "range");
            String dimension = ((CommandSourceStack) command.getSource()).getLevel().dimension().location().toString();
            String playerToFind = StringArgumentType.getString(command, "player");

            int x1 = player.getBlockX() - range;
            int y1 = player.getBlockY() - range;
            int z1 = player.getBlockZ() - range;

            int x2 = player.getBlockX() + range;
            int y2 = player.getBlockY() + range;
            int z2 = player.getBlockZ() + range;

            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Log range of %s for player %s: \n\n", range, playerToFind));

            List<Log> listOfLogs = LogManager.getInstance().selectLogsByPositionAndPlayer(x1, y1, z1, x2, y2, z2, dimension, playerToFind, limit);
            printTable(sb, listOfLogs, true);

            player.sendSystemMessage(Component.literal(sb.toString()));
        }
    }

    private static void getInRangeHistoryText(CommandContext command, int limit) {
        ServerPlayer player = ((CommandSourceStack) command.getSource()).getPlayer();
        if (player != null) {
            int range = IntegerArgumentType.getInteger(command, "range");
            String dimension = ((CommandSourceStack) command.getSource()).getLevel().dimension().location().toString();

            int x1 = player.getBlockX() - range;
            int y1 = player.getBlockY() - range;
            int z1 = player.getBlockZ() - range;

            int x2 = player.getBlockX() + range;
            int y2 = player.getBlockY() + range;
            int z2 = player.getBlockZ() + range;

            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Log range of %s : \n\n", range));

            List<Log> listOfLogs = LogManager.getInstance().selectLogsByPosition(x1, y1, z1, x2, y2, z2, dimension, limit);
            printTable(sb, listOfLogs, true);

            player.sendSystemMessage(Component.literal(sb.toString()));
        }
    }

    public static void printTable(StringBuilder sb, List<Log> listOfLogs) {
        printTable(sb, listOfLogs, false);
    }

    public static void printTable(StringBuilder sb, List<Log> listOfLogs, boolean printCoordinates) {
        if (listOfLogs.size() == 0) {
            sb.append("No logs found.");
        } else {
            final int[] counter = {listOfLogs.size()};
            final String[] container = {""};
            listOfLogs.sort(Comparator.comparing(Log::getTimestamp));
            listOfLogs.forEach(log -> {
                sb.append(String.format("--------[%s/%s]-------\nTimestamp: %s\nPlayer: %s\nAction: %s", counter[0], listOfLogs.size(), log.getTimestamp(), log.getPlayername(), log.getAction()));
                if (!log.getText().isEmpty() && !printCoordinates) {
                    if (log.getAction().equalsIgnoreCase("RightClickContainer")) {
                        if (counter[0] == 10) {
                            sb.append(String.format("\nInfo: Contents changed - unknown"));
                        } else {
                            sb.append(String.format("\nInfo: Contents changed - %s", !container[0].equals(log.getText())));
                        }
                        container[0] = log.getText();
                    } else {
                        sb.append(String.format("\nInfo: %s", log.getText()));
                    }
                }
                if (printCoordinates)
                    sb.append(String.format("\nBlock: x=%s y=%s z= %s", log.getX(), log.getY(), log.getZ()));
                sb.append("\n");
                counter[0]--;
            });
        }
    }
}