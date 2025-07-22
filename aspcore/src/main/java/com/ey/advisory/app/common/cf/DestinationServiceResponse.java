package com.ey.advisory.app.common.cf;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DestinationServiceResponse {
	
	@SerializedName(value = "destinationConfiguration")
	private DestinationConfig destinationConfig;
	
}
