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
package com.alipay.remoting.rpc;

import com.alibaba.fastjson.JSONObject;
import com.alipay.mfma.protocol.ProtocolDecoder;
import com.alipay.remoting.ProtocolCode;
import com.alipay.remoting.codec.Codec;
import com.alipay.remoting.codec.ProtocolCodeBasedEncoder;
import com.alipay.remoting.rpc.protocol.RpcProtocol;
import io.netty.channel.ChannelHandler;

/**
 * @author muyun.cyt
 * @version 2018/6/26 下午3:51
 */
public class RpcCodec implements Codec {
	
	
	@Override
	public ChannelHandler newEncoder() {
		JSONObject protocolCode = new JSONObject().fluentPut("protocolHeader", RpcProtocol.PROTOCOL_HEADER).fluentPut("protocolVersion", RpcProtocol.PROTOCOL_VERSION);
		return new ProtocolCodeBasedEncoder(ProtocolCode.fromBytes(protocolCode));
	}
	
	@Override
	public ChannelHandler newDecoder() {
		JSONObject protocolCode = new JSONObject().fluentPut("protocolHeader", RpcProtocol.PROTOCOL_HEADER).fluentPut("protocolVersion", RpcProtocol.PROTOCOL_VERSION);
		return new ProtocolDecoder();
	}
}
