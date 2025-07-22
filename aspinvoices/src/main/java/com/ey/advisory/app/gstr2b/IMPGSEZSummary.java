package com.ey.advisory.app.gstr2b;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */
@Data
public class IMPGSEZSummary {
	
	@Expose
	@SerializedName("ctin")
	private String suppGstin;
	
	@Expose
	@SerializedName("trdnm")
	private String suppName;
	
	@Expose
	@SerializedName("portcode")
	private String portCode;
	
	@Expose
	@SerializedName("ttldocs")
	private Integer totalDoc;
	
	@Expose
	@SerializedName("igst")
	private BigDecimal igst = BigDecimal.ZERO;

	@Expose
	@SerializedName("txval")
	private BigDecimal totalTaxableVale = BigDecimal.ZERO;
	
	@Expose
	@SerializedName("cess")
	private BigDecimal cess = BigDecimal.ZERO;

}
