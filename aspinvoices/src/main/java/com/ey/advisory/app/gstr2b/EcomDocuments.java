package com.ey.advisory.app.gstr2b;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Ravindra V S
 *
 */
@Data
public class EcomDocuments {
	
	@Expose
	@SerializedName("ctin")
	private String suppGstin;
	
	@Expose
	@SerializedName("trdnm")
	private String suppName;
	
	@Expose
	@SerializedName("supfildt")
	private String gstr1FilingDate;
	
	@Expose
	@SerializedName("supprd")
	private String gstr1FilingPeriod;
	
	@Expose
	@SerializedName("inv")
	private List<EcomInvoiceList> ecomInvoiceData;

}
