/*
 * Copyright © 2015, Leon Mangler and the SuperVanish contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.myzelyam.supervanish.hooks;

import de.myzelyam.api.vanish.PlayerHideEvent;
import de.myzelyam.api.vanish.PlayerShowEvent;
import de.myzelyam.supervanish.SuperVanish;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.dynmap.bukkit.DynmapPlugin;

public class DynmapHook extends PluginHook {

    public DynmapHook(SuperVanish superVanish) {
        super(superVanish);
    }

    private void adjustVisibility(Player p, boolean hide, boolean broadcast) {
        try {
            DynmapPlugin dynmap = (DynmapPlugin) Bukkit.getPluginManager()
                    .getPlugin("dynmap");
            SuperVanish plugin = superVanish;
            boolean sendJoinLeave
                    = superVanish.getSettings().getBoolean("HookOptions.DynmapSendJoinLeaveMessages")
                    && broadcast;
            if (hide) {
                if (sendJoinLeave)
                    dynmap.postPlayerJoinQuitToWeb(p, false);
                dynmap.assertPlayerInvisibility(p, true, plugin);
            } else {
                dynmap.assertPlayerInvisibility(p, false, plugin);
                if (sendJoinLeave)
                    dynmap.postPlayerJoinQuitToWeb(p, true);
            }
        } catch (Exception e) {
            superVanish.logException(e);
        }
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVanish(PlayerHideEvent e) {
        Player p = e.getPlayer();
        adjustVisibility(p, true, true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onReappear(PlayerShowEvent e) {
        Player p = e.getPlayer();
        adjustVisibility(p, false, true);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (superVanish.getVanishStateMgr().isVanished(p.getUniqueId())) {
            adjustVisibility(p, true, false);
        }
    }

/*    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (superVanish.getVanishStateMgr().isVanished(p.getUniqueId())) {
            adjustVisibility(p, false, false);
        }
    }*/
}
