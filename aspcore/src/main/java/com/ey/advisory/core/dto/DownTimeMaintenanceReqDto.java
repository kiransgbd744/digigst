package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author ashutosh.kar
 * 
 * 
 */

@Setter
@Getter
@ToString
public class DownTimeMaintenanceReqDto {
	
	@Expose
	@SerializedName("startTime")
	private String startTime;
	
	@Expose
	@SerializedName("endTime")
	private String endTime;
	
	@Expose
	@SerializedName("downTimemsg")
	private String downTimemsg;
	
	@Expose
	@SerializedName("flag")
	private boolean flag;
	
}
