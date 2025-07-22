package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Umesha.M
 *
 */
@Data
public class EXPInvoices {

	@Expose
	@SerializedName("exp_typ")
	private String exportType; 
	
	@Expose
	@SerializedName("error_msg")
	private String error_msg;

	@Expose
	@SerializedName("error_cd")
	private String error_cd;
	
	@Expose
	@SerializedName("inv")
	private List<EXPInvoiceData> expInvoiceData;

}
