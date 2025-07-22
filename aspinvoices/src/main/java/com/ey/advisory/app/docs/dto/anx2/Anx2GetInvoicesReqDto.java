package com.ey.advisory.app.docs.dto.anx2;

import com.ey.advisory.core.api.APIConstants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Dibyakanta.Sahoo
 *
 */
@Data
public class Anx2GetInvoicesReqDto {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("rtnprd")
	private String returnPeriod;

	@Expose
	@SerializedName("from_time")
	private String fromTime;

	@Expose
	@SerializedName("action")
	private String action = APIConstants.N;

	@Expose
	@SerializedName("ctin")
	private String ctin;
	
	@Expose
	@SerializedName("etin")
	private String etin;

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
	
	//This is the Id from REQUEST_ID_WISE table
	@Expose
	@SerializedName("requestId")
	private Long requestId;

}
