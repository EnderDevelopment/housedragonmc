package com.enderdevelopment.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Main extends JavaPlugin implements CommandExecutor, Listener {
    private static final long FIRE_COOLDOWN_MILLIS = 5000L;
    private final Map<UUID, Long> fireCooldowns = new HashMap<>();
    private NamespacedKey dragonEggKey;

    @Override
    public void onEnable() {
        dragonEggKey = new NamespacedKey(this, "dragon_egg");
        getCommand("dragon").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("Dragonfire enabled.");
    }

    @Override
    public void onDisable() {
        fireCooldowns.clear();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use dragon powers.", NamedTextColor.RED));
            return true;
        }
        if (!player.hasPermission("dragon.use")) {
            player.sendMessage(Component.text("You do not have permission to command the dragons.", NamedTextColor.RED));
            return true;
        }
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelp(player);
            return true;
        }
        if (args[0].equalsIgnoreCase("egg")) {
            giveDragonEgg(player);
            return true;
        }
        if (args[0].equalsIgnoreCase("fire")) {
            breatheFire(player);
            return true;
        }
        player.sendMessage(Component.text("Unknown dragon command. Use /dragon help.", NamedTextColor.RED));
        return true;
    }

    @EventHandler
    public void onDragonEggUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        ItemStack item = event.getItem();
        if (item == null || !isDragonEgg(item)) {
            return;
        }
        event.setCancelled(true);
        breatheFire(event.getPlayer());
    }

    private void sendHelp(Player player) {
        player.sendMessage(Component.text("Dragonfire commands", NamedTextColor.GOLD));
        player.sendMessage(Component.text("/dragon egg", NamedTextColor.YELLOW)
                .append(Component.text(" - receive a dragon egg", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/dragon fire", NamedTextColor.YELLOW)
                .append(Component.text(" - breathe a fireball", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("Right-click the dragon egg to breathe fire.", NamedTextColor.GRAY));
    }

    private void giveDragonEgg(Player player) {
        ItemStack egg = new ItemStack(Material.DRAGON_EGG);
        ItemMeta meta = egg.getItemMeta();
        if (meta == null) {
            return;
        }
        meta.displayName(Component.text("Dragon's Egg", NamedTextColor.DARK_PURPLE));
        meta.lore(java.util.List.of(
                Component.text("A relic of ancient fire", NamedTextColor.GRAY),
                Component.text("Right-click to breathe fire", NamedTextColor.GOLD)));
        meta.getPersistentDataContainer().set(dragonEggKey, PersistentDataType.STRING, "true");
        egg.setItemMeta(meta);
        Map<Integer, ItemStack> overflow = player.getInventory().addItem(egg);
        overflow.values().forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));
        player.sendMessage(Component.text("The dragon's egg answers your call.", NamedTextColor.LIGHT_PURPLE));
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.8f, 1.2f);
    }

    private boolean isDragonEgg(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(dragonEggKey, PersistentDataType.STRING);
    }

    private void breatheFire(Player player) {
        long now = System.currentTimeMillis();
        long lastUse = fireCooldowns.getOrDefault(player.getUniqueId(), 0L);
        long remaining = FIRE_COOLDOWN_MILLIS - (now - lastUse);
        if (remaining > 0) {
            player.sendMessage(Component.text("Your dragon fire is recharging for " + ((remaining + 999) / 1000) + "s.", NamedTextColor.RED));
            return;
        }
        fireCooldowns.put(player.getUniqueId(), now);
        Location origin = player.getEyeLocation().add(player.getLocation().getDirection().multiply(0.8));
        Vector direction = player.getLocation().getDirection().normalize();
        SmallFireball fireball = player.getWorld().spawn(origin, SmallFireball.class);
        fireball.setShooter(player);
        fireball.setVelocity(direction.multiply(1.6));
        fireball.setIsIncendiary(true);
        fireball.setYield(2.0f);
        player.getWorld().spawnParticle(Particle.FLAME, origin, 18, 0.25, 0.25, 0.25, 0.04);
        player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1.0f, 0.7f);
        player.sendActionBar(Component.text("DRAGON FIRE", NamedTextColor.GOLD));
    }
}
