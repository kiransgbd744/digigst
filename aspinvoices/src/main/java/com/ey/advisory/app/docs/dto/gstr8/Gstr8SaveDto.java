package com.ey.advisory.app.docs.dto.gstr8;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Siva.Reddy This Dto is used for Gstr8 to Serialize and DeSerialize
 *         the Data
 */

@Data
public class Gstr8SaveDto {
	@SerializedName("gstin")
	@Expose
	private String gstin;

	@SerializedName("fp")
	@Expose
	private String fp;

	@SerializedName("tcs")
	@Expose
	private List<TcsDto> tcs;

	@SerializedName("tcsa")
	@Expose
	private List<TcsaDto> tcsa;

	@SerializedName("urd")
	@Expose
	private List<UrdDto> urd;

	@SerializedName("urda")
	@Expose
	private List<UrdaDto> urda;

	@Expose
	@SerializedName("error_report")
	Gstr8ErrorReport gstr8ErrorReport;

}
