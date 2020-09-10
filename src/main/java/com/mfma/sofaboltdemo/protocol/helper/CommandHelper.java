package com.mfma.sofaboltdemo.protocol.helper;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.mfma.sofaboltdemo.config.ApplicationContextHelper;
import com.mfma.sofaboltdemo.protocol.decoder.AbstractDecoder;
import com.mfma.sofaboltdemo.protocol.decoder.Cmd1Decoder;
import com.mfma.sofaboltdemo.protocol.decoder.Cmd2Decoder;
import com.mfma.sofaboltdemo.protocol.encoder.AbstractEncoder;
import com.mfma.sofaboltdemo.protocol.encoder.Cmd1Encoder;
import com.mfma.sofaboltdemo.protocol.encoder.Cmd2Encoder;
import com.mfma.sofaboltdemo.sofabolt.rpc.protocol.RpcProtocol;
import io.netty.buffer.ByteBuf;

public class CommandHelper {

    private static final Table<String, Integer, AbstractDecoder<ByteBuf>> DECODER_TABLE = HashBasedTable.create();

    private static final Table<String, Integer, AbstractEncoder<ByteBuf>> ENCODER_TABLE = HashBasedTable.create();


    static {
        DECODER_TABLE.put(RpcProtocol.PROTOCOL_VERSION, 1, new Cmd1Decoder());
        DECODER_TABLE.put(RpcProtocol.PROTOCOL_VERSION, 2, new Cmd2Decoder());
    }

    static {
        ENCODER_TABLE.put(RpcProtocol.PROTOCOL_VERSION, 1, new Cmd1Encoder());
        ENCODER_TABLE.put(RpcProtocol.PROTOCOL_VERSION, 2, new Cmd2Encoder());
    }

    public static AbstractDecoder<ByteBuf> getDecoder(String version, int cmd) {
        return DECODER_TABLE.get(version, cmd);
    }


    public static AbstractEncoder<ByteBuf> getEncoder(String version, int cmd) {
        return ENCODER_TABLE.get(version, cmd);
    }
}
