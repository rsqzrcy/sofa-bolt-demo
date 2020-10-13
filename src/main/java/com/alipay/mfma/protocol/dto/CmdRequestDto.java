package com.alipay.mfma.protocol.dto;

public class CmdRequestDto extends IotRequest {
	
	private static final long serialVersionUID = -2838030577248868631L;
	
	private String data;
	
	public CmdRequestDto(String data) {
		this.data = data;
	}
	
	public String getData() {
		return data;
	}
	
	public void setData(String data) {
		this.data = data;
	}
}
