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
import org.apache.commons.lang.text.StrBuilder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;

import java.util.concurrent.CopyOnWriteArrayList;

import static fr.vinetos.lobbysigns.common.References.*;

public final class LobbySignsPlugin extends JavaPlugin {

    private static LobbySignsPlugin lobbySignsPlugin;
    // Not empty only for LobbyServer
    private CopyOnWriteArrayList<ServerInfo> serversInfoList = new CopyOnWriteArrayList<>();
    // Send/receive message to/from Bungeecord
    private CustomBukkitMessenger customBukkitMessenger;

    public static LobbySignsPlugin getLobbySignsPlugin() {
        return lobbySignsPlugin;
    }

    @Override
    public void onLoad() {
        lobbySignsPlugin = this;
    }

    @Override
    public void onEnable() {
        // Register messenger
        Messenger messenger = getServer().getMessenger();
        customBukkitMessenger = new CustomBukkitMessenger();

        // Only lobbys server have to register incoming plugin channel
        if (Bukkit.getServerName().contains("Lobby")) {
            messenger.registerIncomingPluginChannel(getLobbySignsPlugin(), References.TAG, customBukkitMessenger);
        } else {
            // Register event for game server
            Bukkit.getServer().getPluginManager().registerEvents(new PlayerConnectionListener(), this);
        }
        messenger.registerOutgoingPluginChannel(getLobbySignsPlugin(), References.TAG);

        // Send first message when the server is ready
        ServerInfo info = new ServerInfo(Bukkit.getServerName(), ServerStatus.AVAILABLE)
                .setInfo(DISPLAY_NAME, Bukkit.getServerName())
                .setInfo(ONLINE_PLAYERS, Bukkit.getOnlinePlayers())
                .setInfo(MAX_PLAYER, Bukkit.getMaxPlayers())
                .setInfo(ACCEPTING_PLAYER, true);
        customBukkitMessenger.sendMessage(info);

        // Send info all 10 seconds
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            // Send only minimal infos (server name and status)
            customBukkitMessenger.sendMessage(new ServerInfo(Bukkit.getServerName(), ServerStatus.AVAILABLE));
        }, 200L, 200L);
    }

    @Override
    public void onDisable() {
        // Send message when the server shutdown
        // Send only minimal infos (server name and status)
        customBukkitMessenger.sendMessage(new ServerInfo(Bukkit.getServerName(), ServerStatus.OFF));
    }

    public void updateSigns(ServerInfo infos) {
        // You can update the sign associated with this ServerInfo (do a Map<ServerInfo, Sign> for example)
        // Here we just print the output of the server
        String value = new StrBuilder("Received info of server ").append(infos.getServerName()).appendln(" : ")
                .appendln((Integer) infos.get(ONLINE_PLAYERS))
                .appendln((Integer) infos.get(MAX_PLAYER))
                .appendln((Boolean) infos.get(ACCEPTING_PLAYER))
                .appendln((String) infos.get(DISPLAY_NAME))
                .toString();
        System.out.println(value);
    }

    public CopyOnWriteArrayList<ServerInfo> getServersInfoList() {
        return serversInfoList;
    }

    public CustomBukkitMessenger getCustomBukkitMessenger() {
        return customBukkitMessenger;
    }
}
