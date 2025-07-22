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
public class VehicleDetailsRespDto {
	
	@SerializedName("vehicleNo")
	@Expose
	private String vehicleNo;
	
	@SerializedName("transDocNo")
	@Expose
	private String transDocNo;
	
	@SerializedName("transDocDate")
	@Expose
	private String transDocDate;
	
	@SerializedName("quantity")
	@Expose
	private Long quantity;

}
