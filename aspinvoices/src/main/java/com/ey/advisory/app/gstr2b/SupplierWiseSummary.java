package com.ey.advisory.app.gstr2b;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class SupplierWiseSummary {
	
	@Expose
	@SerializedName("b2b")
	private List<B2BAndB2bASummary> b2bSummary;
	
	@Expose
	@SerializedName("b2ba")
	private List<B2BAndB2bASummary> b2bASummary;
	
	@Expose
	@SerializedName("cdnr")
	private List<CDNRAndCDNRASummary> cdnrSummary;
	
	@Expose
	@SerializedName("cdnra")
	private List<CDNRAndCDNRASummary> cdnrASummary;
	
	@Expose
	@SerializedName("isd")
	private List<ISDAndISDASummary> isdSummary;
	
	@Expose
	@SerializedName("isda")
	private List<ISDAndISDASummary> isdASummary;
	 
	@Expose
	@SerializedName("impgsez")
	private List<IMPGSEZSummary> impgSezSummary;
	
	@Expose
	@SerializedName("ecom")
	private List<EcomSummary> ecomSummary;
	
	@Expose
	@SerializedName("ecoma")
	private List<EcomSummary> ecomASummary;
	

}
