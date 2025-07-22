package com.ey.advisory.app.asprecon.gstr2.initiaterecon;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @author Rajesh N k
 *
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Gstr2InitiateReconReportDownloadDto {

	@Expose
	@SerializedName("partiCulars")
	private String partiCulars;

	@Expose
	@SerializedName("prCount")
	private String prCount;

	@Expose
	@SerializedName("prTaxableValue")
	private String prTaxableValue;

	@Expose
	@SerializedName("prIgst")
	private String prIgst;

	@Expose
	@SerializedName("prCgst")
	private String prCgst;

	@Expose
	@SerializedName("prSgst")
	private String prSgst;

	@Expose
	@SerializedName("prCess")
	private String prCess;

	@Expose
	@SerializedName("a2Count")
	private String a2Count;

	@Expose
	@SerializedName("a2TaxableValue")
	private String a2TaxableValue;

	@Expose
	@SerializedName("a2Igst")
	private String a2Igst;

	@Expose
	@SerializedName("a2Cgst")
	private String a2Cgst;

	@Expose
	@SerializedName("a2Sgst")
	private String a2Sgst;

	@Expose
	@SerializedName("a2Cess")
	private String a2Cess;

}
