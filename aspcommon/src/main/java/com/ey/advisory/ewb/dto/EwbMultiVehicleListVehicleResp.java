package com.ey.advisory.ewb.dto;

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
public class EwbMultiVehicleListVehicleResp {
	
	@SerializedName("vehicleNo")
	@Expose
	public String vehicleNo;
	
	@SerializedName("transDocNo")
	@Expose
	public String transDocNo;

}
