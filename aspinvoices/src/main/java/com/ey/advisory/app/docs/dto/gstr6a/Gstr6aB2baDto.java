package com.ey.advisory.app.docs.dto.gstr6a;

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
public class Gstr6aB2baDto {

	@Expose
	@SerializedName("inv")
	private List<Gstr6aB2baInvoiceData> inv;

	//Cfs has been moved to inv node.
	/*@Expose
	@SerializedName("cfs")
	private String cfs;*/

	@Expose
	@SerializedName("ctin")
	private String ctin;



}
