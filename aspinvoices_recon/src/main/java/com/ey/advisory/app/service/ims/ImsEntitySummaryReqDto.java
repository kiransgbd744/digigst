package com.ey.advisory.app.service.ims;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author ashutosh.kar
 *
 */

@Data
public class ImsEntitySummaryReqDto {

	@Expose
	@SerializedName("entityId")
	public Long entityId;
	
	@Expose
	@SerializedName("gstin")
	public String gstin;

	@Expose
	@SerializedName("gstins")
	public List<String> gstins;
	
	@Expose
	@SerializedName("tableType")
	public List<String> tableType;
	
	@Expose
	@SerializedName("type")
	private String type;
	
	@Expose
	@SerializedName("docKey")
	private String docKey;
	
}
