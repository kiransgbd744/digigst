package com.ey.advisory.app.docs.dto.gstr7;

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
public class Gstr7GetInvoicesReqDto {

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

	// This is should not be null for Gstr7 Get call dependent class is
	// (Gstr7GetApiJobInsertionController)
	@Expose
	@SerializedName("gstr7Sections")
	private List<String> gstr7Sections = new ArrayList<String>();

	@Expose
	private Long userRequestId;

}
