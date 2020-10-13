package com.alipay.mfma.protocol.dto;

import com.alipay.remoting.rpc.RpcCommand;

import java.util.Objects;

public class IotRequest extends RpcCommand {
	
	private static final long serialVersionUID = -6479222417479222710L;
	
	private String header;
	
	private String version;
	
	private String companyId;
	
	private int keyIndex;
	
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
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		IotRequest that = (IotRequest) o;
		return getKeyIndex() == that.getKeyIndex() && getHeader().equals(that.getHeader()) && getVersion().equals(that.getVersion()) && getCompanyId().equals(that.getCompanyId());
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.getHeader() == null || this.getVersion() == null || this.getCompanyId() == null) ? 0 : Objects.hash(getHeader(), getVersion(), getCompanyId(), getKeyIndex()));
		return result;
	}
}
