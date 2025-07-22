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
public class IMPGDocuments {

	@Expose
	@SerializedName("refdt")
	private String iCEGATEReferenceDate;

	@Expose
	@SerializedName("recdt")
	private String recvDateInGst;

	@Expose
	@SerializedName("portcode")
	private String portCode;

	@Expose
	@SerializedName("boenum")
	private String billOfEntityNumber;

	@Expose
	@SerializedName("boedt")
	private String billOfEntityDate;

	@Expose
	@SerializedName("isamd")
	private String isAmended;

	@Expose
	@SerializedName("txval")
	private BigDecimal totalTaxableVal = BigDecimal.ZERO;

	@Expose
	@SerializedName("igst")
	private BigDecimal igst = BigDecimal.ZERO;

	@Expose
	@SerializedName("cess")
	private BigDecimal cess = BigDecimal.ZERO;

}
