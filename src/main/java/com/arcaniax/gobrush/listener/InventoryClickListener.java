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
package com.arcaniax.gobrush.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.inventory.InventoryClickEvent;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.MinecraftItemID;
import cn.nukkit.utils.TextFormat;
import com.arcaniax.gobrush.GoBrushPlugin;
import com.arcaniax.gobrush.Session;
import com.arcaniax.gobrush.enumeration.MainMenuSlot;
import com.arcaniax.gobrush.object.Brush;
import com.arcaniax.gobrush.object.BrushMenu;
import com.arcaniax.gobrush.object.BrushPlayer;
import com.arcaniax.gobrush.util.GuiGenerator;

/**
 * This class contains the listener that gets fired when an inventory is
 * clicked. There are multiple EventHandlers in this listener object.
 *
 * @author McJeffr
 */
public class InventoryClickListener implements Listener {

    private static final String PERMISSION_BYPASS_MAXSIZE = "gobrush.bypass.maxsize";
    private static final String PERMISSION_BYPASS_MAXINTENSITY = "gobrush.bypass.maxintensity";
    private static final String MAIN_MENU_INVENTORY_TITLE = "goBrush Menu";
    private static final String BRUSH_MENU_INVENTORY_TITLE = "goBrush Brushes";
    private static int amountOfValidBrushes;

    @EventHandler(priority = EventPriority.NORMAL)
    public void mainMenuClickEvent(InventoryClickEvent event) {
        if (isInvalidInventory(event, MAIN_MENU_INVENTORY_TITLE)) {
            return;
        }
        event.setCancelled(true);

        Player player = event.getPlayer();
        BrushPlayer brushPlayer = Session.getBrushPlayer(player.getUniqueId());
        int slot = event.getSlot();

        if (MainMenuSlot.MODE_DIRECTION.isValidSlot(slot)) {
            brushPlayer.toggleDirectionMode();
        } else if (MainMenuSlot.MODE_FLAT.isValidSlot(slot)) {
            brushPlayer.toggleFlatMode();
        } else if (MainMenuSlot.MODE_3D.isValidSlot(slot)) {
            brushPlayer.toggle3DMode();
        } else if (MainMenuSlot.FEATURE_AUTOROTATION.isValidSlot(slot)) {
            brushPlayer.toggleAutoRotation();
        } else if (MainMenuSlot.BRUSH_INTENSITY.isValidSlot(slot)) {
            int intensity = brushPlayer.getBrushIntensity() + 1;
            if (player.hasPermission(PERMISSION_BYPASS_MAXINTENSITY)) {
                brushPlayer.setBrushIntensity(intensity);
            } else if (intensity <= brushPlayer.getMaxBrushIntensity()) {
                brushPlayer.setBrushIntensity(intensity);
            }
        } else if (MainMenuSlot.BRUSH_SIZE.isValidSlot(slot)) {
            int size = brushPlayer.getBrushSize() + 2;
            if (player.hasPermission(PERMISSION_BYPASS_MAXSIZE)) {
                brushPlayer.setBrushSize(size);
                brushPlayer.getBrush().resize(size);
            } else if (size <= brushPlayer.getMaxBrushSize()) {
                brushPlayer.setBrushSize(size);
                brushPlayer.getBrush().resize(size);
            }
        } else if (MainMenuSlot.BRUSH_SELECTOR.isValidSlot(slot)) {
            if (event.getHeldItem() != null
                    && event.getHeldItem().getId() != Item.AIR
                    && event.getSourceItem() != null
                    && event.getSourceItem().getId() != Item.AIR
            ) {
                amountOfValidBrushes = GoBrushPlugin.amountOfValidBrushes;
                if (amountOfValidBrushes == 0) {
                    player.sendMessage("&bgoBrush> &cWARNING! The automatic brush installation failed because the server cannot connect to GitHub.");
                    player.sendMessage(TextFormat.AQUA + "goBrush> " + TextFormat.GOLD + "Download the default brushes manually here: https://github.com/Arcaniax-Development/goBrush-Assets/blob/main/brushes.zip?raw=true");
                    player.sendMessage(TextFormat.AQUA + "goBrush> " + TextFormat.RED + "Extract the zip into " + TextFormat.YELLOW + "/plugins/goBrush/brushes");
                } else {
                    Session.initializeBrushMenu();
                    player.addWindow(Session.getBrushMenu().getPage(0).getInventory());
                }
            } else {
                brushPlayer.toggleBrushEnabled();
            }
        }
        openMenu(player);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void brushMenuClickEvent(InventoryClickEvent event) {
        if (isInvalidInventory(event, BRUSH_MENU_INVENTORY_TITLE)) {
            return;
        }
        event.setCancelled(true);

        Player player = event.getPlayer();
        BrushPlayer brushPlayer = Session.getBrushPlayer(player.getUniqueId());
        BrushMenu brushMenu = Session.getBrushMenu();
        int slot = event.getSlot();
        int pageNumber = 0;
        for (int i = 0; i < Session.getBrushMenu().getAmountOfPages(); i++) {
            if (event.getInventory().equals(Session.getBrushMenu().getPage(i).getInventory())) {
                pageNumber = i;
            }
        }
        switch (slot) {
            case (45): {
                if (event.getInventory().getItem(slot).getId() == MinecraftItemID.ARROW.get(1).getId()) {
                    if (pageNumber == 0) {
                        GuiGenerator.openMenu(player, brushMenu.getPage(brushMenu.getAmountOfPages() - 1).getInventory());
                    } else {
                        GuiGenerator.openMenu(player, brushMenu.getPage(pageNumber - 1).getInventory());
                    }
                }

                break;
            }
            case (49): {
                openMenu(player);
                break;
            }
            case (53): {
                if (event.getInventory().getItem(slot).getId() == MinecraftItemID.ARROW.get(1).getId()) {
                    if (pageNumber == brushMenu.getAmountOfPages() - 1) {
                        GuiGenerator.openMenu(player, brushMenu.getPage(0).getInventory());
                    } else {
                        GuiGenerator.openMenu(player, brushMenu.getPage(pageNumber + 1).getInventory());
                    }
                }
                break;
            }
            default: {
                if (event.getInventory().getItem(slot) != null) {
                    if (event.getInventory().getItem(slot).getId() == MinecraftItemID.EMPTY_MAP.get(1).getId()) {
                        String name = TextFormat.clean(event.getInventory().getItem(slot).getCustomName());
                        int size = brushPlayer.getBrushSize();
                        Brush brush = Session.getBrush(name);
                        brushPlayer.setBrush(brush);
                        brushPlayer.getBrush().resize(size);
                        openMenu(player);
                    }
                }
            }
        }
    }

    /**
     * This method checks if an InventoryClickEvent is happening in a valid
     * goBrush menu.
     *
     * @param event The InventoryClickEvent that needs to be checked.
     * @return True if the event is happening in a goBrush menu, false
     *         otherwise.
     */
    private boolean isInvalidInventory(InventoryClickEvent event, String inventoryName) {
        final Inventory view = event.getInventory();
        final String title = view.getTitle();
        return title.contains(inventoryName);
    }

    /**
     * This method opens up the goBrush menu inventory. This method can be used
     * to refresh the inventory upon a change in configuration.
     *
     * @param player The player that needs to open the inventory again.
     */
    private void openMenu(Player player) {
        GuiGenerator.openMenu(player, GuiGenerator.generateMainMenu(Session.getBrushPlayer(player.getUniqueId())));
    }

}
