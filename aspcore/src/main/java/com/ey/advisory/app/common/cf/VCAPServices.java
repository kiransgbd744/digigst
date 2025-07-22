package com.ey.advisory.app.common.cf;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class VCAPServices {
	
	@SerializedName("hana")
	private List<HANADBConfig> hanaConfigs;
	
	@SerializedName("xsuaa")
	private List<XsuaaService> xsuaServices;
	
	@SerializedName("connectivity")
	private List<ConnectivityService> connectivityServices;
	
	@SerializedName("destination")
	private List<DestinationService> destinationServices;
	

}
