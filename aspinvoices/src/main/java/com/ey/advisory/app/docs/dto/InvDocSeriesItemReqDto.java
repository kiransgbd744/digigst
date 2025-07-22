/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Laxmi.Salukuti
 *
 */
@Data
public class InvDocSeriesItemReqDto {

	@Expose
	@SerializedName("sgstin")
	private String sgstin;

	@Expose
	@SerializedName("retPrd")
	private String returnPeriod;

	@Expose
	@SerializedName("sNo")
	private String serialNum;

	@Expose
	@SerializedName("natOfDoc")
	private String natureOfDoc;

	@Expose
	@SerializedName("from")
	private String fromNum;

	@Expose
	@SerializedName("to")
	private String toNum;

	@Expose
	@SerializedName("totNum")
	private String totalNum;

	@Expose
	@SerializedName("cancelled")
	private String cancelled;

	@Expose
	@SerializedName("netNum")
	private String netNum;

}
