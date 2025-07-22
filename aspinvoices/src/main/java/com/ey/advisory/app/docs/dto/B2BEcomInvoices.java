package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Data
public class B2BEcomInvoices {

	@Expose
	@SerializedName("rtin")
	private String rtin;

	@Expose
	@SerializedName("stin")
	private String stin;

	@Expose
	@SerializedName("inv")
	private List<B2BInvoiceData> b2bInvoiceData;

}
