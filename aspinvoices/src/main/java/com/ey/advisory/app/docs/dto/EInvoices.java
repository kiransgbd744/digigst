package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Anand3.M
 *
 */
@Data
public class EInvoices {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("fp")
	private String filePeriod;

	@Expose
	@SerializedName("b2b")
	private List<B2BInvoices> b2bInvoice;

	@Expose
	@SerializedName("cdnur")
	private List<CDNURInvoices> cdnurInvoice;

	@Expose
	@SerializedName("exp")
	private List<EXPInvoices> expInvoice;

	@Expose
	@SerializedName("cdnr")
	private List<CDNRInvoices> cdnrInvoice;

}
