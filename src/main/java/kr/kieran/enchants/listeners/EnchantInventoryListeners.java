package kr.kieran.enchants.listeners;

import kr.kieran.enchants.EnchantPlugin;
import kr.kieran.enchants.implementations.Enchant;
import kr.kieran.enchants.utils.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;

public class EnchantInventoryListeners implements Listener
{

    private final EnchantPlugin plugin;

    public EnchantInventoryListeners(EnchantPlugin plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent event)
    {
        // Verify - Inventory
        if ( event.getClickedInventory() == null || event.getView() == null ) return;

        // Inventory
        InventoryView view = event.getView();
        Player player = (Player) event.getWhoClicked();

        // Verify - Title
        if ( ! view.getTitle().equals(Color.color(plugin.getConfig().getString("inventory.name"))) ) return;

        // Verify - Item
        if ( event.getCurrentItem() == null || event.getCurrentItem().getType() != Material.getMaterial(plugin.getConfig().getString("shop-item-material")) ) return;

        // Cancel
        event.setCancelled(true);

        // Loop - Enchants
        for (Enchant enchant : plugin.getEnchantManager().getEnchants())
        {
            // Verify - Slot
            if ( enchant.getSlot() != event.getSlot() ) continue;

            // Start Charge
            int cost = enchant.getCost();
            if ( player.getLevel() < cost )
            {
                player.sendMessage(Color.color(plugin.getConfig().getString("messages.not-enough-xp")));
                return;
            }
            player.setLevel(player.getLevel() - cost);
            // End Charge

            // Add
            player.getInventory().addItem(enchant.getBookItem());
            player.updateInventory();

            // Inform
            if ( plugin.getConfig().getDouble("sounds.enchant-purchase.volume") != 0.0D )
            {
                player.playSound(player.getLocation(), Sound.valueOf(plugin.getConfig().getString("sounds.enchant-purchase.sound")), (float) plugin.getConfig().getDouble("sounds.enchant-purchase.volume"), (float) plugin.getConfig().getDouble("sounds.enchant-purchase.pitch"));
            }
            player.sendMessage(Color.color(plugin.getConfig().getString("messages.enchant-purchased").replace("%enchant%", enchant.getEnchantName()).replace("%levels%", String.format("%,d", cost))));
        }
    }

}
