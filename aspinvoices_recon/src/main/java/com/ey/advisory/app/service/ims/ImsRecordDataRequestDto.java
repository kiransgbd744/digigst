package com.ey.advisory.app.service.ims;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Vishal.Verma
 *
 */

@Data
public class ImsRecordDataRequestDto {

	@Expose
	@SerializedName("entityId")
	public Long entityId;
	
	@Expose
	@SerializedName("gstins")
	public List<String> gstins;

	@Expose
	@SerializedName("vendorPans")
	public List<String> vendorPans;
	
	@Expose
	@SerializedName("vendorGstins")
	public List<String> vendorGstins;
	
	@Expose
	@SerializedName("tableTypes")
	public List<String> tableTypes;
	
	@Expose
	@SerializedName("docTypes")
	private List<String> docTypes;
	
	@Expose
	@SerializedName("docNumbers")
	private List<String> docNumbers;
	
	@Expose
	@SerializedName("imsUniqueIds")
	private List<String> imsUniqueIds;
	
	@Expose
	@SerializedName("toDocDate")
	private String toDocDate;
	
	@Expose
	@SerializedName("fromDocDate")
	private String fromDocDate;
	
	@Expose
	@SerializedName("actionDigiGsts")
	private List<String> actionDigiGsts;
	
	@Expose
	@SerializedName("actionGstns")
	private List<String> actionGstns;
}
