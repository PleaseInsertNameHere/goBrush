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
import cn.nukkit.level.Level;
import com.arcaniax.gobrush.GoBrushPlugin;
import com.boydti.fawe.nukkit.core.NukkitWorldEdit;
import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.regions.Region;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class HeightMapExporter {

    boolean worldEditSelection;
    int minX;
    int minZ;
    int maxX;
    int maxZ;
    Level w;
    Player p;

    public HeightMapExporter(Player p) throws IncompleteRegionException {
        Region selection = NukkitWorldEdit.inst().getSession(p).getWorldSelection();
        if (selection != null) {
            worldEditSelection = true;
            minX = selection.getMinimumPoint().getBlockX();
            minZ = selection.getMinimumPoint().getBlockZ();
            maxX = selection.getMaximumPoint().getBlockX();
            maxZ = selection.getMaximumPoint().getBlockZ();
            w = p.getLevel();
            this.p = p;
        } else {
            worldEditSelection = false;
        }
    }

    public boolean hasWorldEditSelection() {
        return worldEditSelection;
    }


    public void exportImage(int size, String name) {
        String prefix = "§bgoBrush> ";
        int AdjustX = 0;
        int AdjustZ = 0;
        int blockSize = maxX - minX;
        if (maxZ - minZ > blockSize) {
            blockSize = maxZ - minZ;
            AdjustX = (blockSize - (maxX - minX)) / 2;
        } else {
            AdjustZ = (blockSize - (maxZ - minZ)) / 2;
        }
        if (blockSize < 20) {
            p.sendMessage(prefix + "§cPlease make a bigger selection");
            return;
        }
        int highest = 0;
        int lowest = 254;
        for (int x = 0; x < (maxX - minX); x++) {
            for (int z = 0; z < (maxZ - minZ); z++) {
                EditSession editSession = new EditSessionBuilder(this.w.getName()).fastmode(true).build();
                int y = editSession.getHighestTerrainBlock(x + minX, z + minZ, 0, 255);
                if (y > highest) {
                    highest = y;
                }
                if (y < lowest) {
                    lowest = y;
                }
            }
        }
        int height = highest - lowest;
        if (height < 10) {
            p.sendMessage(prefix + "§cPlease select a bigger mountain");
            return;
        }
        BufferedImage img = new BufferedImage(blockSize, blockSize, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < (maxX - minX); x++) {
            for (int z = 0; z < (maxZ - minZ); z++) {
                EditSession editSession = new EditSessionBuilder(this.w.getName()).fastmode(true).build();
                int y = editSession.getHighestTerrainBlock(x + minX, z + minZ, 0, 255);
                int i = (int) (((double) (y - lowest) / (double) height) * (double) 255);
                int rgb = i; //red
                rgb = (rgb << 8) + i;//green
                rgb = (rgb << 8) + i;//blue
                img.setRGB(x + AdjustX, z + AdjustZ, rgb);
            }
        }

        img = resize(img, 500);
        File f = new File(GoBrushPlugin.getPlugin().getDataFolder() + "/brushes/" + name + ".png");
        try {
            ImageIO.write(img, "PNG", f);
        } catch (IOException ignore) {}
    }

    private BufferedImage resize(BufferedImage bi, int size) {
        BufferedImage pattern = new BufferedImage(size, size, 1);
        Graphics2D graphic = pattern.createGraphics();
        graphic.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphic.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphic.drawImage(bi, 0, 0, size, size, null);
        graphic.dispose();
        return pattern;
    }

}
