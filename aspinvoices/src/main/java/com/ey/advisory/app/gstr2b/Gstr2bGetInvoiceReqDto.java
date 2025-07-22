package com.ey.advisory.app.gstr2b;

import com.ey.advisory.core.api.APIConstants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */
@Data
public class Gstr2bGetInvoiceReqDto {
	
	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("ret_period")
	private String returnPeriod;

	@Expose
	@SerializedName("action_required")
	private String action = APIConstants.N;

	@Expose
	@SerializedName("ctin")
	private String ctin;

	@Expose
	@SerializedName("from_time")
	private String fromTime;

	// Extra Columns used for further processing in the App layer.

	@Expose
	@SerializedName("type")
	private String type;

	@Expose
	@SerializedName("apiSection")
	private String apiSection = APIConstants.GSTR2B;

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
	private String prevGenDt;
	
	@Expose
	private String prevCheckSum;
	
	@Expose
	private boolean isDataChanged;
	
	@Expose
	private boolean isTokenResp;

}


