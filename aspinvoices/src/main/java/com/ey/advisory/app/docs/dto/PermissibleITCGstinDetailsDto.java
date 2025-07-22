package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.ey.advisory.app.dashboard.apiCall.ApiGstinDetailsDto;
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
public class PermissibleITCGstinDetailsDto {
	
	@Expose
	@SerializedName("itcTenPercGstinDetails")
	private List<PermissibleITC10PerGstinDetailsDto> itcTenPercGstinDetails;

}
