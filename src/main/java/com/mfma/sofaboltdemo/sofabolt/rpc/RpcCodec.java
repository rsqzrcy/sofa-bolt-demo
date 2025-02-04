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
package com.mfma.sofaboltdemo.sofabolt.rpc;

import com.mfma.sofaboltdemo.sofabolt.ProtocolCode;
import com.mfma.sofaboltdemo.sofabolt.codec.Codec;
import com.mfma.sofaboltdemo.sofabolt.codec.ProtocolCodeBasedEncoder;
import com.mfma.sofaboltdemo.sofabolt.rpc.protocol.RpcProtocolDecoder;
import com.mfma.sofaboltdemo.sofabolt.rpc.protocol.RpcProtocolManager;
import com.mfma.sofaboltdemo.sofabolt.rpc.protocol.RpcProtocolV2;

import io.netty.channel.ChannelHandler;

/**
 * @author muyun.cyt
 * @version 2018/6/26 下午3:51
 */
public class RpcCodec implements Codec {

    @Override
    public ChannelHandler newEncoder() {
        return new ProtocolCodeBasedEncoder(ProtocolCode.fromBytes(RpcProtocolV2.PROTOCOL_CODE));
    }

    @Override
    public ChannelHandler newDecoder() {
        return new RpcProtocolDecoder(RpcProtocolManager.DEFAULT_PROTOCOL_CODE_LENGTH);
    }
}
