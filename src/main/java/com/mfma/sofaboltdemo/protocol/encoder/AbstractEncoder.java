package com.mfma.sofaboltdemo.protocol.encoder;


import com.mfma.sofaboltdemo.protocol.dto.IotResponse;

public abstract class AbstractEncoder<T> {

    /**
     * 解码
     *
     * @param t 需要解码的源数据
     * @return
     * @throws Exception
     */
    public abstract IotResponse encoder(T t) throws Exception;

}

