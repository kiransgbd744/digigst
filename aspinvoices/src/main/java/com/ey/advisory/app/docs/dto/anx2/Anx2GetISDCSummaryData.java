package com.ey.advisory.app.docs.dto.anx2;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Dibyakanta.sahoo
 *
 */

public class Anx2GetISDCSummaryData {
	
	
	@Expose
	@SerializedName("id")
	private Long id;

	@Expose
	@SerializedName("secnm")
	private String secnm;

	@Expose
	@SerializedName("chksum")
	private String chksum;

	@Expose
	@SerializedName("ttldoc")
	private int totalDoc;

	@Expose
	@SerializedName("ttlval")
	private int totalVal;

	@Expose
	@SerializedName("nettax")
	private BigDecimal netTax ;

	@Expose
	@SerializedName("ttligst")
	private BigDecimal totalIgst ;

	@Expose
	@SerializedName("ttlcgst")
	private BigDecimal totalCgst ;

	@Expose
	@SerializedName("ttlsgst")
	private BigDecimal totalSgst ;
	
	@Expose
	@SerializedName("ttltxval")
	private int totaltaxVal;


}
