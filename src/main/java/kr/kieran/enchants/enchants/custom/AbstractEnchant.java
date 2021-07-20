package kr.kieran.enchants.enchants.custom;

import kr.kieran.enchants.EnchantPlugin;
import kr.kieran.enchants.implementations.Enchant;
import kr.kieran.enchants.utils.Color;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractEnchant implements Enchant, Listener
{

    private final EnchantPlugin plugin;
    public final String key;

    // Construct
    public AbstractEnchant(EnchantPlugin plugin, String key)
    {
        this.plugin = plugin;
        this.key = key;
        this.registerListener();
    }

    private void registerListener()
    {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    // Enchant
    @Override public String getEnchantName() { return plugin.getConfig().getString("enchants.custom-enchants." + key + ".enchant-name"); }
    @Override public int getCost() { return plugin.getConfig().getInt("enchants.custom-enchants." + key + ".cost"); }
    @Override public int getSlot() { return plugin.getConfig().getInt("enchants.custom-enchants." + key + ".slot"); }
    @Override public boolean isEnabled() { return plugin.getConfig().getBoolean("enchants.custom-enchants." + key + ".enabled"); }
    @Override public Set<Material> getApplicableMaterials() { return plugin.getConfig().getStringList("enchants.custom-enchants." + key + ".applicable-materials").stream().map(Material::valueOf).collect(Collectors.toSet()); }
    @Override public String getLoreEnchant() { return plugin.getConfig().getString("enchants.custom-enchants." + key + ".lore-enchant"); }

    @Override public ItemStack getBookItem()
    {
        // Args
        ItemStack shopItem = new ItemStack(Material.getMaterial(plugin.getConfig().getString("shop-item-material")));
        ItemMeta meta = shopItem.getItemMeta();

        // Name
        meta.setDisplayName(Color.color(plugin.getConfig().getString("enchants.custom-enchants." + key + ".shop-item.name").replace("%enchant-name%", this.getEnchantName())));

        // Lore
        List<String> lore = new ArrayList<>();
        for (String text : plugin.getConfig().getStringList("enchants.custom-enchants." + key + ".shop-item.lore"))
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
