package kr.kieran.enchants.commands;

import kr.kieran.enchants.EnchantPlugin;
import kr.kieran.enchants.implementations.Enchant;
import kr.kieran.enchants.utils.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class CustomEnchantCommand implements CommandExecutor
{

    private final EnchantPlugin plugin;

    public CustomEnchantCommand(EnchantPlugin plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if ( ! (sender instanceof Player) )
        {
            sender.sendMessage(Color.color(plugin.getConfig().getString("messages.not-a-player")));
            return true;
        }

        Player player = (Player) sender;

        if ( ! player.hasPermission("enchants.gui") )
        {
            player.sendMessage(Color.color(plugin.getConfig().getString("messages.no-permission")));
            return true;
        }

        player.openInventory(this.getEnchantsGui());
        return true;
    }

    private Inventory getEnchantsGui()
    {
        // Args
        Inventory inventory = plugin.getServer().createInventory(null, plugin.getConfig().getInt("inventory.size"), Color.color(plugin.getConfig().getString("inventory.name")));

        // Populate
        for (Enchant enchant : plugin.getEnchantManager().getEnchants())
        {
            if ( ! enchant.isEnabled() ) continue;
            inventory.setItem(enchant.getSlot(), enchant.getBookItem());
        }

        // Return
        return inventory;
    }

}
