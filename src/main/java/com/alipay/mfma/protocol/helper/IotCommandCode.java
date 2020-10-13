package com.alipay.mfma.protocol.helper;

import com.alipay.remoting.CommandCode;

public enum IotCommandCode implements CommandCode {
	
	HEARTBEAT_VALUE((short) 0), CMD1((short) 1), CMD2((short) 2);
	
	private short value;
	
	IotCommandCode(short value) {
		this.value = value;
	}
	
	@Override
	public short value() {
		return this.value;
	}
	
	public static IotCommandCode valueOf(short value) {
		switch (value) {
			case 0:
				return HEARTBEAT_VALUE;
			case 1:
				return CMD1;
			case 2:
				return CMD2;
		}
		throw new IllegalArgumentException("Unknown Rpc command code value: " + value);
	}
}
