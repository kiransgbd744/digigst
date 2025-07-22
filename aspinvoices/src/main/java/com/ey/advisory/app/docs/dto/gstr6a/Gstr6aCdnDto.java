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
public class Gstr6aCdnDto {
 
	//cfs has been moved to nt node.
	/*@Expose
	@SerializedName("cfs")
	private String cfs;*/

	@Expose
	@SerializedName("nt")
	private List<Gstr6aCdnInvoiceData> nt;

	@Expose
	@SerializedName("ctin")
	private String ctin;

	

}
