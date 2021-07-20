package kr.kieran.enchants.listeners;

import kr.kieran.enchants.EnchantPlugin;
import kr.kieran.enchants.implementations.Enchant;
import kr.kieran.enchants.utils.Color;
import kr.kieran.enchants.utils.Glow;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class EnchantApplyListeners implements Listener
{

    private final EnchantPlugin plugin;

    public EnchantApplyListeners(EnchantPlugin plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBookUse(InventoryClickEvent event)
    {
        Player player = (Player) event.getWhoClicked();
        if ( player == null ) return;

        ItemStack current = event.getCurrentItem();
        ItemStack cursor = event.getCursor();
        if ( cursor == null || cursor.getType() == Material.AIR || current == null || current.getType() == Material.AIR ) return;
        if ( ! cursor.hasItemMeta() || ! cursor.getItemMeta().hasDisplayName() ) return;

        String name = cursor.getItemMeta().getDisplayName();

        for (Enchant enchant : plugin.getEnchantManager().getEnchants())
        {
            // Start Verify
            if ( ! enchant.getBookItem().getItemMeta().getDisplayName().equals(name) ) continue;
            if ( event.getInventory().getType() == InventoryType.ANVIL && (enchant.getBookItem().getItemMeta().getDisplayName().equals(name) || enchant.getBookItem().getItemMeta().getDisplayName().equals(current.getItemMeta().getDisplayName())) )
            {
                event.setCancelled(true);
                return;
            }
            if ( ! enchant.isEnabled() )
            {
                player.sendMessage(Color.color(plugin.getConfig().getString("messages.enchant-disabled")));
                return;
            }
            if ( ! enchant.getApplicableMaterials().contains(current.getType()) )
            {
                player.sendMessage(Color.color(plugin.getConfig().getString("messages.enchant-cannot-apply")));
                return;
            }
            if ( current.hasItemMeta() && current.getItemMeta().hasDisplayName() )
            {
                List<String> blacklistedNames = Color.color(plugin.getConfig().getStringList("blocked-names"));
                if ( blacklistedNames.contains(current.getItemMeta().getDisplayName()) )
                {
                    player.sendMessage(Color.color(plugin.getConfig().getString("messages.disallowed-name")));
                    return;
                }
            }
            if ( current.getItemMeta().hasLore() && current.getItemMeta().getLore().contains(Color.color(enchant.getLoreEnchant())) )
            {
                player.sendMessage(Color.color(plugin.getConfig().getString("messages.enchant-already-applied")));
                return;
            }
            // End Verify

            // Cancel
            event.setCancelled(true);
            event.setCursor(new ItemStack(Material.AIR));

            // Item
            ItemStack newItem = current.clone();

            // Enchants
            for (Enchantment enchants : current.getEnchantments().keySet())
            {
                newItem.addUnsafeEnchantment(enchants, current.getEnchantmentLevel(enchants));
            }
            newItem.addUnsafeEnchantment(Glow.getGlow(), 1);

            // Meta
            ItemMeta meta = newItem.getItemMeta();

            // Name
            if ( current.getItemMeta().hasDisplayName() ) meta.setDisplayName(current.getItemMeta().getDisplayName());

            // Lore
            if ( ! enchant.getLoreEnchant().isEmpty() || ! enchant.getLoreEnchant().equals("") )
            {
                List<String> lore = new ArrayList<>();
                lore.add(Color.color(enchant.getLoreEnchant()));
                if ( current.getItemMeta().hasLore() ) lore.addAll(current.getItemMeta().getLore());
                meta.setLore(lore);
            }

            // Item Flags
            meta.getItemFlags().addAll(current.getItemMeta().getItemFlags());

            // Unbreakable
            meta.spigot().setUnbreakable(current.getItemMeta().spigot().isUnbreakable());

            // Apply
            newItem.setItemMeta(meta);

            event.setCurrentItem(newItem);

            // Update
            player.updateInventory();

            // Inform
            if ( plugin.getConfig().getDouble("sounds.enchant-apply.volume") != 0.0D )
            {
                player.playSound(player.getLocation(), Sound.valueOf(plugin.getConfig().getString("sounds.enchant-apply.sound")), (float) plugin.getConfig().getDouble("sounds.enchant-apply.volume"), (float) plugin.getConfig().getDouble("sounds.enchant-apply.pitch"));
            }
            player.sendMessage(Color.color(plugin.getConfig().getString("messages.enchant-applied").replace("%enchant%", Color.color(enchant.getEnchantName()))));
        }
    }

}
