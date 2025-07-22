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
public class EwbMultiVehicleGetFromAndToDto {
	
	@SerializedName("fromState")
	@Expose
	public String fromState;
	
	@SerializedName("fromPlace")
	@Expose
	public String fromPlace;
	
	@SerializedName("toState")
	@Expose
	public String toState;
	
	@SerializedName("toPlace")
	@Expose
	public String toPlace;

}
