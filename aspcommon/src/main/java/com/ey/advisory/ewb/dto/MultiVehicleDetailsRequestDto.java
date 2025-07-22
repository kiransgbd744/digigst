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
public class MultiVehicleDetailsRequestDto {
	
	@SerializedName("ewbNo")
	@Expose
	public Long ewbNo;
	
	@SerializedName("docNum")
	@Expose
	public String docNum;
	
	@SerializedName("suppGSTIN")
	@Expose
	public String suppGSTIN;
	
	

}
