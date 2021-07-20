package kr.kieran.enchants.listeners;

import kr.kieran.enchants.EnchantPlugin;
import kr.kieran.enchants.implementations.Enchant;
import kr.kieran.enchants.enchants.PotionEnchant;
import kr.kieran.enchants.utils.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class EnchantFixListeners implements Listener
{

    private final EnchantPlugin plugin;

    public EnchantFixListeners(EnchantPlugin plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void useAnvil(InventoryClickEvent event)
    {
        Player player = (Player) event.getWhoClicked();
        if ( player == null ) return;
        if ( event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR ) return;

        for (Enchant enchant : plugin.getEnchantManager().getEnchants())
        {
            if ( ! event.getCurrentItem().hasItemMeta() || ! event.getCurrentItem().getItemMeta().hasDisplayName() ) return;
            if ( enchant.getBookItem().getItemMeta().getDisplayName().equals(event.getCurrentItem().getItemMeta().getDisplayName()) && event.getInventory().getType() == InventoryType.ANVIL )
            {
                player.sendMessage(Color.color(plugin.getConfig().getString("messages.cannot-use-anvil")));
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        for (ItemStack armorPiece : player.getInventory().getArmorContents())
        {
            if ( armorPiece == null || armorPiece.getType() == Material.AIR ) continue;
            for (PotionEnchant enchant : plugin.getEnchantManager().getPotionEnchants())
            {
                enchant.apply(player, armorPiece);
            }
        }
    }

    @EventHandler
    public void playerLogout(PlayerQuitEvent event) { this.playerLogout(event.getPlayer()); }

    @EventHandler
    public void playerLogout(PlayerKickEvent event) { this.playerLogout(event.getPlayer()); }

    private void playerLogout(Player player)
    {
        for (ItemStack armorPiece : player.getInventory().getArmorContents())
        {
            if ( armorPiece == null || armorPiece.getType() == Material.AIR ) continue;
            for (PotionEnchant enchant : plugin.getEnchantManager().getPotionEnchants())
            {
                enchant.remove(player, armorPiece);
            }
        }
    }

}
