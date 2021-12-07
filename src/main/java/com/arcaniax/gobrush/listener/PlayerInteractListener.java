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
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Location;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.TextFormat;
import com.arcaniax.gobrush.Session;
import com.arcaniax.gobrush.object.BrushPlayer;
import com.arcaniax.gobrush.util.BrushPlayerUtil;
import com.arcaniax.gobrush.util.GuiGenerator;
import com.arcaniax.gobrush.util.NestedFor;
import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.nukkit.core.NukkitWorldEdit;
import com.boydti.fawe.object.FaweQueue;
import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

/**
 * This class contains the listener that gets fired when a player interacts with
 * the world.
 *
 * @author Arcaniax, McJeffr
 */
public class PlayerInteractListener implements Listener {

    private static final String PREFIX = TextFormat.AQUA + "goBrush> ";
    private static final String PERMISSION_BYPASS_WORLD = "gobrush.bypass.world";
    private static final String PERMISSION_USE = "gobrush.use";

    @EventHandler
    public void onClickEvent(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final boolean holdingFlint = player.getInventory().getItemInHand().getId() == ItemID.FLINT;

        if (!holdingFlint) {
            return;
        }
        if (!player.hasPermission(PERMISSION_USE)) {
            return;
        }
        if (event.getPlayer().getInventory().getItemInHand().getId() == ItemID.FLINT
                && ((event.getAction().equals(PlayerInteractEvent.Action.RIGHT_CLICK_AIR))
                || (event.getAction().equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)))) {
            BrushPlayer brushPlayer = Session.getBrushPlayer(player.getUniqueId());
            if ((Session.getConfig().getDisabledWorlds().contains(player.getLevelName())) && (!player.hasPermission(
                    PERMISSION_BYPASS_WORLD))) {
                return;
            }
            if (!(brushPlayer.isBrushEnabled())) {
                player.sendMessage(PREFIX + TextFormat.RED + "Your brush is disabled, left click to enable the brush or type " + TextFormat.WHITE + "/gb toggle" + TextFormat.RED + ".");
                return;
            }
            Location loc;
            if (event.getAction().equals(PlayerInteractEvent.Action.RIGHT_CLICK_AIR)) {
                loc = BrushPlayerUtil.getClosest(player);
            } else {
                loc = event.getBlock().getLocation().clone();
            }
            if (loc == null) {
                return;
            }
            LocalSession localSession = NukkitWorldEdit.inst().getSession(player);
            CompletableFuture.runAsync(() -> {
                synchronized (localSession) {
                    EditSession editsession = new EditSessionBuilder(FaweAPI.getWorld(loc.getLevelName())).fastmode(true).build();
                    try {
                        HashMap<Vector3, BaseBlock> blocksToSet = new HashMap<>();
                        try {
                            editsession.setFastMode(false);
                            int size = brushPlayer.getBrushSize();
                            Vector3 start = player.getDirectionVector();
                            Vector v = new Vector(start.x, start.y, start.z);
                            double rot = (player.getLocation().getYaw() - 90.0F) % 360.0F + 360.0F;
                            final double rotation = (rot / 360.0F) * (2 * Math.PI);
                            double rotPitch = (player.getLocation().getPitch()) % 360.0F;
                            rotPitch += 360.0F;
                            final double rotationPitch = (rotPitch / 360.0F) * (2 * Math.PI);
                            if (!brushPlayer.is3DMode()) {
                                int min = size / 2 * -1;
                                int max = size / 2;
                                Random r = new Random();
                                double random = r.nextDouble();
                                String cardinal = BrushPlayerUtil.getCardinalDirection(player);
                                for (int x = min; x <= max; x++) {
                                    for (int z = min; z <= max; z++) {
                                        Location loopLoc = loc.clone().add(x, 0.0D, z);
                                        double worldHeight = editsession.getHighestTerrainBlock(
                                                (int) loopLoc.x,
                                                (int) loopLoc.z,
                                                0,
                                                255
                                        );
                                        if (editsession.getMask() == null || editsession
                                                .getMask()
                                                .test(new BlockVector(loopLoc.x, worldHeight, loopLoc.z))) {
                                            double height = BrushPlayerUtil.getHeight(player, x - min, z - min, cardinal);
                                            double subHeight = height % 1.0D;
                                            if (random > 1.0 - subHeight) {
                                                height++;
                                            }
                                            Location l = new Location(
                                                    loopLoc.x,
                                                    worldHeight,
                                                    loopLoc.z,
                                                    loopLoc.getLevel()
                                            );
                                            if (brushPlayer.isDirectionMode()) {
                                                for (int y = 1; y < Math.floor(height); y++) {
                                                    if ((!brushPlayer.isFlatMode()) || l.y + y <= loc.getY()) {
                                                        try {
                                                            blocksToSet.put(
                                                                    new Vector3(
                                                                            l.x,
                                                                            l.y + y,
                                                                            l.z
                                                                    ),
                                                                    editsession.getBlock(new Vector(l.x, l.y, l.z)
                                                                            .toBlockPoint())
                                                            );
                                                        } catch (Exception ignored) {
                                                        }
                                                    }
                                                }
                                            } else {
                                                for (int y = 0; y < Math.floor(height); y++) {
                                                    if ((!brushPlayer.isFlatMode()) || l.y - y > loc.getY()) {
                                                        if (editsession.getMask() == null || editsession.getMask().test(new Vector(l.x, l.y - y, l.z)
                                                                .toBlockPoint())) {
                                                            if (!(l.y - y < 0)) {
                                                                try {
                                                                    blocksToSet.put(new Vector3(
                                                                            l.x,
                                                                            l.y + y,
                                                                            l.z
                                                                    ), new BaseBlock(0));
                                                                } catch (Exception ignored) {
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                for (Vector3 block : blocksToSet.keySet()) {
                                    BlockVector vec = Vector.toBlockPoint(block.x, block.y, block.z);
                                    editsession.setBlock(
                                            vec.getBlockX(),
                                            vec.getBlockY(),
                                            vec.getBlockZ(),
                                            blocksToSet.get(block)
                                    );
                                }
                                blocksToSet.clear();
                            } else { //3D Mode
                                int min = size / 2 * -1;
                                int max = size / 2;
                                double xMov = Math.cos(rotation);
                                double zMov = Math.sin(rotation);
                                double yMod = Math.cos(rotationPitch);

                                double mod = 0.25;
                                xMov *= mod;
                                zMov *= mod;
                                yMod *= mod;

                                Random r = new Random();
                                double random = r.nextDouble();


                                double finalZMov = zMov;
                                double finalYMod = yMod;
                                double finalXMov = xMov;
                                NestedFor.IAction threeDimensionModeXZLoops = indices -> {

                                    Vector3 vec3 = start.clone().add(finalZMov * indices[0], indices[1] * finalYMod, -finalXMov * indices[0]);
                                    Location loopLoc = new Location(vec3.x, vec3.y, vec3.z, loc.getLevel());

                                    if (player.getLocation().getPitch() < 0) {
                                        loopLoc.add(
                                                (1 - finalYMod) * finalXMov * indices[1],
                                                0,
                                                (1 - finalYMod) * finalZMov * indices[1]
                                        );
                                    } else {
                                        loopLoc.add(
                                                -(1 - finalYMod) * finalXMov * indices[1],
                                                0,
                                                -(1 - finalYMod) * finalZMov * indices[1]
                                        );
                                    }

                                    Location blockLoc = BrushPlayerUtil.getClosest(
                                            player,
                                            loopLoc.clone(),
                                            loc.clone(),
                                            size,
                                            editsession
                                    );

                                    if (blockLoc != null && (editsession.getMask() == null || editsession.getMask().test(
                                            new Vector(blockLoc.x, blockLoc.y, blockLoc.z)))) {
                                        double height = BrushPlayerUtil.getHeight(
                                                player,
                                                indices[0] - min,
                                                indices[1] - min,
                                                "N"
                                        );
                                        double subHeight = height % 1.0D;
                                        if (height == 255.0) {
                                            subHeight = 1.0;
                                        }
                                        if (random > 1.0 - subHeight) {
                                            height++;
                                        }
                                        for (int y = 0; y < height; y++) {
                                            Vector _v = v.multiply(-1).multiply(y);
                                            if (brushPlayer.isDirectionMode()) {
                                                if (!brushPlayer.isFlatMode()) {
                                                    try {
                                                        blocksToSet.put(
                                                                new Vector3(
                                                                        blockLoc.x + _v.getBlockX(),
                                                                        blockLoc.y + _v.getBlockY(),
                                                                        blockLoc.z + _v.getBlockZ()
                                                                ),
                                                                editsession.getBlock(new Vector(
                                                                                blockLoc.x,
                                                                                blockLoc.y,
                                                                                blockLoc.z
                                                                        )
                                                                        .toBlockPoint())
                                                        );
                                                    } catch (Exception ignored) {
                                                    }
                                                } else {
                                                    Vector v_ = v.multiply(-1).multiply(y);
                                                    Location place = blockLoc.clone().add(v_.getBlockX(), v_.getBlockY(), v_.getBlockZ());
                                                    if (place.distance(loopLoc) > loc.distance(start)) {
                                                        try {
                                                            blocksToSet.put(
                                                                    new Vector3(
                                                                            blockLoc.x + _v.getBlockX(),
                                                                            blockLoc.y + _v.getBlockY(),
                                                                            blockLoc.z + _v.getBlockZ()
                                                                    ),
                                                                    editsession.getBlock(new Vector(
                                                                                    blockLoc.x,
                                                                                    blockLoc.y,
                                                                                    blockLoc.z
                                                                            )
                                                                            .toBlockPoint())
                                                            );
                                                        } catch (Exception ignored) {
                                                        }
                                                    }
                                                }
                                            } else {
                                                if (!brushPlayer.isFlatMode()) {
                                                    try {
                                                        blocksToSet.put(
                                                                new Vector3(
                                                                        blockLoc.x + _v.getBlockX(),
                                                                        blockLoc.y + _v.getBlockY(),
                                                                        blockLoc.z + _v.getBlockZ()
                                                                ),
                                                                editsession.getBlock(new Vector(
                                                                                blockLoc.x,
                                                                                blockLoc.y,
                                                                                blockLoc.z
                                                                        )
                                                                        .toBlockPoint())
                                                        );
                                                    } catch (Exception ignored) {
                                                    }
                                                } else {
                                                    Vector v_ = v.multiply(1).multiply(y - 1);
                                                    Location place = blockLoc.clone().add(v_.getBlockX(), v_.getBlockY(), v_.getBlockZ());
                                                    if (place.distance(loopLoc) < loc.distance(start) - 1) {
                                                        try {
                                                            blocksToSet.put(
                                                                    new Vector3(
                                                                            blockLoc.x + _v.getBlockX(),
                                                                            blockLoc.y + _v.getBlockY(),
                                                                            blockLoc.z + _v.getBlockZ()
                                                                    ),
                                                                    editsession.getBlock(new Vector(
                                                                                    blockLoc.x,
                                                                                    blockLoc.y,
                                                                                    blockLoc.z
                                                                            ).toBlockPoint())
                                                            );
                                                        } catch (Exception ignored) {
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                };
                                NestedFor nf = new NestedFor(min - 1, max, threeDimensionModeXZLoops);
                                nf.nFor(2);

                                for (Vector3 block : blocksToSet.keySet()) {
                                    if (brushPlayer.isDirectionMode()) {
                                        editsession.setBlock(
                                                (int) block.x,
                                                (int) block.y,
                                                (int) block.z,
                                                blocksToSet.get(block)
                                        );
                                    } else {
                                        editsession.setBlock(
                                                (int) block.x,
                                                (int) block.y,
                                                (int) block.z,
                                                new BaseBlock(0)
                                        );
                                    }
                                }
                                blocksToSet.clear();
                            }
                        } finally {

                            editsession.commit();
                        }
                    } finally {
                        localSession.remember(editsession);
                    }
                }
            });
        } else if ((event.getPlayer().getInventory().getItemInHand().getId() == ItemID.FLINT)
                && ((event.getAction().equals(PlayerInteractEvent.Action.LEFT_CLICK_AIR))
                || (event.getAction().equals(PlayerInteractEvent.Action.LEFT_CLICK_BLOCK)))) {
            event.setCancelled(true);
            GuiGenerator.openMenu(player, GuiGenerator.generateMainMenu(Session.getBrushPlayer(player.getUniqueId())));
        }
    }


}
