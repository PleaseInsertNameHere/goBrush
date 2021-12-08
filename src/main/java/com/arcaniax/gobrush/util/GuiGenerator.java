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
    private static final Item GRAY_GLASS_PANE = createItem(MinecraftItemID.STAINED_GLASS_PANE.get(1), 7, TextFormat.GOLD.toString(), "");
    private static final Item GREEN_GLASS_PANE = createItem(MinecraftItemID.STAINED_GLASS_PANE.get(1), 13, TextFormat.GOLD.toString(), "");
    private static final Item ORANGE_GLASS_PANE = createItem(MinecraftItemID.STAINED_GLASS_PANE.get(1), 1, TextFormat.GOLD.toString(), "");
    private static final Item RED_GLASS_PANE = createItem(MinecraftItemID.STAINED_GLASS_PANE.get(1), 14, TextFormat.GOLD.toString(), "");
    private static final Item WHITE_GLASS_PANE = createItem(MinecraftItemID.STAINED_GLASS_PANE.get(1), 0, TextFormat.GOLD.toString(), "");

    private static final String PERMISSION_BYPASS_MAXSIZE = "gobrush.bypass.maxsize";
    private static final String PERMISSION_BYPASS_MAXINTENSITY = "gobrush.bypass.maxintensity";

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
                        "" + TextFormat.RESET + TextFormat.GOLD + "Size: " + TextFormat.YELLOW + brushPlayer.getBrushSize(),
                        "" + TextFormat.RESET + TextFormat.GOLD + "3D Size: " + TextFormat.YELLOW + (double) brushPlayer.getBrushSize() / 4.0 + "\n\n" + TextFormat.GRAY + "Left click to increase\nRight click to decrease\nShift click to change by 10"
                )
        );
        mainMenu.setItem(
                12,
                createItem(MinecraftItemID.BLAZE_POWDER.get(1),
                        0,
                        "" + TextFormat.RESET + TextFormat.GOLD + "Intensity: " + TextFormat.YELLOW + brushPlayer.getBrushIntensity(),
                        "" + TextFormat.RESET + TextFormat.GRAY + "Left click to increase\nRight click to decrease"
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
                            0,
                            "" + TextFormat.RESET + TextFormat.GOLD + "Selected Brush: " + TextFormat.YELLOW + brushPlayer.getBrush().getName(),
                            "" + TextFormat.RESET + TextFormat.GREEN + TextFormat.BOLD + "Enabled\n\n" + TextFormat.RESET + TextFormat.GRAY + "Left click to change brush\nRight click to toggle"
                    )
            );
            mainMenu.setItem(1, GREEN_GLASS_PANE);
            mainMenu.setItem(19, GREEN_GLASS_PANE);
        } else {
            mainMenu.setItem(
                    10,
                    createItem(MinecraftItemID.WRITABLE_BOOK.get(1),
                            0,
                            "" + TextFormat.RESET + TextFormat.GOLD + "Selected Brush: " + TextFormat.YELLOW + brushPlayer.getBrush().getName(),
                            "" + TextFormat.RESET + TextFormat.RED + TextFormat.BOLD + "Disabled\n\n" + TextFormat.RESET + TextFormat.GRAY + "Left click to change brush\nRight click to toggle"
                    )
            );
            mainMenu.setItem(1, RED_GLASS_PANE);
            mainMenu.setItem(19, RED_GLASS_PANE);
        }
        if (brushPlayer.isDirectionMode()) {
            mainMenu.setItem(13, MinecraftItemID.SKULL.get(1).setCustomName("" + TextFormat.RESET + TextFormat.GOLD + "Pull Mode").setLore("" + TextFormat.RESET + TextFormat.GRAY + "Click to change"));
            mainMenu.setItem(4, ORANGE_GLASS_PANE);
            mainMenu.setItem(22, ORANGE_GLASS_PANE);
        } else {
            mainMenu.setItem(13, MinecraftItemID.SKULL.get(1).setCustomName("" + TextFormat.RESET + TextFormat.GOLD + "Push Mode").setLore("" + TextFormat.RESET + TextFormat.GRAY + "Click to change"));
            mainMenu.setItem(4, ORANGE_GLASS_PANE);
            mainMenu.setItem(22, ORANGE_GLASS_PANE);
        }
        if (brushPlayer.is3DMode()) {
            mainMenu.setItem(14, MinecraftItemID.SKULL.get(1).setCustomName("" + TextFormat.RESET + TextFormat.GOLD + "3D Mode").setLore("" + TextFormat.RESET + TextFormat.GREEN + TextFormat.BOLD + "Enabled\n\n" + TextFormat.RESET + TextFormat.GRAY + "Click to toggle"));
            mainMenu.setItem(5, GREEN_GLASS_PANE);
            mainMenu.setItem(23, GREEN_GLASS_PANE);
        } else {
            mainMenu.setItem(14, MinecraftItemID.SKULL.get(1).setCustomName("" + TextFormat.RESET + TextFormat.GOLD + "3D Mode").setLore("" + TextFormat.RESET + TextFormat.RED + TextFormat.BOLD + "Disabled\n\n" + TextFormat.RESET + TextFormat.GRAY + "Click to toggle"));
            mainMenu.setItem(5, RED_GLASS_PANE);
            mainMenu.setItem(23, RED_GLASS_PANE);
        }
        if (brushPlayer.isFlatMode()) {
            mainMenu.setItem(
                    15,
                    createItem(MinecraftItemID.HEAVY_WEIGHTED_PRESSURE_PLATE.get(1),
                            (short) 0,
                            "" + TextFormat.RESET + TextFormat.GOLD + "Flat Mode",
                            "" + TextFormat.RESET + TextFormat.GREEN + TextFormat.BOLD + "Enabled\n\n" + TextFormat.RESET + TextFormat.GRAY + "Click to toggle"
                    )
            );
            mainMenu.setItem(6, GREEN_GLASS_PANE);
            mainMenu.setItem(24, GREEN_GLASS_PANE);
        } else {
            mainMenu.setItem(
                    15,
                    createItem(MinecraftItemID.HEAVY_WEIGHTED_PRESSURE_PLATE.get(1),
                            0,
                            "" + TextFormat.RESET + TextFormat.GOLD + "Flat Mode",
                            "" + TextFormat.RESET + TextFormat.RED + TextFormat.BOLD + "Disabled\n\n" + TextFormat.RESET + TextFormat.GRAY + "Click to toggle"
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
                            "" + TextFormat.RESET + TextFormat.GOLD + "Auto Rotation",
                            "" + TextFormat.RESET + TextFormat.GREEN + TextFormat.BOLD + "Enabled\n\n" + TextFormat.RESET + TextFormat.GRAY + "Click to toggle"
                    )
            );
            mainMenu.setItem(7, GREEN_GLASS_PANE);
            mainMenu.setItem(25, GREEN_GLASS_PANE);
        } else {
            mainMenu.setItem(
                    16,
                    createItem(MinecraftItemID.COMPASS.get(1),
                            0,
                            "" + TextFormat.RESET + TextFormat.GOLD + "Auto Rotation",
                            "" + TextFormat.RESET + TextFormat.RED + TextFormat.BOLD + "Disabled\n\n" + TextFormat.RESET + TextFormat.GRAY + "Click to toggle"
                    )
            );
            mainMenu.setItem(7, RED_GLASS_PANE);
            mainMenu.setItem(25, RED_GLASS_PANE);
        }
        mainMenu.addListener(event -> {
            event.setCancelled(true);

            Player player = event.getPlayer();
            BrushPlayer brushPlayer1 = Session.getBrushPlayer(player.getUniqueId());
            int slot = event.getAction().getSlot();

            if (MainMenuSlot.MODE_DIRECTION.isValidSlot(slot)) {
                mainMenu.setItem(slot, MinecraftItemID.SKULL.get(1).setCustomName("" + TextFormat.RESET + TextFormat.GOLD + (brushPlayer1.toggleDirectionMode() ? "Pull" : "Push") + " Mode").setLore("" + TextFormat.RESET + TextFormat.GRAY + "Click to change"));
            } else if (MainMenuSlot.MODE_FLAT.isValidSlot(slot)) {
                event.getInventory().setItem(
                        slot,
                        createItem(MinecraftItemID.HEAVY_WEIGHTED_PRESSURE_PLATE.get(1),
                                0,
                                "" + TextFormat.RESET + TextFormat.GOLD + "Flat Mode",
                                "" + TextFormat.RESET + (brushPlayer1.toggleFlatMode() ? "" + TextFormat.GREEN + TextFormat.BOLD + "Enabled" : "" + TextFormat.RED + TextFormat.BOLD + "Disabled") + "\n\n" + TextFormat.RESET + TextFormat.GRAY + "Click to toggle"
                        )
                );
            } else if (MainMenuSlot.MODE_3D.isValidSlot(slot)) {
                event.getInventory().setItem(slot, MinecraftItemID.SKULL.get(1).setCustomName("" + TextFormat.RESET + TextFormat.GOLD + "3D Mode").setLore("" + TextFormat.RESET + (brushPlayer1.toggle3DMode() ? "" + TextFormat.GREEN + TextFormat.BOLD + "Enabled" : "" + TextFormat.RED + TextFormat.BOLD + "Disabled") + "\n\n" + TextFormat.RESET + TextFormat.GRAY + "Click to toggle"));
            } else if (MainMenuSlot.FEATURE_AUTOROTATION.isValidSlot(slot)) {
                event.getInventory().setItem(
                        slot,
                        createItem(MinecraftItemID.COMPASS.get(1),
                                0,
                                "" + TextFormat.RESET + TextFormat.GOLD + "Auto Rotation",
                                "" + TextFormat.RESET + (brushPlayer1.toggleAutoRotation() ? "" + TextFormat.GREEN + TextFormat.BOLD + "Enabled" : "" + TextFormat.RED + TextFormat.BOLD + "Disabled") + "\n\n" + TextFormat.RESET + TextFormat.GRAY + "Click to toggle"
                ));
            } else if (MainMenuSlot.BRUSH_INTENSITY.isValidSlot(slot)) {
                int intensity = brushPlayer1.getBrushIntensity() + 1;
                if (player.hasPermission(PERMISSION_BYPASS_MAXINTENSITY) || intensity <= brushPlayer1.getMaxBrushIntensity()) {
                    event.getInventory().setItem(
                            slot,
                            createItem(MinecraftItemID.BLAZE_POWDER.get(1),
                                    0,
                                    "" + TextFormat.RESET + TextFormat.GOLD + "Intensity: " + TextFormat.YELLOW + brushPlayer1.setBrushIntensity(intensity),
                                    "" + TextFormat.RESET + "\n" + TextFormat.GRAY + "Left click to increase\nRight click to decrease"
                            )
                    );
                }
            } else if (MainMenuSlot.BRUSH_SIZE.isValidSlot(slot)) {
                int size = brushPlayer1.getBrushSize() + 2;
                if (player.hasPermission(PERMISSION_BYPASS_MAXSIZE) || size <= brushPlayer1.getMaxBrushSize()) {
                    brushPlayer1.setBrushSize(size);
                    brushPlayer1.getBrush().resize(size);
                    event.getInventory().setItem(
                            slot,
                            createItem(MinecraftItemID.BROWN_MUSHROOM.get(1),
                                    0,
                                    "" + TextFormat.RESET + TextFormat.GOLD + "Size: " + TextFormat.YELLOW + size,
                                    "" + TextFormat.RESET + TextFormat.GOLD + "3D Size: " + TextFormat.YELLOW + (double) size / 4.0 + "\n\n" + TextFormat.GRAY + "Left click to increase\nRight click to decrease\nShift click to change by 10"
                            )
                    );
                }
            } else if (MainMenuSlot.BRUSH_SELECTOR.isValidSlot(slot)) {
                if (event.getAction().getSourceItem() != null
                        && event.getAction().getSourceItem().getId() != Item.AIR
                        && event.getAction().getTargetItem() != null
                        && event.getAction().getTargetItem().getId() != Item.AIR
                ) {
                    int amountOfValidBrushes = GoBrushPlugin.amountOfValidBrushes;
                    if (amountOfValidBrushes == 0) {
                        player.sendMessage(TextFormat.AQUA + "goBrush> " + TextFormat.RED + "WARNING! The automatic brush installation failed because the server cannot connect to GitHub.");
                        player.sendMessage(TextFormat.AQUA + "goBrush> " + TextFormat.GOLD + "Download the default brushes manually here: https://github.com/Arcaniax-Development/goBrush-Assets/blob/main/brushes.zip?raw=true");
                        player.sendMessage(TextFormat.AQUA + "goBrush> " + TextFormat.RED + "Extract the zip into " + TextFormat.YELLOW + "/plugins/goBrush/brushes");
                    } else {
                        Session.initializeBrushMenu();
                        GuiGenerator.closeInventory(player, event.getInventory());
                        Server.getInstance().getScheduler().scheduleDelayedTask(GoBrushPlugin.getPlugin(), () -> player.addWindow(Session.getBrushMenu().getPage(0).getInventory()), 20, true);
                    }
                } else {
                    event.getInventory().setItem(
                            slot,
                            createItem(MinecraftItemID.WRITABLE_BOOK.get(1),
                                    0,
                                    "" + TextFormat.RESET + TextFormat.GOLD + "Selected Brush: " + TextFormat.YELLOW + brushPlayer.getBrush().getName(),
                                    "" + TextFormat.RESET + (brushPlayer1.toggleBrushEnabled() ? "" + TextFormat.GREEN + TextFormat.BOLD + "Enabled" : "" + TextFormat.RED + TextFormat.BOLD + "Disabled") + "\n\n" + TextFormat.RESET + TextFormat.GRAY + "Left click to change brush\nRight click to toggle"
                            )
                    );
                }
            }
        });
        return mainMenu;
    }

    private static Item createItem(Item item, int damage, String name, String... lore) {
        item.setCustomName(name).setLore(lore);
        item.setDamage(damage);
        return item;
    }

    public static void openMenu(Player player, Inventory inventory) {
        player.addWindow(inventory);
    }

    public static void closeInventory(Player player, Inventory inventory) {
        player.removeWindow(inventory);
    }
}
