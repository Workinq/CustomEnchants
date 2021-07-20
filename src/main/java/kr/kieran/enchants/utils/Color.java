package kr.kieran.enchants.utils;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class Color
{

    public static String color(String text)
    {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static List<String> color(List<String> uncolored)
    {
        List<String> colored = new ArrayList<>();
        for (String text : uncolored)
        {
            colored.add(color(text));
        }
        return colored;
    }

}
