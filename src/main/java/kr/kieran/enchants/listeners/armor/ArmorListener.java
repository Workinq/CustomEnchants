package kr.kieran.enchants.listeners.armor;

import kr.kieran.enchants.ArmorType;
import kr.kieran.enchants.events.ArmorEquipEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @author Arnah
 * @since Jul 30, 2015
 */
public class ArmorListener implements Listener
{

    private final List<String> blockedMaterials;

    public ArmorListener(List<String> blockedMaterials)
    {
        this.blockedMaterials = blockedMaterials;
    }

    // Event Priority is highest because other plugins might cancel the events before we check.
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public final void inventoryClick(final InventoryClickEvent event)
    {
        boolean shift = false, numberkey = false;

        if ( event.getAction() == InventoryAction.NOTHING ) return; // Why does this get called if nothing happens??
        if ( event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT ) shift = true;
        if ( event.getClick() == ClickType.NUMBER_KEY ) numberkey = true;
        if ( event.getSlotType() != SlotType.ARMOR && event.getSlotType() != SlotType.QUICKBAR && event.getSlotType() != SlotType.CONTAINER ) return;
        if ( event.getClickedInventory() != null && event.getClickedInventory().getType() != InventoryType.PLAYER ) return;
        if ( event.getInventory().getType() != InventoryType.CRAFTING && event.getInventory().getType() != InventoryType.PLAYER ) return;
        if ( ! (event.getWhoClicked() instanceof Player) ) return;

        ArmorType newArmorType = ArmorType.matchType(shift ? event.getCurrentItem() : event.getCursor());

        // Used for drag and drop checking to make sure you aren't trying to place a helmet in the boots slot.
        if ( ! shift && newArmorType != null && event.getRawSlot() != newArmorType.getSlot() ) return;

        if ( shift )
        {
            newArmorType = ArmorType.matchType(event.getCurrentItem());
            if ( newArmorType == null ) return;

            boolean equipping = true;
            if ( event.getRawSlot() == newArmorType.getSlot() ) equipping = false;

            if (
                    newArmorType == ArmorType.HELMET
                    && equipping == isAirOrNull(event.getWhoClicked().getInventory().getHelmet()) || newArmorType == ArmorType.CHESTPLATE
                    && equipping == isAirOrNull(event.getWhoClicked().getInventory().getChestplate()) || newArmorType == ArmorType.LEGGINGS
                    && equipping == isAirOrNull(event.getWhoClicked().getInventory().getLeggings()) || newArmorType == ArmorType.BOOTS
                    && equipping == isAirOrNull(event.getWhoClicked().getInventory().getBoots()))
            {
                ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) event.getWhoClicked(), ArmorEquipEvent.EquipMethod.SHIFT_CLICK, newArmorType, equipping ? null : event.getCurrentItem(), equipping ? event.getCurrentItem() : null);
                Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
                if ( armorEquipEvent.isCancelled() )
                {
                    event.setCancelled(true);
                }
            }
        }
        else
        {
            ItemStack newArmorPiece = event.getCursor();
            ItemStack oldArmorPiece = event.getCurrentItem();
            if ( numberkey )
            {
                if ( event.getClickedInventory().getType() == InventoryType.PLAYER ) // Prevents shit in the 2by2 crafting
                {
                    // event.getClickedInventory() == The players inventory
                    // event.getHotBarButton() == key people are pressing to equip or unequip the item to or from.
                    // event.getRawSlot() == The slot the item is going to.
                    // event.getSlot() == Armor slot, can't use e.getRawSlot() as that gives a hotbar slot ;-;
                    ItemStack hotbarItem = event.getClickedInventory().getItem(event.getHotbarButton());
                    if ( ! isAirOrNull(hotbarItem) ) // Equipping
                    {
                        newArmorType = ArmorType.matchType(hotbarItem);
                        newArmorPiece = hotbarItem;
                        oldArmorPiece = event.getClickedInventory().getItem(event.getSlot());
                    }
                    else
                    {
                        newArmorType = ArmorType.matchType(!isAirOrNull(event.getCurrentItem()) ? event.getCurrentItem() : event.getCursor());
                    }
                }
            }
            else
            {
                if ( isAirOrNull(event.getCursor()) && ! isAirOrNull(event.getCurrentItem()) ) // Unequip with no new item going into the slot.
                {
                    newArmorType = ArmorType.matchType(event.getCurrentItem());
                }
                // event.getCurrentItem() == Unequip
                // event.getCursor() == Equip
                // newArmorType = ArmorType.matchType( ! isAirOrNull(event.getCurrentItem()) ? event.getCurrentItem() : event.getCursor() );
            }
            if ( newArmorType != null && event.getRawSlot() == newArmorType.getSlot() )
            {
                ArmorEquipEvent.EquipMethod method = ArmorEquipEvent.EquipMethod.PICK_DROP;
                if ( event.getAction().equals(InventoryAction.HOTBAR_SWAP) || numberkey ) method = ArmorEquipEvent.EquipMethod.HOTBAR_SWAP;
                ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) event.getWhoClicked(), method, newArmorType, oldArmorPiece, newArmorPiece);
                Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
                if ( armorEquipEvent.isCancelled() ) event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerInteractEvent(PlayerInteractEvent event)
    {
        if ( event.useItemInHand() == Result.DENY ) return;
        if ( event.getAction() == Action.PHYSICAL ) return;
        if ( event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK ) return;

        Player player = event.getPlayer();
        if ( event.useInteractedBlock() != Result.DENY )
        {
            if ( event.getClickedBlock() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK && ! player.isSneaking() ) // Having both of these checks is useless, might as well do it though.
            {
                // Some blocks have actions when you right click them which stops the client from equipping the armor in hand.
                Material material = event.getClickedBlock().getType();
                for (String blocked : blockedMaterials)
                {
                    if ( material.name().equalsIgnoreCase(blocked) ) return;
                }
            }
        }
        ArmorType newArmorType = ArmorType.matchType(event.getItem());
        if ( newArmorType == null ) return;

        if
        (
                newArmorType == ArmorType.HELMET && isAirOrNull(event.getPlayer().getInventory().getHelmet())
                || newArmorType == ArmorType.CHESTPLATE && isAirOrNull(event.getPlayer().getInventory().getChestplate())
                || newArmorType == ArmorType.LEGGINGS && isAirOrNull(event.getPlayer().getInventory().getLeggings())
                || newArmorType == ArmorType.BOOTS && isAirOrNull(event.getPlayer().getInventory().getBoots())
        )
        {
            ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(event.getPlayer(), ArmorEquipEvent.EquipMethod.HOTBAR, ArmorType.matchType(event.getItem()), null, event.getItem());
            Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
            if ( armorEquipEvent.isCancelled() )
            {
                event.setCancelled(true);
                player.updateInventory();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void inventoryDrag(InventoryDragEvent event)
    {
        // getType() seems to always be even.
        // Old Cursor gives the item you are equipping
        // Raw slot is the ArmorType slot
        // Can't replace armor using this method making getCursor() useless.
        ArmorType type = ArmorType.matchType(event.getOldCursor());
        if ( event.getRawSlots().isEmpty() ) return;// Idk if this will ever happen
        if ( type != null && type.getSlot() == event.getRawSlots().stream().findFirst().orElse(0) )
        {
            ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) event.getWhoClicked(), ArmorEquipEvent.EquipMethod.DRAG, type, null, event.getOldCursor());
            Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
            if ( armorEquipEvent.isCancelled() )
            {
                event.setResult(Result.DENY);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void itemBreakEvent(PlayerItemBreakEvent event)
    {
        ArmorType type = ArmorType.matchType(event.getBrokenItem());
        if ( type == null ) return;

        Player player = event.getPlayer();
        ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(player, ArmorEquipEvent.EquipMethod.BROKE, type, event.getBrokenItem(), null);
        Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
        if ( armorEquipEvent.isCancelled() )
        {
            ItemStack item = event.getBrokenItem().clone();
            item.setAmount(1);
            item.setDurability((short) (item.getDurability() - 1));

            switch (type)
            {
                case HELMET:
                    player.getInventory().setHelmet(item);
                    break;
                case CHESTPLATE:
                    player.getInventory().setChestplate(item);
                    break;
                case LEGGINGS:
                    player.getInventory().setLeggings(item);
                    break;
                case BOOTS:
                    player.getInventory().setBoots(item);
                    break;
                default: break;
            }
        }
    }

    @EventHandler
    public void playerDeathEvent(PlayerDeathEvent event)
    {
        Player player = event.getEntity();
        if ( event.getKeepInventory() ) return;
        for (ItemStack item : player.getInventory().getArmorContents())
        {
            if ( ! isAirOrNull(item) )
            {
                Bukkit.getServer().getPluginManager().callEvent(new ArmorEquipEvent(player, ArmorEquipEvent.EquipMethod.DEATH, ArmorType.matchType(item), item, null));
            }
        }
    }

    /**
     * A utility method to support versions that use null or air ItemStacks.
     */
    public static boolean isAirOrNull(ItemStack item)
    {
        return item == null || item.getType() == Material.AIR;
    }

}