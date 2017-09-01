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
package fr.vinetos.lobbysigns.bungee;

import fr.vinetos.lobbysigns.common.References;
import fr.vinetos.lobbysigns.common.messages.ServerInfo;
import fr.vinetos.lobbysigns.common.messages.ServerStatus;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public final class LobbySignsPlugin extends Plugin {

    private static LobbySignsPlugin lobbySignsPlugin;
    private ConcurrentHashMap<ServerInfo, Long> serversInfoMap = new ConcurrentHashMap<>();
    private CustomBungeeMessenger customBungeeMessenger;

    public static LobbySignsPlugin getLobbySignsPlugin() {
        return lobbySignsPlugin;
    }

    @Override
    public void onLoad() {
        lobbySignsPlugin = this;
    }

    @Override
    public void onEnable() {
        ProxyServer instance = ProxyServer.getInstance();
        customBungeeMessenger = new CustomBungeeMessenger();

        // Open channel for servers info
        instance.registerChannel(References.TAG);
        // Register message Listener
        ProxyServer.getInstance().getPluginManager().registerListener(this, customBungeeMessenger);
        ProxyServer.getInstance().getScheduler().schedule(this, () -> {
            List<ServerInfo> haveToBePing = new ArrayList<>();
            // Check if server have timed out
            serversInfoMap.entrySet().stream().filter(entry -> System.currentTimeMillis() - entry.getValue() >= References.TIME_OUT * 1000)
                    .forEach(entry -> haveToBePing.add(entry.getKey()));

            haveToBePing.forEach(serverInfo -> ProxyServer.getInstance().getServerInfo(serverInfo.getServerName()).ping((ping, throwable) -> {
                if (throwable != null) {
                    ServerInfo info =
                            serverInfo.update(new ServerInfo(serverInfo.getServerName(), ServerStatus.OFF));
                    customBungeeMessenger.sendToLobby(serverInfo);
                }
                // Update ping
                serversInfoMap.put(serverInfo, System.currentTimeMillis());
            }));

        }, 0, References.TIME_OUT, TimeUnit.SECONDS);
    }

    @Override
    public void onDisable() {

    }

    public ConcurrentHashMap<ServerInfo, Long> getServersInfoMap() {
        return serversInfoMap;
    }

    public CustomBungeeMessenger getCustomBungeeMessenger() {
        return customBungeeMessenger;
    }
}
