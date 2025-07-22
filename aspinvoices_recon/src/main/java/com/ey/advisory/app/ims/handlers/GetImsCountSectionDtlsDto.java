package com.ey.advisory.app.ims.handlers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GetImsCountSectionDtlsDto {

	//need variables names with serializable name as noaction, reject, pending, accept, total, gstin, goodstype, isdelete, batchid, createdby, createddate, section
	@Expose
	@SerializedName("noaction")
	private String noAction;

	@Expose
	@SerializedName("reject")
	private String reject;

	@Expose
	@SerializedName("pending")
	private String pending;

	@Expose
	@SerializedName("accept")
	private String accept;

	@Expose
	@SerializedName("total")
	private String total;
}
