package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Mahesh.Golla
 *
 */
@Data
public class Gstr7HeaderReqDto {

	@Expose
	@SerializedName("deductrGstin")
	private String sgstin;

	@Expose
	@SerializedName("retPrd")
	private String returnPeriod;
	
	@Expose
	@SerializedName("dataOriginTypeCode")
	protected String dataOriginTypeCode;

	@Expose
	@SerializedName("docs")
	private List<Gstr7ItemReqDto> gstr7Invoices;
}
