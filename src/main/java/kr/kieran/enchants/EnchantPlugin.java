package kr.kieran.enchants;

import kr.kieran.enchants.commands.CustomEnchantCommand;
import kr.kieran.enchants.listeners.ArmorEquipListeners;
import kr.kieran.enchants.listeners.EnchantApplyListeners;
import kr.kieran.enchants.listeners.EnchantFixListeners;
import kr.kieran.enchants.listeners.EnchantInventoryListeners;
import kr.kieran.enchants.listeners.armor.ArmorListener;
import kr.kieran.enchants.managers.EnchantManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class EnchantPlugin extends JavaPlugin
{

    private static EnchantPlugin instance;
    public static EnchantPlugin get() { return instance; }

    private EnchantManager enchantManager;

    @Override
    public void onLoad()
    {
        instance = this;
        this.saveDefaultConfig();
    }

    @Override
    public void onEnable()
    {
        this.registerManagers();
        this.registerCommands();
        this.registerListeners();
    }

    @Override
    public void onDisable()
    {
        enchantManager.disable();
        instance = null;
    }

    private void registerManagers()
    {
        this.enchantManager = new EnchantManager(this);
    }

    private void registerCommands()
    {
        this.getCommand("enchants").setExecutor(new CustomEnchantCommand(this));
    }

    private void registerListeners()
    {
        this.getServer().getPluginManager().registerEvents(new ArmorListener(Arrays.asList("FURNACE", "CHEST", "TRAPPED_CHEST", "BEACON", "DISPENSER", "DROPPER", "HOPPER", "WORKBENCH", "ENCHANTMENT_TABLE", "ENDER_CHEST", "ANVIL", "BED_BLOCK", "FENCE_GATE", "SPRUCE_FENCE_GATE", "ACACIA_FENCE_GATE", "JUNGLE_FENCE_GATE", "DARK_OAK_FENCE_GATE", "IRON_DOOR_BLOCK", "WOODEN_DOOR", "SPRUCE_DOOR", "BIRCH_DOOR", "JUNGLE_DOOR", "ACACIA_DOOR", "DARK_OAK_DOOR", "WOOD_BUTTON", "STONE_BUTTON", "TRAP_DOOR", "IRON_TRAPDOOR", "DIODE_BLOCK_OFF", "DIODE_BLOCK_ON", "REDSTONE_COMPARATOR_OFF", "REDSTONE_COMPARATOR_ON", "FENCE", "SPRUCE_FENCE", "BIRCH_FENCE", "JUNGLE_FENCE", "DARK_OAK_FENCE", "ACACIA_FENCE", "NETHER_FENCE", "BREWING_STAND", "CAULDRON", "LEGACY_SIGN_POST", "LEGACY_WALL_SIGN", "LEGACY_SIGN", "ACACIA_SIGN", "ACACIA_WALL_SIGN", "BIRCH_SIGN", "BIRCH_WALL_SIGN", "DARK_OAK_SIGN", "DARK_OAK_WALL_SIGN", "JUNGLE_SIGN", "JUNGLE_WALL_SIGN", "OAK_SIGN", "OAK_WALL_SIGN", "SPRUCE_SIGN", "SPRUCE_WALL_SIGN", "LEVER", "BLACK_SHULKER_BOX", "BROWN_SHULKER_BOX", "CYAN_SHULKER_BOX", "GRAY_SHULKER_BOX", "GREEN_SHULKER_BOX", "LIGHT_BLUE_SHULKER_BOX", "LIME_SHULKER_BOX", "MAGENTA_SHULKER_BOX", "ORANGE_SHULKER_BOX", "PINK_SHULKER_BOX", "PURPLE_SHULKER_BOX", "RED_SHULKER_BOX", "SILVER_SHULKER_BOX", "WHITE_SHULKER_BOX", "YELLOW_SHULKER_BOX", "DAYLIGHT_DETECTOR_INVERTED", "DAYLIGHT_DETECTOR", "BARREL", "BLAST_FURANCE", "SMOKER", "CARTOGRAPHY_TABLE", "COMPOSTER", "GRINDSTONE", "LECTERN", "LOOM", "STONECUTTER", "BELL")), this);
        this.getServer().getPluginManager().registerEvents(new ArmorEquipListeners(this), this);
        this.getServer().getPluginManager().registerEvents(new EnchantApplyListeners(this), this);
        this.getServer().getPluginManager().registerEvents(new EnchantInventoryListeners(this), this);
        this.getServer().getPluginManager().registerEvents(new EnchantFixListeners(this), this);
    }

    public EnchantManager getEnchantManager()
    {
        return enchantManager;
    }

}
