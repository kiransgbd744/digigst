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
public class DocDetails {
	
	@Expose
	@SerializedName("b2b")
	private List<B2BDocuments> b2bSummary;
	
	@Expose
	@SerializedName("b2ba")
	private List<B2BADocuments> b2bASummary;
	
	@Expose
	@SerializedName("cdnr")
	private List<CDNRDocuments> cdnrSummary;
	
	@Expose
	@SerializedName("cdnra")
	private List<CDNRADocuments> cdnrASummary;
	
	@Expose
	@SerializedName("isd")
	private List<ISDDocuments> isdSummary;
	
	@Expose
	@SerializedName("isda")
	private List<ISDADocuments> isdASummary;
	
	@Expose
	@SerializedName("impg")
	private List<IMPGDocuments> impgSummary;
	 
	@Expose
	@SerializedName("impgsez")
	private List<IMPGSEZDocuments> impgSezSummary;
	
	@Expose
	@SerializedName("ecom")
	private List<EcomDocuments> ecomSummary;
	
	@Expose
	@SerializedName("ecoma")
	private List<EcomADocuments> ecomASummary;
	

}
