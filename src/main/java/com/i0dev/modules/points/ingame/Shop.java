package com.i0dev.modules.points.ingame;

import com.i0dev.modules.points.Option;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.util.FormatUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Shop {

    private static final String MAX_BUY_AMOUNT = Configuration.getString("modules.points.parts.buy.message.maxBuyAmount");
    private static final List<JSONObject> OPTIONS = Configuration.getObjectList("pointShop.options");
    private static final long MAX_BUY_AMOUNT_LONG = Configuration.getLong("modules.points.parts.buy.option.maxBuyAmount");
    private static final List<String> BOTTOM_LORE = Configuration.getStringList("pointShop.general.ingameBottomLore");

    @Setter
    @Getter
    public static Inventory shopGUI;


    public static void run(CommandSender sender, String[] args) {
        if (!PointsManager.hasPermission(sender, "discordbot.points.shop")) {
            sender.sendMessage(FormatUtil.c("&cInsufficient Permission"));
            return;
        }
        loadInventory();

        if (args.length != 1) {
            sender.sendMessage("Usage: /points shop");
            return;
        }

        ((Player) sender).openInventory(getShopGUI());
    }

    public static void loadInventory() {
        setShopGUI(Bukkit.getServer().createInventory(null, PointsManager.getInventorySize(OPTIONS.size()), FormatUtil.c("&c&lDiscord Points Shop")));
        for (int i = 0; i < OPTIONS.size(); i++) {
            Option option = com.i0dev.modules.points.discord.PointsManager.makeObject(OPTIONS.get(i), true);

            ItemStack itemStack = new ItemStack(Material.valueOf(option.getItemMaterial()), (int) option.getItemAmount(), ((short) option.getItemData()));
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(FormatUtil.c(option.getIngameDisplayName()));
            List<String> formattedLore = new ArrayList<>();
            for (String s : option.getIngameDescription()) {
                formattedLore.add(FormatUtil.c(s));
            }
            for (String s : BOTTOM_LORE) {
                formattedLore.add(FormatUtil.c(s.replace("{price}", option.getPrice() + "")));
            }
            itemMeta.setLore(formattedLore);
            if (option.isGlow()) {
                itemMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
                itemMeta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
            }
            itemStack.setItemMeta(itemMeta);
            getShopGUI().setItem(i, itemStack);
        }
        for (int i = 0; i < shopGUI.getSize(); i++) {
            if (shopGUI.getItem(i) == null || shopGUI.getItem(i).getType().equals(Material.AIR)) {
                ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, ((short) 7));
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(FormatUtil.c("&0"));
                itemMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
                itemMeta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
                itemStack.setItemMeta(itemMeta);

                shopGUI.setItem(i, itemStack);
            }
        }

    }

}
