package com.ey.advisory.app.asprecon.gstr2.recon.result;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author vishal.verma
 *
 */

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Gstr2ReconResultPsdRespDto {

	private String forceMatchResponse;
	private String taxPeriodforGSTR3B;
	

	private String recipientGstin2A;
	private String recipientGstinPR;
	private String supplierGstin2A;
	private String supplierGstinPR;

	private String docType2A;
	private String docTypePR;
	private String documentNumber2A;
	private String documentNumberPR;
	private Date documentDate2A;
	private Date documentDatePR;

	private BigDecimal cGST2A;
	private BigDecimal cGSTPR;
	private BigDecimal sGST2A;
	private BigDecimal sGSTPR;
	private BigDecimal cess2A;
	private BigDecimal cessPR;

	private BigDecimal taxableValue2A;
	private BigDecimal taxableValuePR;
	private BigDecimal iGST2A;
	private BigDecimal iGSTPR;

	private String InvoiceKeyPR;
	private String InvoiceKeyA2;
	private String iDPR;
	private String iD2A;
	private String gstr1FillingStatus;

	private BigDecimal availableIGST;
	private BigDecimal availableCGST;
	private BigDecimal availableSGST;
	private BigDecimal availableCESS;

	private String taxPeriod2A;
	private String taxPeriodPR;

	private String tableType2A;

	private String configId;

	private String userResponse;
	
}
