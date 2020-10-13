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
package com.alipay.mfma.protocol;

import com.alibaba.fastjson.JSONObject;
import com.alipay.mfma.config.Agreement;
import com.alipay.mfma.protocol.util.NettyUtils;
import com.alipay.remoting.Connection;
import com.alipay.remoting.Protocol;
import com.alipay.remoting.ProtocolCode;
import com.alipay.remoting.ProtocolManager;
import com.alipay.remoting.codec.AbstractBatchDecoder;
import com.alipay.remoting.exception.CodecException;
import com.alipay.remoting.log.BoltLoggerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;

import java.util.List;

/**
 * Protocol code based decoder, the main decoder for a certain protocol, which is lead by one or multi bytes (magic code).
 * <p>
 * Notice: this is not stateless, can not be noted as {@link io.netty.channel.ChannelHandler.Sharable}
 *
 * @author xiaomin.cxm
 * @version $Id: ProtocolCodeBasedDecoder.java, v0.1 Mar 20, 2017 2:42:46 PM xiaomin.cxm Exp $
 */
@ChannelHandler.Sharable
public class ProtocolDecoder extends AbstractBatchDecoder {
	
	private static final Logger logger = BoltLoggerFactory.getLogger("RpcRemoting");
	
	public static final int DEFAULT_DATA_LENGTH = 4;
	
	public ProtocolDecoder() {
		super();
	}
	
	/**
	 * decode the protocol code
	 *
	 * @param in input byte buf
	 * @return an instance of ProtocolCode
	 */
	protected ProtocolCode decodeProtocolCode(ByteBuf in) {
		//是否满足最少4字节要求，即接收到的数据包的大小要大等于header+version
		if (in.readableBytes() >= DEFAULT_DATA_LENGTH) {
			String protocolHeader = NettyUtils.getHexString(in, 2);
			String protocolVersion = NettyUtils.getHexString(in, 2);
			JSONObject protocolCode = new JSONObject().fluentPut("protocolHeader", protocolHeader).fluentPut("protocolVersion", protocolVersion);
			return ProtocolCode.fromBytes(protocolCode);
		}
		return null;
	}
	
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		in.markReaderIndex();
		ProtocolCode protocolCode = decodeProtocolCode(in);
		if (null != protocolCode) {
			if (ctx.channel().attr(Connection.PROTOCOL).get() == null) {
				ctx.channel().attr(Connection.PROTOCOL).set(protocolCode);
//                if (DEFAULT_ILLEGAL_PROTOCOL_VERSION_LENGTH != protocolVersion) {
				ctx.channel().attr(Connection.VERSION).set(protocolCode.toString());
//                }
			}
			Protocol protocol = ProtocolManager.getProtocol(protocolCode);
			if (null != protocol) {
				int contentLength = in.readUnsignedShortLE();
				int packageSize = contentLength + Agreement.HEADER_SIZE + Agreement.VERSION_SIZE + Agreement.LENGHT_SIZE;
				in.resetReaderIndex();
				//获取除校验外的所有数据
				byte[] toBeChecked = NettyUtils.getBytes(in, packageSize - Agreement.CRC_SIZE);
				String checkedSum = NettyUtils.getHexString(in, Agreement.CRC_SIZE);
				//判断校验和是否正确
				boolean isLegal = NettyUtils.checkSum(toBeChecked, checkedSum);
				if (isLegal) {
					in.readerIndex(Agreement.HEADER_SIZE + Agreement.VERSION_SIZE + Agreement.LENGHT_SIZE);
					protocol.getDecoder().decode(ctx, in, out, protocolCode);
				} else {
					throw new CodecException("Illegal Checksum");
				}
			} else {
				throw new CodecException("Unknown protocol code: [" + protocolCode.toString() + "] while decode in ProtocolDecoder.");
			}
		} else {
			throw new CodecException("不正确的数据包");
		}
	}
}
