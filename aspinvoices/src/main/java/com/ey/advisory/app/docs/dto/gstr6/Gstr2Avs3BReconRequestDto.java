package com.ey.advisory.app.docs.dto.gstr6;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * 
 * @author kiran s
 *
 */
@Setter
@Getter
@ToString
public class Gstr2Avs3BReconRequestDto {
	@Expose
	@SerializedName(value = "entity", alternate = "entityId")
	private Long entityId;

	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;

	@Expose
	@SerializedName("taxPeriodTo")
	private String taxPeriodTo;
	@Expose
	@SerializedName("taxPeriodFrom")
	private String taxPeriodFrom;

	@Expose
	@SerializedName("gstin")
	private List<String> gstin;	
	
	@Expose
	@SerializedName("isdGstin")
	private List<String> isdGstin;
	
	@Expose
	@SerializedName("tableType")
	private List<String> tableType;
	
	@Expose
	@SerializedName("type")
	private String type;
	
	@Expose
	@SerializedName("id")
	private List<Integer> id;
	
	@Expose
	@SerializedName("fromPeriod")
	private String fromPeriod;
	
	@Expose
	@SerializedName("unSavedGstins")
	private List<String> unSavedGstins;
	
	@Expose
	@SerializedName("gstins")
	private List<String> gstins;
	
	@Expose
	@SerializedName("activeGstinsList")
	private List<String> activeGstinsList;
	
	@Expose
	@SerializedName("batchId")
	private Long batchId;


}
