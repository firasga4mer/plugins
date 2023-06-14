package org.first.test;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class join extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        event.setJoinMessage(null);
        player.teleport(new Location(Bukkit.getWorld("spawn"), -13.446, 53, 3.534));
        player.sendMessage(ChatColor.GREEN + "=== Server Rules ===");
        player.sendMessage(ChatColor.YELLOW + "2. Be respectful to others.");
        player.sendMessage(ChatColor.YELLOW + "3. No cheating or hacking.");
        player.sendMessage(ChatColor.GREEN + "====================");
        player.sendTitle(ChatColor.WHITE + "Welcome to the server!", "", -90, 6, 20);
    }


}



