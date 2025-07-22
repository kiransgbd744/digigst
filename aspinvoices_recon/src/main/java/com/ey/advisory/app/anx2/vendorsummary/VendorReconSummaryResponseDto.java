package com.ey.advisory.app.anx2.vendorsummary;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class VendorReconSummaryResponseDto {
	
	@Expose
	private String level;
	
	@Expose
	private String vendorPan;

	@Expose
	private String vendorName;

	@Expose
	private BigDecimal rating = BigDecimal.ZERO;

	@Expose
	private String gstin;

	@Expose
	private String docType;

	@Expose
	private BigDecimal prMatchPer = BigDecimal.ZERO;

	@Expose
	private BigDecimal prMatchAmt = BigDecimal.ZERO;

	@Expose
	private BigDecimal anx2MatchAmt = BigDecimal.ZERO;

	@Expose
	private BigDecimal prMissMatPer = BigDecimal.ZERO;

	@Expose
	private BigDecimal prMissMatAmt = BigDecimal.ZERO;

	@Expose
	private BigDecimal anxrMisMatAmt = BigDecimal.ZERO;

	@Expose
	private BigDecimal prProbPer = BigDecimal.ZERO;

	@Expose
	private BigDecimal prProbMat = BigDecimal.ZERO;

	@Expose
	private BigDecimal anx2ProbMat = BigDecimal.ZERO;

	@Expose
	private BigDecimal prForcMatPer = BigDecimal.ZERO;

	@Expose
	private BigDecimal prForcMat = BigDecimal.ZERO;

	@Expose
	private BigDecimal anx2ForcMat = BigDecimal.ZERO;

	@Expose
	private BigDecimal addanx2Per = BigDecimal.ZERO;

	@Expose
	private BigDecimal addAnx2 = BigDecimal.ZERO;

	@Expose
	private BigDecimal addPrPer = BigDecimal.ZERO;

	@Expose
	private BigDecimal addPr = BigDecimal.ZERO;

	@Expose
	private String fillStatus;
	
	@Expose
	private int count = 0;
	
	
	public VendorReconSummaryResponseDto() {

	}

	public VendorReconSummaryResponseDto(String level, String vendorPan
		    ,String cgstin, String taxDocType,
			BigDecimal prMatchAmt, BigDecimal anx2MatchAmt,
			BigDecimal prMissMatAmt, BigDecimal anxrMisMatAmt,
			BigDecimal prProbMat,BigDecimal anx2ProbMat,BigDecimal prForcMat,
			BigDecimal anx2ForcMat, BigDecimal addAnx2,
			BigDecimal addPr) {
		
			this.level = level;
			this.vendorPan = vendorPan;
			this.gstin  = cgstin;
			this.docType = taxDocType;
			this.prMatchAmt = prMatchAmt;
			this.anx2MatchAmt = anx2MatchAmt;
			this.prMissMatAmt = prMissMatAmt;
			this.anxrMisMatAmt = anxrMisMatAmt;
			this.prProbMat = prProbMat;
			this.anx2ProbMat = anx2ProbMat;
			this.prForcMat = prForcMat;
			this.anx2ForcMat = anx2ForcMat;
			this.addAnx2 = addAnx2;
			this.addPr = addPr;
	}

	public VendorReconSummaryResponseDto(String level) {
		this.level = level;
	}

	public VendorReconSummaryResponseDto(String level, String vendorPan,
			String vendorName, String cgstin, String taxDocType,
			BigDecimal prMatchAmt, BigDecimal anx2MatchAmt,
			BigDecimal prMissMatAmt, BigDecimal anxrMisMatAmt,
			BigDecimal prProbMat, BigDecimal anx2ProbMat, BigDecimal prForcMat,
			BigDecimal anx2ForcMat, BigDecimal addPr, BigDecimal addAnx2,
			BigDecimal prMatchPer, BigDecimal prMisMatchPer,
			BigDecimal prProbPer, BigDecimal prForcMatchPer,
			BigDecimal addInAnx2Per, BigDecimal addInPrPer,int count) {

		this.level = level;
		this.vendorPan = vendorPan;
		this.vendorName = vendorName;
		this.gstin = cgstin;
		this.docType = taxDocType;
		this.prMatchAmt = prMatchAmt;
		this.anx2MatchAmt = anx2MatchAmt;
		this.prMissMatAmt = prMissMatAmt;
		this.anxrMisMatAmt = anxrMisMatAmt;
		this.prProbMat = prProbMat;
		this.anx2ProbMat = anx2ProbMat;
		this.prForcMat = prForcMat;
		this.anx2ForcMat = anx2ForcMat;
		this.addAnx2 = addAnx2;
		this.addPr = addPr;
		this.prMatchPer = prMatchPer;
		this.prMissMatPer = prMisMatchPer;
		this.prProbPer = prProbPer;
		this.prForcMatPer = prForcMatchPer;
		this.addanx2Per = addInAnx2Per;
		this.addPrPer = addInPrPer;
		this.count= count;
	}

	public String getKey() {
		if ("L1".equals(level))
			return vendorPan;
		if ("L2".equals(level))
			return vendorPan + "#" + gstin;
		return vendorPan + "#" + gstin + "#" + docType;
	}

	
}
