package kr.kieran.enchants.enchants.custom;

import kr.kieran.enchants.EnchantPlugin;
import kr.kieran.enchants.utils.LoreUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class GrindEnchant extends AbstractEnchant
{

    public GrindEnchant(EnchantPlugin plugin, String key)
    {
        super(plugin, key);
    }

    @EventHandler
    public void killEntity(EntityDeathEvent event)
    {
        if ( ! this.isEnabled() ) return;
        if ( event.getEntity().getKiller() == null ) return;

        Player killer = event.getEntity().getKiller();
        if ( killer == null ) return;

        ItemStack item = killer.getItemInHand();
        if ( item == null || ! this.getApplicableMaterials().contains(item.getType()) ) return;
        if ( ! LoreUtil.hasEnchant(item, this) ) return;

        event.setDroppedExp((int) (event.getDroppedExp() * EnchantPlugin.get().getConfig().getDouble("enchants.custom-enchants." + key + ".exp-multiplier")));
    }

}
