package kr.kieran.enchants.enchants;

import kr.kieran.enchants.EnchantPlugin;
import kr.kieran.enchants.implementations.Enchant;
import kr.kieran.enchants.utils.Color;
import kr.kieran.enchants.utils.LoreUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PotionEnchant implements Enchant
{

    private final EnchantPlugin plugin;
    private final String key;

    public PotionEnchant(EnchantPlugin plugin, String key)
    {
        this.plugin = plugin;
        this.key = key;
    }

    // Enchant Variables
    @Override public String getEnchantName() { return plugin.getConfig().getString("enchants.potion-enchants." + key + ".enchant-name"); }
    @Override public int getCost() { return plugin.getConfig().getInt("enchants.potion-enchants." + key + ".cost"); }
    @Override public int getSlot() { return plugin.getConfig().getInt("enchants.potion-enchants." + key + ".slot"); }
    @Override public boolean isEnabled() { return plugin.getConfig().getBoolean("enchants.potion-enchants." + key + ".enabled"); }
    @Override public Set<Material> getApplicableMaterials() { return plugin.getConfig().getStringList("enchants.potion-enchants." + key + ".applicable-materials").stream().map(Material::valueOf).collect(Collectors.toSet()); }
    @Override public String getLoreEnchant() { return plugin.getConfig().getString("enchants.potion-enchants." + key + ".lore-enchant"); }

    // Potion Variables
    public PotionEffectType getEffect() { return PotionEffectType.getByName(plugin.getConfig().getString("enchants.potion-enchants." + key + ".potion-effect")); }
    public int getAmplifier() { return plugin.getConfig().getInt("enchants.potion-enchants." + key + ".amplifier"); }

    // Called when an armor piece is applied
    public boolean apply(Player player, ItemStack item)
    {
        // Verify
        if ( ! this.isEnabled() ) return false;
        if ( item == null || ! this.getApplicableMaterials().contains(item.getType()) ) return false;
        if ( ! LoreUtil.hasEnchant(item, this) ) return false;

        // Apply
        player.addPotionEffect(new PotionEffect(this.getEffect(), Integer.MAX_VALUE, this.getAmplifier(), false, false), false);
        return true;
    }

    // Called when an armor piece is removed
    public boolean remove(Player player, ItemStack item)
    {
        // Verify
        if ( ! this.isEnabled() ) return false;
        if ( item == null || ! this.getApplicableMaterials().contains(item.getType()) ) return false;
        if ( item.getItemMeta() == null || item.getItemMeta().getLore() == null) return false;
        if ( ! item.getItemMeta().getLore().contains(Color.color(this.getLoreEnchant())))return false;

        // Apply
        player.removePotionEffect(this.getEffect());
        return true;
    }

    @Override public ItemStack getBookItem()
    {
        // Args
        ItemStack shopItem = new ItemStack(Material.getMaterial(plugin.getConfig().getString("shop-item-material")));
        ItemMeta meta = shopItem.getItemMeta();

        // Name
        meta.setDisplayName(Color.color(plugin.getConfig().getString("enchants.potion-enchants." + key + ".shop-item.name").replace("%enchant-name%", this.getEnchantName())));

        // Lore
        List<String> lore = new ArrayList<>();
        for (String text : plugin.getConfig().getStringList("enchants.potion-enchants." + key + ".shop-item.lore"))
        {
            lore.add(Color.color(text.replace("%cost%", String.format("%,d", this.getCost()))));
        }

        // Apply
        meta.setLore(lore);
        shopItem.setItemMeta(meta);

        // Return
        return shopItem;
    }

}
