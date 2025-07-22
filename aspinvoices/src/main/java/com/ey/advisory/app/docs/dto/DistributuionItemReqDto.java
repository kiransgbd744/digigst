/**
 * 
 */
package com.ey.advisory.app.docs.dto;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Siva
 *
 */
@Data
public class DistributuionItemReqDto {

	@Expose
	@SerializedName("isdGstin")
	private String isdGstin;

	@Expose
	@SerializedName("returnPeriod")
	private String returnPeriod;

	@Expose
	@SerializedName("recipientGstin")
	private String recipientGstin;

	@Expose
	@SerializedName("stateCode")
	private String stateCode;

	@Expose
	@SerializedName("orgRecptGstin")
	private String orgRecptGstin;

	@Expose
	@SerializedName("orgStateCode")
	private String orgStateCode;

	@Expose
	@SerializedName("documentType")
	private String documentType;

	@Expose
	@SerializedName("supplyType")
	private String supplyType;

	@Expose
	@SerializedName("documentNumber")
	private String documentNumber;

	@Expose
	@SerializedName("documentDate")
	private String documentDate;

	@Expose
	@SerializedName("orgDocNum")
	private String orgDocNum;

	@Expose
	@SerializedName("orgDocDate")
	private String orgDocDate;

	@Expose
	@SerializedName("orgCdtnoteNum")
	private String orgCdtnoteNum;

	@Expose
	@SerializedName("orgCdtnoteDt")
	private String orgCdtnoteDt;

	
	
	@Expose
	@SerializedName("elgIndicator")
	private String elgIndicator;

	@Expose
	@SerializedName("igstAsIgst")
	private String igstAsIgst;

	@Expose
	@SerializedName("igstAsSgst")
	private String igstAsSgst;

	@Expose
	@SerializedName("igstAsCgst")
	private String igstAsCgst;

	@Expose
	@SerializedName("sgstAsSgst")
	private String sgstAsSgst;

	@Expose
	@SerializedName("sgstAsIgst")
	private String sgstAsIgst;

	@Expose
	@SerializedName("cgstAsCgst")
	private String cgstAsCgst;

	@Expose
	@SerializedName("cgstAsIgst")
	private String cgstAsIgst;

	@Expose
	@SerializedName("cessAmount")
	private String cessAmount;
}
