package kr.kieran.enchants.managers;

import kr.kieran.enchants.EnchantPlugin;
import kr.kieran.enchants.enchants.PotionEnchant;
import kr.kieran.enchants.enchants.custom.GrindEnchant;
import kr.kieran.enchants.enchants.custom.JellyLegsEnchant;
import kr.kieran.enchants.enchants.custom.ObsidianBreakerEnchant;
import kr.kieran.enchants.implementations.Enchant;
import kr.kieran.enchants.enchants.custom.AbstractEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.logging.Level;

public class EnchantManager {

    private final EnchantPlugin plugin;

    private final Set<Enchant> enchants = new HashSet<>();
    private final Set<PotionEnchant> potionEnchants = new HashSet<>();
    private final Set<AbstractEnchant> customEnchants = new HashSet<>();

    public EnchantManager(EnchantPlugin plugin) {
        this.plugin = plugin;
        this.registerPotionEnchants();
        this.registerCustomEnchants();
        this.mergeEnchants();
        this.enable();
    }

    private void registerPotionEnchants() {
        for (String key : plugin.getConfig().getConfigurationSection("enchants.potion-enchants").getKeys(false)) {
            PotionEnchant enchant = new PotionEnchant(plugin, key);
            potionEnchants.add(enchant);
        }
    }

    private void registerCustomEnchants() {
        for (String key : plugin.getConfig().getConfigurationSection("enchants.custom-enchants").getKeys(false)) {
            if (key.equalsIgnoreCase("grind")) {customEnchants.add(new GrindEnchant(plugin,"grind"));continue;}
            if (key.equalsIgnoreCase("jelly-legs")) {customEnchants.add(new JellyLegsEnchant(plugin,"jelly-legs"));continue;}
            if(key.equalsIgnoreCase("obsidian-breaker")) {customEnchants.add(new ObsidianBreakerEnchant(plugin,"obsidian-breaker")); }
        }
    }

    private void mergeEnchants() {
        enchants.addAll(potionEnchants);
        enchants.addAll(customEnchants);
    }

    public Set<PotionEnchant> getPotionEnchants() {
        return Collections.unmodifiableSet(potionEnchants);
    }

    public Set<Enchant> getEnchants() {
        return Collections.unmodifiableSet(enchants);
    }

    public void enable() {
        // Counter
        int total = 0;
        int players = plugin.getServer().getOnlinePlayers().size();

        // Loop - Players
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            // Loop - Armor
            for (ItemStack armorPiece : player.getInventory().getArmorContents()) {
                // Verify - Armor
                if (armorPiece == null || armorPiece.getType() == Material.AIR) continue;

                // Loop - Enchants
                for (PotionEnchant enchant : this.getPotionEnchants()) {
                    if (enchant.apply(player, armorPiece)) total += 1;
                }
            }
        }
        plugin.getLogger().log(Level.INFO, "Enabled " + String.format("%,d", total) + " potion enchant" + (total == 1 ? "" : "s") + " for " + String.format("%,d", players) + " player" + (players == 1 ? "" : "s" + "."));
    }

    public void disable() {
        // Counter
        int total = 0;
        int players = plugin.getServer().getOnlinePlayers().size();

        // Loop - Players
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            // Loop - Armor
            for (ItemStack armorPiece : player.getInventory().getArmorContents()) {
                // Verify - Armor
                if (armorPiece == null || armorPiece.getType() == Material.AIR) continue;

                // Loop - Enchants
                for (PotionEnchant enchant : this.getPotionEnchants()) {
                    if (enchant.remove(player, armorPiece)) total += 1;
                }
            }
        }
        plugin.getLogger().log(Level.INFO, "Disabled " + String.format("%,d", total) + " potion enchant" + (total == 1 ? "" : "s") + " for " + String.format("%,d", players) + " player" + (players == 1 ? "" : "s" + "."));
    }

}
