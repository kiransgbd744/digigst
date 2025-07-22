package com.ey.advisory.app.data.services.drc;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class DrcGetCompSummaryDetails {

	@SerializedName("refid")
	@Expose
	private String refId;

	@SerializedName("rtnprd")
	@Expose
	private String rtnprd;

	@SerializedName("gstin")
	@Expose
	private String gstin;

	@SerializedName("authStatus")
	@Expose
	private String authStatus;

	@SerializedName("regType")
	@Expose
	private String regType;

	@SerializedName("stateName")
	@Expose
	private String stateName;

	@SerializedName("mismatch")
	@Expose
	private List<DifferenceAmtDetails> mismatchDetails;

	@SerializedName(value = "diffAmtDetails", alternate = { "diff" })
	@Expose
	private DifferenceAmtDetails diffAmtDetails;
	
	@SerializedName("reasons")
	@Expose
	private List<Reason> reasons;

	@SerializedName("getCallStatus")
	@Expose
	private String getCallStatus;

	@SerializedName("getCallTime")
	@Expose
	private String getCallTime;

	@SerializedName("saveStatus")
	@Expose
	private String saveStatus;

	@SerializedName("saveStatusTime")
	@Expose
	private String saveStatusTime;

	@SerializedName("filingStatus")
	@Expose
	private String filingStatus;

	@SerializedName("filingStatusTime")
	@Expose
	private String filingStatusTime;

	@SerializedName(value = "differentialDetails", alternate = { "drc03arn" })
	@Expose
	private List<DifferentialDetail> differentialDetailsList;

	@SerializedName("reasonUsr")
	@Expose
	private List<UsrReasonList> reasonUsr;

	@SerializedName("status")
	@Expose
	private String status;

	@SerializedName("profile")
	@Expose
	private String profile;
	
	@SerializedName(value = "suppDocDetails", alternate = { "suppdoc" })
	@Expose
	private SuppDocdetailsDto suppDocDetails;

}
