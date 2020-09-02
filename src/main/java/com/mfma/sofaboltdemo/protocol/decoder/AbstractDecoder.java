package com.mfma.sofaboltdemo.protocol.decoder;

import com.mfma.sofaboltdemo.protocol.dto.IotRequest;

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

