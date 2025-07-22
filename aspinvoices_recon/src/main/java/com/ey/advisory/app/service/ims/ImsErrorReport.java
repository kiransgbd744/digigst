/**
 * 
 */
package com.ey.advisory.app.service.ims;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */
@Data
public class ImsErrorReport {

	@Expose
	@SerializedName("b2b")
	private List<ErrorDetail> b2b;

	@Expose
	@SerializedName("b2ba")
	private List<ErrorDetail> b2ba;

	@Expose
	@SerializedName("dn")
	private List<ErrorDetail> dn;

	@Expose
	@SerializedName("dna")
	private List<ErrorDetail> dna;

	@Expose
	@SerializedName("cn")
	private List<ErrorDetail> cn;

	@Expose
	@SerializedName("cna")
	private List<ErrorDetail> cna;
	
	@Expose
	@SerializedName(value = "ecom", alternate = { "e_b2b", "e_c2b" })
	private List<ErrorDetail> ecom;
	
	@Expose
	@SerializedName(value = "ecoma", alternate = { "e_b2ba", "e_c2ba" })
	private List<ErrorDetail> ecoma;
	

}
