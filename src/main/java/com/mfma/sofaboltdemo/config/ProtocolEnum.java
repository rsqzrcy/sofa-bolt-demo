package com.mfma.sofaboltdemo.config;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/**
 * 协议定义
 * @author mfma
 */
public class ProtocolEnum {


    private static final Table<Byte, String, String> PROTOCOLS = HashBasedTable.create();

    static {
        PROTOCOLS.put((byte) 1, "FDFC", "030H");
    }

}
