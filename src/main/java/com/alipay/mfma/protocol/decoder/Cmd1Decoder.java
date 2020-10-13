package com.alipay.mfma.protocol.decoder;

import com.alipay.mfma.protocol.dto.CmdRequestDto;
import com.alipay.mfma.protocol.dto.IotRequest;
import com.alipay.mfma.protocol.util.NettyUtils;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Cmd1Decoder extends AbstractDecoder<ByteBuf> {
	
	
	@Override
	public IotRequest decoder(ByteBuf in) throws Exception {
		String data = NettyUtils.getHexString(in, 8);
		log.info("package data=[" + data + "]");
		return new CmdRequestDto(data);
	}
}
