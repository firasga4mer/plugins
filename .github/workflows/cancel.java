package org.first.cancel;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.material.TrapDoor;
import org.bukkit.plugin.java.JavaPlugin;

public class Cancel extends JavaPlugin implements Listener {
    private Location spawnLocation;

    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        setSpawnLocation();
    }

    public void onDisable() {
    }

    private void setSpawnLocation() {
        World world = Bukkit.getWorld("world");
        if (world != null) {
            spawnLocation = world.getSpawnLocation();
        } else {
            getLogger().warning("Unable to retrieve spawn location. Make sure the 'world' exists in your server.");
        }
    }

    private boolean isWithinSpawn(Location location) {
        if (spawnLocation == null) {
            return false;
        }

        World world = location.getWorld();
        if (world == null || !world.equals(spawnLocation.getWorld())) {
            return false;
        }

        double spawnX = spawnLocation.getX();
        double spawnY = spawnLocation.getY();
        double spawnZ = spawnLocation.getZ();

        double minX = Math.min(spawnX, location.getX());
        double minY = Math.min(spawnY, location.getY());
        double minZ = Math.min(spawnZ, location.getZ());

        double maxX = Math.max(spawnX, location.getX());
        double maxY = Math.max(spawnY, location.getY());
        double maxZ = Math.max(spawnZ, location.getZ());

        return minX <= spawnX && minY <= spawnY && minZ <= spawnZ
                && maxX >= spawnX && maxY >= spawnY && maxZ >= spawnZ;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();
        if (player.getGameMode() == GameMode.SURVIVAL && !isWithinSpawn(location)) {
            event.setCancelled(true);
        } else {
            event.setCancelled(false);
        }

        if (player.getGameMode() == GameMode.SURVIVAL && isWithinSpawn(location) && event.getBlock().getType() == Material.WHEAT && isPlayerOnTopOfBlock(player, location)) {
            event.setCancelled(true);
            player.sendMessage("You cannot break wheat while standing on it in the spawn area.");
        }

    }
    private boolean isPlayerOnTopOfBlock(Player player, Location location) {
        Location playerLocation = player.getLocation();
        double playerY = playerLocation.getY();
        double blockY = location.getY();
        return playerY - blockY <= 0.5;
    }


    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();

        if (player.getGameMode() == GameMode.SURVIVAL && !isWithinSpawn(location)) {
            event.setCancelled(true);
        } else {
            event.setCancelled(false);
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        Player player = (Player) event.getPlayer();

        if (holder != null && !isWithinSpawn(player.getLocation())) {
            if (holder.getClass().getName().equals("org.bukkit.block.EnderChest")
                    || holder instanceof Barrel
                    || holder instanceof CraftingInventory
                    || holder instanceof EnchantingInventory
                    || holder instanceof Chest
                    || holder instanceof EnderChest
                    || holder instanceof Furnace
                    || holder instanceof EnchantingTable
                    || holder instanceof CraftItemEvent) {
                event.setCancelled(true);
                player.sendMessage("You cannot open this type of inventory in the spawn area.");
            }
        }
    }


    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player damagedPlayer = (Player) event.getEntity();
            Player damagerPlayer = (Player) event.getDamager();

            if (event.getEntity() instanceof Player && isWithinSpawn(damagerPlayer.getLocation())){
                event.setCancelled(true);
            }
            else
            {
                event.setCancelled(false);
            }

        } else if (event.getEntity() instanceof Player) {
            Player damagedPlayer = (Player) event.getEntity();


                if (event.getEntity() instanceof Player) {
                    Player player = (Player) event.getEntity();
                    if (isWithinSpawn(player.getLocation())) {
                        event.setCancelled(false);
                    }
                    else
                    {
                        event.setCancelled(true);
                    }

            }

        }
    }


    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.SURVIVAL && isWithinSpawn(player.getLocation())) {
            event.setCancelled(false);
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();

        if (event.getClickedBlock() != null && event.getClickedBlock().getState().getData() instanceof TrapDoor) {
            TrapDoor trapdoor = (TrapDoor) event.getClickedBlock().getState().getData();

            if (player.getGameMode() == GameMode.SURVIVAL && trapdoor.isOpen() && isWithinSpawn(location)) {
                event.setCancelled(true);
                player.sendMessage("You cannot interact with open trapdoors in the spawn area.");
            } else {
                event.setCancelled(false);
            }
        }
    }


}
