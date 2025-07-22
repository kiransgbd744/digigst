package com.ey.advisory.app.dashboard.apiCall;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class APICAllDtls {
	
	@Expose
	@SerializedName("apiGstinDetails")
	private List<ApiGstinDetailsDto> apiGstinDetails;
	
	@Expose
	@SerializedName("scheduledDateAndTime")
	String scheduledDateAndTime;

}
