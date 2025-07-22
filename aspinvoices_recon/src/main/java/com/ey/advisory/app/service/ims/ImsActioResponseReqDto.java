/**
 * 
 */
package com.ey.advisory.app.service.ims;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class ImsActioResponseReqDto {
	
	@Expose
	private List<String> imsUniqueId = new ArrayList<>();
	
	@Expose
	private String actionTaken;
	
	@Expose
	private String responseRemark;
	
	@Expose
	private String entityId;

	@Expose
	private String records;
	
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
	
	@Expose
	@SerializedName("imsUniqueIds")
	private List<String> imsUniqueIds;

	@Expose
	@SerializedName("itcRedRequired")
	private String itcRedRequired;

	@Expose
	@SerializedName("declIgst")
	private String declIgst;

	@Expose
	@SerializedName("declSgst")
	private String declSgst;

	@Expose
	@SerializedName("declCgst")
	private String declCgst;

	@Expose
	@SerializedName("declCess")
	private String declCess;
}
