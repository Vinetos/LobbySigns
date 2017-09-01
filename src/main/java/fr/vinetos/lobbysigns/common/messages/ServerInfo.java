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
package fr.vinetos.lobbysigns.common.messages;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang.Validate;

import java.util.HashMap;
import java.util.Map;

/**
 * Message sent between server wich contains Info from the server
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY) // Ignore null and empty values
public class ServerInfo {
    // All info of server
    private final String serverName;
    private final byte serverStatus;
    private Map<String, Object> datas = new HashMap<>();

    public ServerInfo(String serverName, ServerStatus serverStatus) {
        this.serverName = serverName;
        this.serverStatus = serverStatus.getMessageId();
    }

    /**
     * Add optional info to the message
     *
     * @param infoKey
     * @param infoValue
     * @return
     */
    public ServerInfo setInfo(String infoKey, Object infoValue) {
        Validate.notEmpty(infoKey, "Key cannot be null or empty !");
        Validate.notNull(infoValue, "Value cannot be null !");

        datas.put(infoKey, infoValue);
        return this;
    }

    /**
     * Get optional info from the message
     *
     * @param infoKey
     * @param <T>
     * @return
     */
    public <T> T get(String infoKey) {
        Validate.notEmpty(infoKey, "Key cannot be null or empty !");
        return (T) datas.get(infoKey);
    }

    public ServerInfo update(ServerInfo updateServerInfo) {
        Validate.notNull(updateServerInfo.serverName);
        if (!updateServerInfo.serverName.equals(serverName))
            throw new IllegalArgumentException("Server name must be the same to update server info !");
        // Merge and update new info
        datas.putAll(updateServerInfo.datas);
        return this;
    }

    public final String getServerName() {
        return serverName;
    }

    public ServerStatus getServerStatus() {
        return ServerStatus.getById(serverStatus);
    }
}