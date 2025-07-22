/**
 * 
 */
package com.ey.advisory.app.docs.dto.anx2;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Hemasundar.J
 *
 */
@Data
public class SaveAnx2 {

	@Expose
	@SerializedName("gstin")
	private String cgstin;

	@Expose
	@SerializedName("rtnprd")
	private String taxperiod;

	@Expose
	@SerializedName("b2b")
	private List<Anx2Data> b2bInvoice;

	@Expose
	@SerializedName("b2ba")
	private List<Anx2Data> b2baInvoice;

	@Expose
	@SerializedName("sezwp")
	private List<Anx2Data> sezwpInvoice;
	
	@Expose
	@SerializedName("sezwpa")
	private List<Anx2Data> sezwpaInvoice;

	@Expose
	@SerializedName("sezwop")
	private List<Anx2Data> sezwopInvoice;
	
	@Expose
	@SerializedName("sezwopa")
	private List<Anx2Data> sezwopaInvoice;

	@Expose
	@SerializedName("de")
	private List<Anx2Data> deInvoice;

	@Expose
	@SerializedName("dea")
	private List<Anx2Data> deaInvoice;

}
