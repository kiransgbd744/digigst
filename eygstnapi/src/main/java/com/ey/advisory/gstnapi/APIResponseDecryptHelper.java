package com.ey.advisory.gstnapi;

public interface APIResponseDecryptHelper {
	
	String decrypt(String encRespJson, String rek,
			String sk);

}
