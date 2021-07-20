package kr.kieran.enchants.enchants.custom;

import kr.kieran.enchants.EnchantPlugin;
import kr.kieran.enchants.utils.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;

public class ObsidianBreakerEnchant extends AbstractEnchant {


    public ObsidianBreakerEnchant(EnchantPlugin plugin, String key) {
        super(plugin, key);
    }

    @EventHandler
    public void damageBlock(BlockDamageEvent event) {
        if ( ! this.isEnabled() ) return;
        if ( event.getBlock() == null || event.getBlock().getType() != Material.OBSIDIAN ) return;

        Player player = event.getPlayer();
        if ( player.getItemInHand() == null || ! this.getApplicableMaterials().contains(player.getItemInHand().getType()) ) return;

        ItemStack item = player.getItemInHand();
        if ( item.getItemMeta() == null || item.getItemMeta().getLore() == null) return;
        if ( ! item.getItemMeta().getLore().contains(Color.color(this.getLoreEnchant())))return;

        event.setInstaBreak(true);
    }
}
