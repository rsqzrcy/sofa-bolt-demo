package com.alipay.mfma.config;

public class Agreement {
	
	public static final String KEY_X1 = "e74fc0278d13c72eb1d7aa012e84d5b8";
	
	public static final int HEADER_SIZE = 2;
	
	public static final int VERSION_SIZE = 2;
	
	public static final int LENGHT_SIZE = 2;
	
	public static final int COMPANY_ID_SIZE = 8;
	
	public static final int KEYINDEX_SIZE = 1;
	
	public static final int CRC_SIZE = 2;
	
	public static final int ID_SIZE = 8;
	
	public static final int CMD_SIZE = 1;
	
	
	public enum KeyIndex {
		
		KeyIndex0(0, "KeyIndex1"),
		
		KeyIndex2(2, "KeyIndex2");
		
		private int key;
		
		private String value;
		
		
		KeyIndex(int key, String value) {
			this.key = key;
			this.value = value;
		}
		
		public int getKey() {
			return key;
		}
		
		public void setKey(int key) {
			this.key = key;
		}
		
		public String getValue() {
			return value;
		}
		
		public void setValue(String value) {
			this.value = value;
		}
		
		public static KeyIndex getByKey(int key) {
			for (KeyIndex keyIndex : values()) {
				if (keyIndex.getKey() == key) {
					return keyIndex;
				}
			}
			return null;
		}
		
	}
}
