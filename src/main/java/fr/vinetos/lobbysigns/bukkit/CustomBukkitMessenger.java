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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.vinetos.lobbysigns.common.References;
import fr.vinetos.lobbysigns.common.messages.MessagesUtils;
import fr.vinetos.lobbysigns.common.messages.ServerInfo;
import fr.vinetos.lobbysigns.common.messages.ServerStatus;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.IOException;
import java.util.Optional;

public final class CustomBukkitMessenger implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String tag, Player player, byte[] message) {
        // Stop here if the message isn't for LobbySigns
        if (!tag.equals(References.TAG))
            return;

        // Parse JSON
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        try {
            ServerInfo infos = MessagesUtils.readMessage(in.readUTF());
            updateServerInfo(infos);// Update signs info into the list and do whatever you want

            if (infos.getServerStatus() == ServerStatus.AVAILABLE || infos.getServerStatus() == ServerStatus.UPDATE) {
                // Trigger update for signs here
                LobbySignsPlugin.getLobbySignsPlugin().updateSigns(infos);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateServerInfo(ServerInfo serverInfo) {
        // Update in list
        Optional<ServerInfo> optional = LobbySignsPlugin.getLobbySignsPlugin().getServersInfoList().stream()
                .filter((si) -> si.getServerName().equals(serverInfo.getServerName()))
                .findFirst();
        // If present update, if not add
        if (optional.isPresent())
            optional.get().update(serverInfo);
        else
            LobbySignsPlugin.getLobbySignsPlugin().getServersInfoList().add(serverInfo);
    }

    public void sendMessage(ServerInfo info) {
        Bukkit.getOnlinePlayers().stream().findAny().ifPresent(player -> sendMessage(info, player));
    }

    public void sendMessage(ServerInfo infos, Player player) {
        try {
            String message = MessagesUtils.writeMessage(infos);
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF(message);
            player.sendPluginMessage(LobbySignsPlugin.getLobbySignsPlugin(), References.TAG, out.toByteArray());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
