/**
 * 
 */
package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Hemasundar.J
 *
 */
@Data
public class Anx1GetInvoicesReqDto {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("rtnprd")
	private String returnPeriod;

	@Expose
	@SerializedName("doc_action")
	private String action;// = APIConstants.N;

	@Expose
	@SerializedName("from_time")
	private String fromTime;

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

}
