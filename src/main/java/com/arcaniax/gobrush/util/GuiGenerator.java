/*
 *                              ____                 _
 *                             |  _ \               | |
 *                   __ _  ___ | |_) |_ __ _   _ ___| |__
 *                  / _` |/ _ \|  _ <| '__| | | / __| '_ \
 *                 | (_| | (_) | |_) | |  | |_| \__ \ | | |
 *                  \__, |\___/|____/|_|   \__,_|___/_| |_|
 *                   __/ |
 *                  |___/
 *
 *    goBrush is designed to streamline and simplify your mountain building experience.
 *                            Copyright (C) 2021 Arcaniax
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.arcaniax.gobrush.util;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.MinecraftItemID;
import cn.nukkit.utils.TextFormat;
import com.arcaniax.gobrush.GoBrushPlugin;
import com.arcaniax.gobrush.Session;
import com.arcaniax.gobrush.enumeration.MainMenuSlot;
import com.arcaniax.gobrush.object.BrushPlayer;
import com.nukkitx.fakeinventories.inventory.ChestFakeInventory;
import com.nukkitx.fakeinventories.inventory.FakeInventory;

import java.util.Optional;

/**
 * This class contains a utility used for generating the goBrush main menu
 * inventory.
 *
 * @author Arcaniax, McJeffr
 */
public class GuiGenerator {

    private static final String MAIN_MENU_INVENTORY_TITLE = TextFormat.BLUE + "goBrush Menu";
    private static final Item GRAY_GLASS_PANE = createItem(MinecraftItemID.GLASS_PANE.get(1), 7, TextFormat.GOLD.toString(), "");
    private static final Item GREEN_GLASS_PANE = createItem(MinecraftItemID.GLASS_PANE.get(1), 13, TextFormat.GOLD.toString(), "");
    private static final Item ORANGE_GLASS_PANE = createItem(MinecraftItemID.GLASS_PANE.get(1), 1, TextFormat.GOLD.toString(), "");
    private static final Item RED_GLASS_PANE = createItem(MinecraftItemID.GLASS_PANE.get(1), 14, TextFormat.GOLD.toString(), "");
    private static final Item WHITE_GLASS_PANE = createItem(MinecraftItemID.GLASS_PANE.get(1), 0, TextFormat.GOLD.toString(), "");

    /**
     * This method generates the goBrush main menu based on the BrushPlayer's
     * settings.
     *
     * @param brushPlayer The BrushPlayer that this main menu belongs to.
     * @return The generated goBrush main menu inventory.
     */
    public static FakeInventory generateMainMenu(BrushPlayer brushPlayer) {
        ChestFakeInventory mainMenu = new ChestFakeInventory();
        mainMenu.setTitle(MAIN_MENU_INVENTORY_TITLE);
        for (int i = 0; i < 27; i++) {
            mainMenu.setItem(i, GRAY_GLASS_PANE);
        }

        mainMenu.setItem(
                11,
                createItem(MinecraftItemID.BROWN_MUSHROOM.get(1),
                        0,
                        "&6Size: &e" + brushPlayer.getBrushSize(),
                        "&63D Size: &e" + (double) brushPlayer.getBrushSize() / 4.0 + "___&3___&7Left click to increase&3___&7Right click to decrease___&7Shift click to change by 10"
                )
        );
        mainMenu.setItem(
                12,
                createItem(MinecraftItemID.BLAZE_POWDER.get(1),
                        0,
                        "&6Intensity: &e" + brushPlayer.getBrushIntensity(),
                        "&3___&7Left click to increase&3___&7Right click to decrease"
                )
        );
        if (brushPlayer.getBrushSize() > brushPlayer.getMaxBrushSize()) {
            mainMenu.setItem(2, ORANGE_GLASS_PANE);
            mainMenu.setItem(20, ORANGE_GLASS_PANE);
        } else {
            mainMenu.setItem(2, WHITE_GLASS_PANE);
            mainMenu.setItem(20, WHITE_GLASS_PANE);
        }
        if (brushPlayer.getBrushIntensity() > brushPlayer.getMaxBrushIntensity()) {
            mainMenu.setItem(3, ORANGE_GLASS_PANE);
            mainMenu.setItem(21, ORANGE_GLASS_PANE);
        } else {
            mainMenu.setItem(3, WHITE_GLASS_PANE);
            mainMenu.setItem(21, WHITE_GLASS_PANE);
        }
        if (brushPlayer.isBrushEnabled()) {
            mainMenu.setItem(
                    10,
                    createItem(MinecraftItemID.WRITABLE_BOOK.get(1),
                            (short) 0,
                            "&6Selected Brush: &e" + brushPlayer.getBrush().getName(),
                            "&a&lEnabled___&7___&7Left click to change brush___&7Right click to toggle"
                    )
            );
            mainMenu.setItem(1, GREEN_GLASS_PANE);
            mainMenu.setItem(19, GREEN_GLASS_PANE);
        } else {
            mainMenu.setItem(
                    10,
                    createItem(MinecraftItemID.WRITABLE_BOOK.get(1),
                            (short) 0,
                            "&6Selected Brush: &e" + brushPlayer.getBrush().getName(),
                            "&c&lDisabled___&7___&7Left click to change brush___&7Right click to toggle"
                    )
            );
            mainMenu.setItem(1, RED_GLASS_PANE);
            mainMenu.setItem(19, RED_GLASS_PANE);
        }
        if (brushPlayer.isDirectionMode()) {
            mainMenu.setItem(13, MinecraftItemID.SKULL.get(1).setCustomName("&6Pull Mode").setLore("&7Click to change"));
            mainMenu.setItem(4, ORANGE_GLASS_PANE);
            mainMenu.setItem(22, ORANGE_GLASS_PANE);
        } else {
            mainMenu.setItem(13, MinecraftItemID.SKULL.get(1).setCustomName("&6Push Mode").setLore("&7Click to change"));
            mainMenu.setItem(4, ORANGE_GLASS_PANE);
            mainMenu.setItem(22, ORANGE_GLASS_PANE);
        }
        if (brushPlayer.is3DMode()) {
            mainMenu.setItem(14, MinecraftItemID.SKULL.get(1).setCustomName("&63D Mode").setLore("&a&lEnabled___&7___&7Click to toggle"));
            mainMenu.setItem(5, GREEN_GLASS_PANE);
            mainMenu.setItem(23, GREEN_GLASS_PANE);
        } else {
            mainMenu.setItem(14, MinecraftItemID.SKULL.get(1).setCustomName("&63D Mode").setLore("&c&lDisabled___&7___&7Click to toggle"));
            mainMenu.setItem(5, RED_GLASS_PANE);
            mainMenu.setItem(23, RED_GLASS_PANE);
        }
        if (brushPlayer.isFlatMode()) {
            mainMenu.setItem(
                    15,
                    createItem(MinecraftItemID.HEAVY_WEIGHTED_PRESSURE_PLATE.get(1),
                            (short) 0,
                            "&6Flat Mode",
                            "&a&lEnabled___&7___&7Click to toggle"
                    )
            );
            mainMenu.setItem(6, GREEN_GLASS_PANE);
            mainMenu.setItem(24, GREEN_GLASS_PANE);
        } else {
            mainMenu.setItem(
                    15,
                    createItem(MinecraftItemID.HEAVY_WEIGHTED_PRESSURE_PLATE.get(1),
                            0,
                            "&6Flat Mode",
                            "&c&lDisabled___&7___&7Click to toggle"
                    )
            );
            mainMenu.setItem(6, RED_GLASS_PANE);
            mainMenu.setItem(24, RED_GLASS_PANE);
        }

        if (brushPlayer.isAutoRotation()) {
            mainMenu.setItem(
                    16,
                    createItem(MinecraftItemID.COMPASS.get(1),
                            0,
                            "&6Auto Rotation",
                            "&a&lEnabled___&7___&7Click to toggle"
                    )
            );
            mainMenu.setItem(7, GREEN_GLASS_PANE);
            mainMenu.setItem(25, GREEN_GLASS_PANE);
        } else {
            mainMenu.setItem(
                    16,
                    createItem(MinecraftItemID.COMPASS.get(1),
                            0,
                            "&6Auto Rotation",
                            "&c&lDisabled___&7___&7Click to toggle"
                    )
            );
            mainMenu.setItem(7, RED_GLASS_PANE);
            mainMenu.setItem(25, RED_GLASS_PANE);
        }
        return mainMenu;
    }

    private static Item createItem(Item item, int damage, String name, String... lore) {
        item.setCustomName(name).setLore(lore);
        item.setDamage(damage);
        return item;
    }

    public static void openMenu(Player player, Inventory inventory) {
        Optional<Inventory> inv = player.getTopWindow();
        if (inv.isPresent() && inv.get() instanceof FakeInventory) {
            player.removeWindow(inv.get());
        }

        Server.getInstance().getScheduler().scheduleDelayedTask(GoBrushPlugin.getPlugin(), () -> player.addWindow(inventory), 20, true);
    }
}
