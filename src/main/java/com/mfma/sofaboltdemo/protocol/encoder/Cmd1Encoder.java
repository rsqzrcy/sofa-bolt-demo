package com.mfma.sofaboltdemo.protocol.encoder;

import com.mfma.sofaboltdemo.protocol.dto.CmdResponseDto;
import com.mfma.sofaboltdemo.protocol.dto.IotResponse;
import com.mfma.sofaboltdemo.util.NettyUtils;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Cmd1Encoder extends AbstractEncoder<ByteBuf> {


    @Override
    public IotResponse encoder(ByteBuf in) throws Exception {
        String data = NettyUtils.getHexString(in, 8);
        log.info("package data=[" + data + "]");
        return new CmdResponseDto(data);
    }
}
