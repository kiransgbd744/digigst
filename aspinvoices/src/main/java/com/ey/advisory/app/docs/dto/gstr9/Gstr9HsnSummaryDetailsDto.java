package com.ey.advisory.app.docs.dto.gstr9;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Rajesh N K
 *
 */
@Data
public class Gstr9HsnSummaryDetailsDto {
	
	@SerializedName("gstr9Table17ListDto")
	@Expose
	List<Gstr9Table17ItemsReqDto> gstr9Table17ListDto;
	
	@SerializedName("gstin")
	@Expose
	String gstin;
	
	@SerializedName("fy")
	@Expose
	String fy;
	
	@SerializedName("type")
	@Expose
	String type;
	
	@SerializedName("entityId")
	@Expose
	Long entityId;
	
	@SerializedName("listGstins")
	@Expose
	List<String> listGstins;

}
