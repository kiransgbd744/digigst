package com.ey.advisory.core.dto;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Anand3.M
 *
 */
@Data
public class Itc04GetInvoicesReqDto {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("ret_period")
	private String returnPeriod;

	@Expose
	@SerializedName("action_required")
	private String action;// = APIConstants.N;

	@Expose
	@SerializedName("ctin")
	private String ctin;

	@Expose
	@SerializedName("from_time")
	private String fromTime;

	@Expose
	@SerializedName("rec_type")
	private String recType;

	// Extra Columns used for further processing in the App layer.

	@Expose
	@SerializedName("type")
	private String type;

	@Expose
	@SerializedName("apiSection")
	private String apiSection;

	@Expose
	@SerializedName("groupCode")
	private String groupcode;

	@Expose
	@SerializedName("batchId")
	private Long batchId;

	@Expose
	@SerializedName("isFailed")
	private Boolean isFailed;
	
	@Expose
	@SerializedName("fromPeriod")
	private String fromPeriod;
	
	@Expose
	@SerializedName("toPeriod")
	private String toPeriod;

	// This is should not be null for Itc04 Get call dependent class is
	// (Itc04GetApiJobInsertionController)
	@Expose
	@SerializedName("itc04Sections")
	private List<String> itc04Sections = new ArrayList<String>();

	@Expose
	private Long userRequestId;

}
