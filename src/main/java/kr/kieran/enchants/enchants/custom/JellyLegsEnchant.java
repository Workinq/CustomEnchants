package kr.kieran.enchants.enchants.custom;

import kr.kieran.enchants.EnchantPlugin;
import kr.kieran.enchants.utils.LoreUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class JellyLegsEnchant extends AbstractEnchant
{

    public JellyLegsEnchant(EnchantPlugin plugin, String key)
    {
        super(plugin, key);
    }

    @EventHandler
    public void takeDamage(EntityDamageEvent event)
    {
        if ( ! this.isEnabled() ) return;
        if ( event.getCause() != EntityDamageEvent.DamageCause.FALL ) return;
        if ( ! (event.getEntity() instanceof Player) ) return;

        Player player = (Player) event.getEntity();
        if ( player.getInventory().getBoots() == null || ! this.getApplicableMaterials().contains(player.getInventory().getBoots().getType()) ) return;

        ItemStack boots = player.getInventory().getBoots();
        if ( ! LoreUtil.hasEnchant(boots, this) ) return;

        event.setCancelled(true);
    }

}
