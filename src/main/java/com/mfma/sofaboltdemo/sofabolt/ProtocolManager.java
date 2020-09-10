/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mfma.sofaboltdemo.sofabolt;

import com.alibaba.fastjson.JSONObject;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Manager of all protocols
 *
 * @author tsui
 * @version $Id: ProtocolManager.java, v 0.1 2018-03-27 15:18 tsui Exp $
 */
public class ProtocolManager {

    private static final ConcurrentMap<ProtocolCode, Protocol> protocols = new ConcurrentHashMap<ProtocolCode, Protocol>();

    public static Protocol getProtocol(ProtocolCode protocolCode) {
        return protocols.get(protocolCode);
    }


    public static void registerProtocol(ConcurrentMap<ProtocolCode, Protocol> protocolsMap) {
        ProtocolManager.protocols.putAll(protocolsMap);
    }


    public static Protocol unRegisterProtocol( JSONObject protocolCode) {
        return ProtocolManager.protocols.remove(ProtocolCode.fromBytes(protocolCode));
    }


}