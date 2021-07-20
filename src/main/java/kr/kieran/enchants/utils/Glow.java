package kr.kieran.enchants.utils;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;

public class Glow extends EnchantmentWrapper
{

    private static Enchantment glow;

    private Glow() { super(255); }

    public static Enchantment getGlow()
    {
        if ( glow != null ) return glow;

        // Reflection
        try
        {
            Field field = Enchantment.class.getDeclaredField("acceptingNew");
            field.setAccessible(true);
            field.set(null, true);
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            return null;
        }

        // Instance
        glow = new Glow();
        if ( Enchantment.getById(255) == null && Enchantment.getByName("Glow") == null )
        {
            Enchantment.registerEnchantment(glow);
        }

        // Return
        return glow;
    }

    @Override
    public String getName() { return "Glow"; }

    @Override
    public boolean canEnchantItem(ItemStack item) { return true; }

    @Override
    public boolean conflictsWith(Enchantment other) { return false; }

    @Override
    public EnchantmentTarget getItemTarget() { return EnchantmentTarget.ALL; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public int getStartLevel() { return 1; }

}
