package kr.kieran.enchants.utils;

import kr.kieran.enchants.implementations.Enchant;
import org.bukkit.inventory.ItemStack;

public class LoreUtil
{

    public static boolean hasEnchant(ItemStack item, Enchant enchant)
    {
        if ( ! item.hasItemMeta() || ! item.getItemMeta().hasLore() ) return false;
        return item.getItemMeta().getLore().contains(enchant.getLoreEnchant());
    }

}
