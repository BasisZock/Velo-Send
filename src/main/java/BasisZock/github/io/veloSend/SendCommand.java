package BasisZock.github.io.veloSend;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class SendCommand implements CommandExecutor {

    private final Plugin plugin;

    public SendCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /vsend <player|selector> <server>");
            return false;
        }

        String targetArg = args[0].toLowerCase();
        String targetServer = args[1];

        List<Player> targetPlayers = new ArrayList<>();

        switch (targetArg) {
            case "@p":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "You must be a player to use the @p selector.");
                    return true;
                }
                Player senderPlayer = (Player) sender;
                double closestDistance = Double.MAX_VALUE;
                Player closestPlayer = null;

                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (onlinePlayer.equals(senderPlayer)) continue;
                    if (!onlinePlayer.getWorld().equals(senderPlayer.getWorld())) continue;

                    double distance = senderPlayer.getLocation().distanceSquared(onlinePlayer.getLocation());
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        closestPlayer = onlinePlayer;
                    }
                }

                if (closestPlayer != null) {
                    targetPlayers.add(closestPlayer);
                } else {
                    sender.sendMessage(ChatColor.RED + "No nearby player found.");
                    return true;
                }
                break;
            case "@a":
                targetPlayers.addAll(Bukkit.getOnlinePlayers());
                break;
            case "@r":
                Collection<? extends Player> online = Bukkit.getOnlinePlayers();
                if (online.isEmpty()) {
                    sender.sendMessage(ChatColor.RED + "No online players available.");
                    return true;
                }
                int index = new Random().nextInt(online.size());
                targetPlayers.add(new ArrayList<>(online).get(index));
                break;
            case "@s":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "You must be a player to use the s selector.");
                    return true;
                }
                targetPlayers.add((Player) sender);
                break;
            default:
                Player player = Bukkit.getPlayerExact(args[0]);
                if (player == null) {
                    sender.sendMessage(ChatColor.RED + "Player '" + args[0] + "' not found.");
                    return true;
                }
                targetPlayers.add(player);
                break;
        }

        // Send the target server message to each player in the list
        for (Player player : targetPlayers) {
            try {
                ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(byteArray);
                out.writeUTF("Connect");
                out.writeUTF(targetServer);
                player.sendPluginMessage(plugin, "BungeeCord", byteArray.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
                sender.sendMessage(ChatColor.RED + "Failed to send player " + player.getName() + " to server.");
                continue;
            }
        }

        StringBuilder playerList = new StringBuilder();
        for (Player player : targetPlayers) {
            playerList.append(player.getName()).append(" ");
        }
        sender.sendMessage(ChatColor.GREEN + "Sending player(s): " + playerList.toString().trim() + " to " + targetServer + ".");
        return true;
    }
}
