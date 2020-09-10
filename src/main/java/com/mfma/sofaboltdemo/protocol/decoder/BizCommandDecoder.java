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
package com.mfma.sofaboltdemo.protocol.decoder;

import com.alibaba.fastjson.JSONObject;
import com.mfma.sofaboltdemo.config.Agreement;
import com.mfma.sofaboltdemo.protocol.dto.IotRequest;
import com.mfma.sofaboltdemo.protocol.helper.CommandHelper;
import com.mfma.sofaboltdemo.sofabolt.CommandDecoder;
import com.mfma.sofaboltdemo.sofabolt.ProtocolCode;
import com.mfma.sofaboltdemo.util.NettyUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Command decoder for Rpc v2.
 *
 * @author jiangping
 * @version $Id: RpcCommandDecoderV2.java, v 0.1 2017-05-27 PM5:15:26 tao Exp $
 */
public class BizCommandDecoder implements CommandDecoder {

    private Logger logger = LoggerFactory.getLogger(BizCommandDecoder.class);


    /**
     * @see CommandDecoder#decode(ChannelHandlerContext, ByteBuf, List, ProtocolCode)
     */
    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out, ProtocolCode protocolCode) throws Exception {
        String companyId = NettyUtils.getHexString(in, Agreement.COMPANY_ID_SIZE);
        int keyIndex = NettyUtils.getInt(in);
        int cmd = NettyUtils.getInt(in);
        JSONObject code = protocolCode.getCode();
        String header =  code.getString("protocolHeader");
        String version = code.getString("protocolVersion");
        AbstractDecoder<ByteBuf> abstractDecoder = CommandHelper.getDecoder(version,cmd);
        IotRequest iotRequest = abstractDecoder.decoder(in);
        iotRequest.setHeader(header);
        iotRequest.setVersion(version);
        iotRequest.setCompanyId(companyId);
        iotRequest.setKeyIndex(keyIndex);
        iotRequest.setCmd(cmd);
        out.add(iotRequest);
    }

}
