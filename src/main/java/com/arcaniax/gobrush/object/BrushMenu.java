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
package com.arcaniax.gobrush.object;

import cn.nukkit.Player;
import cn.nukkit.item.MinecraftItemID;
import cn.nukkit.utils.TextFormat;
import com.arcaniax.gobrush.Session;
import com.arcaniax.gobrush.util.GuiGenerator;
import com.nukkitx.fakeinventories.inventory.FakeInventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class contains the object of BrushMenu. This object is the menu of
 * brushes, made up of BrushPage objects.
 *
 * @author McJeffr
 */
public class BrushMenu {

    /* Attributes */
    private final List<BrushPage> PAGES;
    private final int AMOUNT_OF_PAGES;

    /**
     * Constructor of a BrushMenu object. This constructor makes a menu of
     * brushes that contains BrushPage objects which are the individual pages of
     * brushes.
     *
     * @param brushes A List containing the brushes that need to be added.
     */
    @SuppressWarnings("unchecked")
    public BrushMenu(List<Brush> brushes) {
        this.PAGES = new ArrayList<>();
        Collections.sort(brushes);
        this.AMOUNT_OF_PAGES = (int) Math.ceil((brushes.size() + 1) / 45.0);
        for (int i = 0; i < this.AMOUNT_OF_PAGES; i++) {
            int start = (i * 45);
            int end = ((i + 1) * 45);
            BrushPage page;
            if (brushes.size() >= end) {
                List<Brush> subList = brushes.subList(start, end);
                page = new BrushPage(subList, i, AMOUNT_OF_PAGES);
            } else {
                List<Brush> subList = brushes.subList(start, brushes.size());
                page = new BrushPage(subList, i, AMOUNT_OF_PAGES);
            }
            this.PAGES.add(page);
        }
    }

    /**
     * This method updates contents of old BrushPage
     *
     * @param old The old inventory to update contents of
     * @param page The new page to take the contents of
     */
    public void update(FakeInventory old, BrushPage page) {
        old.setContents(page.getInventory().getContents());
        page.getInventory().removeListener(page.getListener());
        page.getInventory().addListener(event -> {
            event.setCancelled(true);

            Player player = event.getPlayer();
            BrushPlayer brushPlayer = Session.getBrushPlayer(player.getUniqueId());
            BrushMenu brushMenu = Session.getBrushMenu();
            int slot = event.getAction().getSlot();
            int pageNum = page.getPageNumber();
            switch (slot) {
                case (45): {
                    brushMenu.update(event.getInventory(), brushMenu.getPage(pageNum == 0 ? brushMenu.getAmountOfPages() - 1 : pageNum - 1));
                    break;
                }
                case (49): {
                    GuiGenerator.closeInventory(player);
                    break;
                }
                case (53): {
                    brushMenu.update(event.getInventory(), brushMenu.getPage(pageNum == (brushMenu.getAmountOfPages() - 1) ? 0 : pageNum + 1));
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
                            GuiGenerator.closeInventory(player);
                        }
                    }
                }
            }
        });
    }

    /**
     * This method gets a BrushPage object at the given index.
     *
     * @param index The index of the BrushPage that needs to be retrieved.
     * @return The BrushPage if the index exists, null otherwise.
     */
    public BrushPage getPage(int index) {
        if (index < this.PAGES.size()) {
            return this.PAGES.get(index);
        } else {
            return null;
        }
    }

    /**
     * Getter for the amount of pages the BrushMenu contains.
     *
     * @return The amount of pages the BrushMenu contains.
     */
    public int getAmountOfPages() {
        return AMOUNT_OF_PAGES;
    }

}
