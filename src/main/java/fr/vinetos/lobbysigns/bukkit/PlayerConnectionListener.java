/*
 * ==============================================================================
 *            _    _______   __________________  _____
 *            | |  / /  _/ | / / ____/_  __/ __ \/ ___/
 *            | | / // //  |/ / __/   / / / / / /\__ \
 *            | |/ // // /|  / /___  / / / /_/ /___/ /
 *            |___/___/_/ |_/_____/ /_/  \____//____/
 *
 * ==============================================================================
 *
 * LobbySigns game
 * LobbySigns Copyright (C) 2017  Vinetos
 * 
 * ==============================================================================
 * 
 * This file is part of LobbySigns.
 * 
 * LobbySigns is free software: you can redistribute it and/or modify
 * it under the terms of the MIT License.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in 
 * the Software without restriction, including without limitation the rights to 
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies 
 * of the Software, and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all 
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS 
 * FOR A PARTICULAR PURPOSE AND NONINFINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *==============================================================================
 */
package fr.vinetos.lobbysigns.bukkit;

import fr.vinetos.lobbysigns.common.References;
import fr.vinetos.lobbysigns.common.messages.ServerInfo;
import fr.vinetos.lobbysigns.common.messages.ServerStatus;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {

    @EventHandler
    public void onEvent(PlayerJoinEvent event) {
        CustomBukkitMessenger customBukkitMessenger = LobbySignsPlugin.getLobbySignsPlugin().getCustomBukkitMessenger();
        // Update the server infos for lobbys
        customBukkitMessenger.sendMessage(
                new ServerInfo(Bukkit.getServerName(), ServerStatus.UPDATE)
                        .setInfo(References.ONLINE_PLAYERS, Bukkit.getOnlinePlayers().size())
                // if you want to disable the connection to the current server
                // .setInfo(References.ACCEPTING_PLAYER, false)
        );
    }

    @EventHandler
    public void onEvent(PlayerQuitEvent event) {
        CustomBukkitMessenger customBukkitMessenger = LobbySignsPlugin.getLobbySignsPlugin().getCustomBukkitMessenger();
        customBukkitMessenger.sendMessage(
                new ServerInfo(Bukkit.getServerName(), ServerStatus.UPDATE)
                        .setInfo(References.ONLINE_PLAYERS, Bukkit.getOnlinePlayers().size())
                // if you want to accept the connection to the current server
                // .setInfo(References.ACCEPTING_PLAYER, true)
        );
    }


}
