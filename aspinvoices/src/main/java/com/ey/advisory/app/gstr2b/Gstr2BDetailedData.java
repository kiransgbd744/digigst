package com.ey.advisory.app.gstr2b;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class Gstr2BDetailedData {
	
	@Expose
	@SerializedName("gstin")
	private String rGstin;
	
	@Expose
	@SerializedName("rtnprd")
	private String taxpeiod;
	
	@Expose
	@SerializedName("version")
	private String version;
	
	@Expose
	@SerializedName("gendt")
	private String genDate;
	
	@Expose
	@SerializedName("itcsumm")
	private ITCSummary  itcSummary;
	
	@Expose
	@SerializedName("cpsumm")
	private SupplierWiseSummary suppSummary;
	
	@Expose
	@SerializedName("docdata")
	private DocDetails docDetails;
			
	@Expose
	@SerializedName("fc")
	private Integer fileCount;
	
	@Expose
	@SerializedName("docRejdata")
	private DocDetails docRejdata;
	
	@Expose
	@SerializedName("cpSummRej")
	private RejectedSupplierWiseSummary RejSuppSummary;
	
	
	

}
