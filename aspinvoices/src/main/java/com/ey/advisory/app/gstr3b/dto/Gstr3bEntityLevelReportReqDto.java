package com.ey.advisory.app.gstr3b.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Rajesh N K
 *
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Gstr3bEntityLevelReportReqDto {
	
	
	@Expose
	@SerializedName("gstinList")
	private List<String> gstinList = new ArrayList<>();
	
	@Expose
	@SerializedName("entityId")
	String entityId;
	
	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;
	

}
