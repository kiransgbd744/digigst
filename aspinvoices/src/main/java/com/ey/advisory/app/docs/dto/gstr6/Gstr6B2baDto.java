package com.ey.advisory.app.docs.dto.gstr6;

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
public class Gstr6B2baDto {

	@Expose
	@SerializedName("ctin")
	private String cgstin;

	@Expose
	@SerializedName("cfs")
	private String cfs;

	@Expose
	@SerializedName("inv")
	private List<Gstr6B2baInvoiceData> inv;
	
	@Expose
	@SerializedName("error_msg")
	private String error_msg;

	@Expose
	@SerializedName("error_cd")
	private String error_cd;

	

}
