package com.alipay.mfma.protocol.decoder;

import com.alipay.mfma.protocol.dto.IotRequest;

public abstract class AbstractDecoder<T> {
	
	/**
	 * 解码
	 *
	 * @param t 需要解码的源数据
	 * @return
	 * @throws Exception
	 */
	public abstract IotRequest decoder(T t) throws Exception;
	
}

