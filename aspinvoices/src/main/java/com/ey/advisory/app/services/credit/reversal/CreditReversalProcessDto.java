package com.ey.advisory.app.services.credit.reversal;

import java.math.BigDecimal;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class CreditReversalProcessDto {

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("authToken")
	private String authToken;

	@Expose
	@SerializedName("state")
	private String state;

	@Expose
	@SerializedName("status")
	private String status;
	
	@Expose
	@SerializedName("regType")
	private String regType;

	@Expose
	@SerializedName("dateTime")
	private String dateTime;

	@Expose
	@SerializedName("ratio1Ratio")
	private BigDecimal ratio1Ratio = BigDecimal.ZERO;

	@Expose
	@SerializedName("ratio1TotalTax")
	private BigDecimal ratio1TotalTax = BigDecimal.ZERO;

	@Expose
	@SerializedName("ratio1Igst")
	private BigDecimal ratio1Igst = BigDecimal.ZERO;

	@Expose
	@SerializedName("ratio1Sgst")
	private BigDecimal ratio1Sgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("ratio1Cgst")
	private BigDecimal ratio1Cgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("ratio1Cess")
	private BigDecimal ratio1Cess = BigDecimal.ZERO;

	@Expose
	@SerializedName("ratio2Ratio")
	private BigDecimal ratio2Ratio = BigDecimal.ZERO;

	@Expose
	@SerializedName("ratio2TotalTax")
	private BigDecimal ratio2TotalTax = BigDecimal.ZERO;

	@Expose
	@SerializedName("ratio2Igst")
	private BigDecimal ratio2Igst = BigDecimal.ZERO;

	@Expose
	@SerializedName("ratio2Sgst")
	private BigDecimal ratio2Sgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("ratio2Cgst")
	private BigDecimal ratio2Cgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("ratio2Cess")
	private BigDecimal ratio2Cess = BigDecimal.ZERO;

	@Expose
	@SerializedName("ratio3Ratio")
	private BigDecimal ratio3Ratio = BigDecimal.ZERO;

	@Expose
	@SerializedName("ratio3TotalTax")
	private BigDecimal ratio3TotalTax = BigDecimal.ZERO;

	@Expose
	@SerializedName("ratio3Igst")
	private BigDecimal ratio3Igst = BigDecimal.ZERO;

	@Expose
	@SerializedName("ratio3Sgst")
	private BigDecimal ratio3Sgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("ratio3Cgst")
	private BigDecimal ratio3Cgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("ratio3Cess")
	private BigDecimal ratio3Cess = BigDecimal.ZERO;
	
}
