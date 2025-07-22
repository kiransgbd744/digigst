package com.ey.advisory.ewb.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Rajesh N K
 *
 */
@Setter
@Getter
@ToString
public class EwbMultiVehicleDetailsDto {
	
	@SerializedName("ewbDetails")
	@Expose
	private List<EwbMultiVehicleDetailsRespDto> ewbDetails;

}
