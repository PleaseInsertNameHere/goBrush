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
package com.arcaniax.gobrush.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import com.arcaniax.gobrush.GoBrushPlugin;
import com.arcaniax.gobrush.Session;
import com.arcaniax.gobrush.object.Brush;
import com.arcaniax.gobrush.object.BrushPlayer;
import com.arcaniax.gobrush.object.HeightMapExporter;

public class CommandHandler extends Command {
    private static final String prefix = TextFormat.AQUA + "goBrush> ";

    public CommandHandler() {
        super ("gobrush", "goBrush", "/goBrush", new String[]{"gb"});
        this.setPermission("gobrush.use");
        this.setPermissionMessage(prefix + TextFormat.RED + "You are lacking the permission gobrush.use");
    }


    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player) || !this.testPermission(sender)) {
            return false;
        }
        final Player p = (Player) sender;
        BrushPlayer bp = Session.getBrushPlayer(p.getUniqueId());
        final String adminUsage = prefix + TextFormat.RED + "/gb size" + TextFormat.GRAY + "|" + TextFormat.RED + "intensity" + TextFormat.GRAY + "|" + TextFormat.RED + "brush" + TextFormat.GRAY + "|" + TextFormat.RED + "toggle" + TextFormat.GRAY + "|" + TextFormat.RED + "reload" + TextFormat.GRAY + "|" + TextFormat.RED + "export" + TextFormat.GRAY + "|" + TextFormat.RED + "info ";
        final String exportUsage = prefix + TextFormat.RED + "/gb size" + TextFormat.GRAY + "|" + TextFormat.RED + "intensity" + TextFormat.GRAY + "|" + TextFormat.RED + "brush" + TextFormat.GRAY + "|" + TextFormat.RED + "toggle" + TextFormat.GRAY + "|" + TextFormat.RED + "export" + TextFormat.GRAY + "|" + TextFormat.RED + "info ";
        if (args.length == 0) {
            if (p.hasPermission("gobrush.admin")) {
                p.sendMessage(adminUsage);
                return true;
            } else if (p.hasPermission("gobrush.export")) {
                p.sendMessage(exportUsage);
                return true;
            }
            p.sendMessage(exportUsage);
            return true;
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("size") || args[0].equalsIgnoreCase("s")) {
                p.sendMessage(prefix + TextFormat.RED + "/gb size [number]");
                return true;
            } else if (args[0].equalsIgnoreCase("intensity") || args[0].equalsIgnoreCase("i")) {
                p.sendMessage(prefix + TextFormat.RED + "/gb intensity [number]");
                return true;
            } else if (args[0].equalsIgnoreCase("brush") || args[0].equalsIgnoreCase("b")) {
                p.sendMessage(prefix + TextFormat.RED + "/gb brush [fileName]");
                return true;
            } else if ((args[0].equalsIgnoreCase("export") || args[0].equalsIgnoreCase("e")) && p.hasPermission(
                    "gobrush.export")) {
                p.sendMessage(prefix + TextFormat.RED + "/gb export [fileName]");
                return true;
            } else if (args[0].equalsIgnoreCase("toggle") || args[0].equalsIgnoreCase("t")) {

                if (bp.isBrushEnabled()) {
                    bp.toggleBrushEnabled();
                    p.sendMessage(prefix + TextFormat.RED + "Disabled brush");
                } else {
                    bp.toggleBrushEnabled();
                    p.sendMessage(prefix + TextFormat.GREEN + "Enabled brush");
                }
                return true;
            } else if ((args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("r")) && p.hasPermission(
                    "gobrush.admin")) {
                GoBrushPlugin.getPlugin().reloadConfig();
                Session.getConfig().reload(GoBrushPlugin.getPlugin().getConfig());
                int amountOfValidBrushes = Session.initializeValidBrushes();
                GoBrushPlugin.getPlugin().getLogger().info("Registered " + amountOfValidBrushes + " brushes.");
                Session.initializeBrushMenu();
                Session.initializeBrushPlayers();
                p.sendMessage(prefix + TextFormat.GREEN + "Reload Successful");
                return true;
            } else if (args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("i")) {
                p.sendMessage(prefix + TextFormat.GOLD + "Created by: " +  TextFormat.YELLOW + "Arcaniax (https://twitter.com/Arcaniax)");
                p.sendMessage(prefix + TextFormat.GOLD + "Sponsored by: " +  TextFormat.YELLOW + "@goCreativeMC (https://twitter.com/goCreativeMC)");
                p.sendMessage(prefix + TextFormat.GOLD + "Plugin download: " + TextFormat.YELLOW + "https://www.spigotmc.org/resources/23118/");
                p.sendMessage(prefix + TextFormat.GOLD + "More brushes: " + TextFormat.YELLOW + "https://gumroad.com/aerios#JAtxa");
                p.sendMessage(prefix + TextFormat.GOLD + "This build is a fork of goBrush for PowerNukkit by PleaseInsertNameHere (https://github.com/PleaseInsertNameHere/goBrushNK)");

                return true;
            }
            if (p.hasPermission("gobrush.admin")) {
                p.sendMessage(adminUsage);
                return true;
            } else if (p.hasPermission("gobrush.export")) {
                p.sendMessage(exportUsage);
                return true;
            }
            p.sendMessage(exportUsage);
            return true;
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("size") || args[0].equalsIgnoreCase("s")) {
                try {
                    int sizeAmount = Integer.parseInt(args[1]);
                    if (sizeAmount > bp.getMaxBrushSize() && !p.hasPermission("gobrush.bypass.maxsize")) {
                        p.sendMessage(prefix + TextFormat.GOLD + "The maximum size is " + TextFormat.YELLOW + bp.getMaxBrushSize());
                        sizeAmount = bp.getMaxBrushSize();
                    } else if (sizeAmount < 5) {
                        p.sendMessage(prefix + TextFormat.GOLD + "The minimum size is " + TextFormat.YELLOW + "5");
                        sizeAmount = 5;
                    } else if (sizeAmount % 2 == 0) {
                        sizeAmount++;
                    }
                    bp.setBrushSize(sizeAmount);
                    p.sendMessage(prefix + TextFormat.GOLD + "Size set to: " + TextFormat.YELLOW + sizeAmount);
                    bp.getBrush().resize(sizeAmount);

                    return true;
                } catch (Exception e) {
                    p.sendMessage(prefix + TextFormat.RED + "/gb size [number]");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("intensity") || args[0].equalsIgnoreCase("i")) {
                try {
                    int intensityAmount = Integer.parseInt(args[1]);
                    if (intensityAmount > bp.getMaxBrushIntensity() && !p.hasPermission("gobrush.bypass.maxintensity")) {
                        p.sendMessage(prefix + TextFormat.GOLD + "The maximum intensity is " + TextFormat.YELLOW + bp.getBrushIntensity());
                        intensityAmount = bp.getMaxBrushIntensity();
                    } else if (intensityAmount < 1) {
                        p.sendMessage(prefix + TextFormat.GOLD + "The minimum intensity is " + TextFormat.YELLOW + "1");
                        intensityAmount = 1;
                    }
                    bp.setBrushIntensity(intensityAmount);
                    p.sendMessage(prefix + TextFormat.GOLD + "Intensity set to: " + TextFormat.YELLOW + intensityAmount);
                    return true;
                } catch (Exception e) {
                    p.sendMessage(prefix + TextFormat.RED + "/gb intensity [number]");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("brush") || args[0].equalsIgnoreCase("b")) {
                String name = args[1].replace("_", " ");
                if (Session.containsBrush(name)) {
                    int size = bp.getBrushSize();
                    Brush brush = Session.getBrush(name);
                    bp.setBrush(brush);
                    bp.getBrush().resize(size);
                    p.sendMessage(prefix + TextFormat.GOLD + "Brush set to: " + TextFormat.YELLOW +  name);
                    return true;
                } else {
                    p.sendMessage(prefix + TextFormat.RED + "Could not load brush \"" + name + "\"");
                    return true;
                }
            } else if ((args[0].equalsIgnoreCase("export") || args[0].equalsIgnoreCase("e")) && p.hasPermission(
                    "gobrush.export")) {
                final String name = args[1];
                Server.getInstance().getScheduler().scheduleTask(GoBrushPlugin.plugin, () -> {
                        HeightMapExporter hm;
                        try {
                            hm = new HeightMapExporter(p);
                        } catch (Exception e) {
                            p.sendMessage(prefix + TextFormat.RED + "Please make a WorldEdit selection " + TextFormat.GOLD + "(//wand)");
                            return;
                        }
                        if (!hm.hasWorldEditSelection()) {
                            p.sendMessage(prefix + TextFormat.RED + "Please make a WorldEdit selection "+ TextFormat.DARK_GRAY + "(//wand)");
                            return;
                        }
                        hm.exportImage(500, name);
                        p.sendMessage(prefix + TextFormat.GOLD + "Exported " + TextFormat.YELLOW + name + ".png");
                        Session.initializeValidBrushes();
                        Session.initializeBrushMenu();
                    }, true);
                return true;
            }
            if (p.hasPermission("gobrush.admin")) {
                p.sendMessage(adminUsage);
                return true;
            } else if (p.hasPermission("gobrush.export")) {
                p.sendMessage(exportUsage);
                return true;
            }
            p.sendMessage(exportUsage);
            return true;
        }
        return false;
    }
}
