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
import fr.vinetos.lobbysigns.common.messages.MessagesUtils;
import fr.vinetos.lobbysigns.common.messages.ServerInfo;
import fr.vinetos.lobbysigns.common.messages.ServerStatus;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.*;
import java.util.Map;
import java.util.Optional;

public class CustomBungeeMessenger implements Listener {

    @EventHandler
    public void onEvent(PluginMessageEvent event) {
        if (!event.getTag().equals(References.TAG))
            return;

        // Transfer message to lobby servers and register when the message has been received
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
        try {
            ServerInfo serverInfo = MessagesUtils.readMessage(in.readUTF());
            updateInfo(serverInfo);
            sendToLobby(serverInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateInfo(ServerInfo serverInfo) {
        // Update in list
        Optional<Map.Entry<ServerInfo, Long>> optional
                = LobbySignsPlugin.getLobbySignsPlugin().getServersInfoMap().entrySet().stream()
                .filter((entry) -> entry.getKey().getServerName().equals(serverInfo.getServerName()))
                .findFirst();
        // If present update, if not add
        if (optional.isPresent()) {
            ServerInfo update = optional.get().getKey().update(serverInfo);
            LobbySignsPlugin.getLobbySignsPlugin().getServersInfoMap().put(update, System.currentTimeMillis());
        } else
            LobbySignsPlugin.getLobbySignsPlugin().getServersInfoMap().put(serverInfo, System.currentTimeMillis());
    }

    public void sendToLobby(ServerInfo serverInfo) {
        // If no player is online, don't send infos
        if (ProxyServer.getInstance().getOnlineCount() == 0)
            return;
        // The info of the server is a lobby
        if (serverInfo.getServerName().contains("Lobby"))
            return;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try {
            out.writeUTF(MessagesUtils.writeMessage(serverInfo));
            byte[] message = stream.toByteArray();
            LobbySignsPlugin.getLobbySignsPlugin().getServersInfoMap().entrySet().stream()
                    .filter((entry) -> entry.getKey().getServerName().contains("Lobby")
                            && entry.getKey().getServerStatus() == ServerStatus.AVAILABLE)
                    .forEach((entry) ->
                            ProxyServer.getInstance().getServerInfo(entry.getKey().getServerName()).sendData(References.TAG, message));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
