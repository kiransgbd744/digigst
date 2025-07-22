package com.ey.advisory.app.inward.einvoice;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InwardEinvoiceInvMngtRespDto {

	@Expose
	public Long id;

	@Expose
	public String gstin;

	@Expose
	public String vendorGstin;
	
	@Expose
	public String docType;
	
	@Expose
	public String supplyType;
	
	@Expose
	public String docNo;

	@Expose
	public String docDate;
	
	@Expose
	public BigDecimal taxableVal = BigDecimal.ZERO;
	
	@Expose
	public BigDecimal totalTax = BigDecimal.ZERO;
	
	@Expose
	public BigDecimal igst = BigDecimal.ZERO;
	
	@Expose
	public BigDecimal cgst = BigDecimal.ZERO;
	
	@Expose
	public BigDecimal sgst = BigDecimal.ZERO;
	
	@Expose
	public BigDecimal cess = BigDecimal.ZERO;
	
	@Expose
	public BigDecimal totInvVal = BigDecimal.ZERO;
	
	@Expose
	public String irnNum;
	
	@Expose
	public String irnSts;
	@Expose
	public String ackNum;
	
	@Expose
	public String ackDt;
	
	@Expose
	public String ewbNo;
	
	@Expose
	public String ewbDt;
	
	@Expose
	public String cnclDt;
	
	@Expose
	public String irpName;

	@Expose
	private String qrCodeValidated;

	@Expose
	private String qrCodeValidationResult;

	@Expose
	private Integer qrCodeMatchCount;

	@Expose
	private Integer qrCodeMismatchCount;

	@Expose
	private String qrCodeMismatchAttributes;
	
	@Expose 
	private String prTagging;
	
	@Expose
	private String lastPrTagged;

}
