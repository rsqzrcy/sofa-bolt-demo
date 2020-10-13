package com.alipay.mfma.protocol.dto;

import java.io.Serializable;

public class IotResponse implements Serializable {
	
	private static final long serialVersionUID = -6479222417479222710L;
	
	private String header;
	
	private String version;
	
	private String companyId;
	
	private int keyIndex;
	
	private int cmd;
	
	public String getHeader() {
		return header;
	}
	
	public void setHeader(String header) {
		this.header = header;
	}
	
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public int getKeyIndex() {
		return keyIndex;
	}
	
	public void setKeyIndex(int keyIndex) {
		this.keyIndex = keyIndex;
	}
	
	public String getCompanyId() {
		return companyId;
	}
	
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	
	public int getCmd() {
		return cmd;
	}
	
	public void setCmd(int cmd) {
		this.cmd = cmd;
	}
}
