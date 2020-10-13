package com.alipay.mfma.protocol.encoder;

import com.alipay.mfma.protocol.dto.CmdResponseDto;
import com.alipay.mfma.protocol.dto.IotResponse;
import com.alipay.mfma.protocol.util.NettyUtils;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Cmd2Encoder extends AbstractEncoder<ByteBuf> {
	
	
	@Override
	public IotResponse encoder(ByteBuf in) throws Exception {
		String data = NettyUtils.getHexString(in, 8);
		log.info("package data=[" + data + "]");
		return new CmdResponseDto(data);
	}
}
