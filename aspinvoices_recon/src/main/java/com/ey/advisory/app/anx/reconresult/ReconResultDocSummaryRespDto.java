/**
 * 
 */
package com.ey.advisory.app.anx.reconresult;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.google.gson.annotations.Expose;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Nikhil.Duseja
 *
 */
@Getter
@Setter
@ToString
public class ReconResultDocSummaryRespDto {
	
	@Expose
	private String reportType;
	
	@Expose
	private String returnPeriod;
	
	@Expose
	private String recipientGstin;
	
	@Expose
	private String supplierGstin;
	
	@Expose
	private String prdocType;
	
	@Expose
	private String anx2docType;
	
	@Expose
	private String prdocNo;
	
	@Expose
	private  String anx2docNo;
	
	@Expose
	private String prdocdate;
	
	@Expose
	private String anx2docdate;
	
	@Expose
	private String prpos;
	
	@Expose
	private String anx2pos;
	
	@Expose
	private BigDecimal prTaxableValue = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal anx2TaxableValue = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal prTotalTax = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal anx2TotalTax = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal prigst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal anx2igst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal prcgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal anx2cgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal prsgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal anx2sgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal prcess  = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal anx2cess  = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal prAvailableIgst  = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal prAvailableCgst  = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal prAvailableSgst  = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal prAvailableCess  = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal  retImpactA10Igst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal 	retImpactA10Cgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal retImpactA10Sgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal retImpactA10Cess = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal retImpactA11Igst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal retImpactA11Cgst = BigDecimal.ZERO;
	
	@Expose 
	private BigDecimal retImpactA11Sgst = BigDecimal.ZERO;
	
	@Expose 
	private BigDecimal retImpactA11Cess = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal retImpactB2Igst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal retImpactB2Cgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal retImpactB2Sgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal retImpactB2Cess = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal retImpactB3Igst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal retImpactB3Cgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal retImpactB3Sgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal retImpactB3Cess = BigDecimal.ZERO;
	
	@Expose
	private String userAction;
	
	@Expose
	private BigInteger reconLinkId = BigInteger.ZERO ;
	
	@Expose
	private String prKey;
	
	@Expose
	private String a2Key;
	
	@Expose
	private Integer isAction = 0;
	
	@Expose
	private String suggestedResponse;
	
	@Expose
	private String prPreviousResponse;
	
	@Expose
	private String anx2pPreviousResponse;


}
