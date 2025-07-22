package com.ey.advisory.app.common.cf;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DestinationCredentials {
	
	@SerializedName("clientid")
	private String clientid;
	
	@SerializedName("clientsecret")
	private String clientsecret;
	
	@SerializedName("uri")
	private String uri;

}
