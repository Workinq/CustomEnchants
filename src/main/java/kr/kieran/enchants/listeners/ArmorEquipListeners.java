package kr.kieran.enchants.listeners;

import kr.kieran.enchants.EnchantPlugin;
import kr.kieran.enchants.events.ArmorEquipEvent;
import kr.kieran.enchants.enchants.PotionEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class ArmorEquipListeners implements Listener
{

    private final EnchantPlugin plugin;

    public ArmorEquipListeners(EnchantPlugin plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onArmorEquip(ArmorEquipEvent event)
    {
        Player player = event.getPlayer();
        ItemStack newArmor = event.getNewArmorPiece();
        ItemStack oldArmor = event.getOldArmorPiece();

        // New Armor
        if ( newArmor != null && newArmor.getType() != Material.AIR )
        {
            for (PotionEnchant enchant : plugin.getEnchantManager().getPotionEnchants())
            {
                enchant.apply(player, newArmor);
            }
        }

        // Old Armor
        if ( oldArmor != null && oldArmor.getType() != Material.AIR )
        {
            for (PotionEnchant enchant : plugin.getEnchantManager().getPotionEnchants())
            {
                enchant.remove(player, oldArmor);
            }
        }
    }

    @EventHandler
    public void applyFix(ArmorEquipEvent event)
    {
        Player player = event.getPlayer();
        ItemStack newArmor = event.getNewArmorPiece();
        ItemStack oldArmor = event.getOldArmorPiece();

        if ( newArmor != null && newArmor.getType() != Material.AIR )
        {

        }
    }

}
