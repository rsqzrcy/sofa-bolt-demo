package com.mfma.sofaboltdemo.protocol.decoder;

import com.mfma.sofaboltdemo.protocol.dto.IotRequest;
import io.netty.buffer.ByteBuf;

public class Cmd2Decoder extends AbstractDecoder<ByteBuf> {

    @Override
    public IotRequest decoder(ByteBuf o) throws Exception {
        return null;
    }
}
