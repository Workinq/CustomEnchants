package kr.kieran.enchants.implementations;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public interface Enchant
{

    String getEnchantName();

    int getCost();

    int getSlot();

    boolean isEnabled();

    Set<Material> getApplicableMaterials();

    String getLoreEnchant();

    ItemStack getBookItem();

}
