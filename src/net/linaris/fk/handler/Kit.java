package net.linaris.fk.handler;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public enum Kit {
    MINER(ChatColor.GOLD + "Mineur", Material.STONE_PICKAXE),
    BETTER_BOW(ChatColor.GOLD + "Arc am�lior�", Material.BOW),
    BETTER_SWORD(ChatColor.GOLD + "Ep�e am�lior�e", Material.STONE_SWORD),
    BETTER_ARMOR(ChatColor.GOLD + "Stuff am�lior�", Material.IRON_CHESTPLATE),
    MERLIN(ChatColor.GOLD + "Enchanteur", Material.EXP_BOTTLE);

    private static Map<Player, Kit> playerKits = new HashMap<>();

    @Getter
    private String name;
    @Getter
    private Material material;
    @Getter
    private short durability;

    public static Kit getPlayerKit(Player player) {
        return Kit.playerKits.get(player);
    }

    public static void setPlayerKit(Player player, Kit kit) {
        if (kit == null) {
            Kit.playerKits.remove(player);
        } else {
            Kit.playerKits.put(player, kit);
        }
    }

    private Kit(String name, Material material) {
        this(name, material, (short) 0);
    }

    private Kit(String name, Material material, short durability) {
        this.name = name;
        this.material = material;
        this.durability = durability;
    }
}
