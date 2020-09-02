package com.mfma.sofaboltdemo.protocol.helper;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.mfma.sofaboltdemo.config.ApplicationContextHelper;
import com.mfma.sofaboltdemo.protocol.decoder.AbstractDecoder;
import com.mfma.sofaboltdemo.protocol.decoder.Cmd1Decoder;
import com.mfma.sofaboltdemo.protocol.encoder.AbstractEncoder;
import com.mfma.sofaboltdemo.protocol.encoder.Cmd1Encoder;
import com.mfma.sofaboltdemo.protocol.encoder.Cmd2Encoder;
import com.mfma.sofaboltdemo.sofabolt.rpc.protocol.RpcProtocol;
import io.netty.buffer.ByteBuf;

public class CommandHelper {

    private static final Table<String,Integer, Class<? extends AbstractDecoder<ByteBuf>>> DECODER_TABLE = HashBasedTable.create();

    private static final Table<String,Integer, Class<? extends AbstractEncoder<ByteBuf>>> ENCODER_TABLE = HashBasedTable.create();


    static {
        DECODER_TABLE.put(RpcProtocol.PROTOCOL_VERSION,1, Cmd1Decoder.class);
        DECODER_TABLE.put(RpcProtocol.PROTOCOL_VERSION,2, Cmd1Decoder.class);
    }

    static {
        ENCODER_TABLE.put(RpcProtocol.PROTOCOL_VERSION,1, Cmd1Encoder.class);
        ENCODER_TABLE.put(RpcProtocol.PROTOCOL_VERSION,2, Cmd2Encoder.class);
    }

    public static AbstractDecoder<ByteBuf> getDecoder(String version, int cmd) {
        Class<? extends AbstractDecoder<ByteBuf>> clazz = DECODER_TABLE.get(version,cmd);
        return ApplicationContextHelper.getBean(clazz);
    }


    public static AbstractEncoder<ByteBuf> getEncoder(String version, int cmd) {
        Class<? extends AbstractEncoder<ByteBuf>> clazz = ENCODER_TABLE.get(version,cmd);
        return ApplicationContextHelper.getBean(clazz);
    }
}
