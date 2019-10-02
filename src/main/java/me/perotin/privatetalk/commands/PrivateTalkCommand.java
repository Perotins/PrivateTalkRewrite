package me.perotin.privatetalk.commands;

import me.perotin.privatetalk.PrivateTalk;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/* Created by Perotin on 8/20/19 */

/**
 * Base command for PrivateTalk, extends (need to look into)Command for ability to set custom command names, aliases etc.
 */
public class PrivateTalkCommand  implements CommandExecutor  {



    private PrivateTalk plugin;


   public PrivateTalkCommand(PrivateTalk plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        player.sendTitle(ChatColor.RED+"Use only 1 word", "", 0, 20*3, 20);
        player.sendMessage("tt");
        return true;
    }



}
