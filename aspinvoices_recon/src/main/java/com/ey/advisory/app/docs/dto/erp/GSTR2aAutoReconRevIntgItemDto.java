package com.ey.advisory.app.docs.dto.erp;

import java.math.BigDecimal;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 * @author Jithendra.B
 *
 */
@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class GSTR2aAutoReconRevIntgItemDto {

	@XmlElement(name = "MANDT")
	public String mandt;

	@XmlElement(name = "RECON_LINK_ID")
	public String reconLinkId;

	@XmlElement(name = "INV_KEY_2A")
	public String invKey2A;

	@XmlElement(name = "INV_KEY_PR")
	public String invKeyPR;

	@XmlElement(name = "USERRESPONSE")
	public String userResp;

	@XmlElement(name = "TAXPER_GSTR3B")
	public String taxPeriodGstr3B;

	@XmlElement(name = "MATCH_REASON")
	public String matchReason;

	@XmlElement(name = "MISMATCH_REASON")
	public String misMatchReason;

	@XmlElement(name = "REPORT_CAT")
	public String reportCategory;

	@XmlElement(name = "REPORT_TYPE")
	public String reportType;

	@XmlElement(name = "ERP_REP_TYPE")
	public String erpReportType;

	@XmlElement(name = "TAXPER_2A")
	public String taxPeriod2A;

	@XmlElement(name = "TAXPER_2B")
	public String taxPeriod2B;

	@XmlElement(name = "TAXPER_PR")
	public String taxPeriodPR;

	@XmlElement(name = "RGSTIN_2A")
	public String rgstin2A;

	@XmlElement(name = "RGSTIN_PR")
	public String rgstinPR;

	@XmlElement(name = "SGSTIN_2A")
	public String sgstin2A;

	@XmlElement(name = "SGSTIN_PR")
	public String sgstinPR;

	@XmlElement(name = "DOCTYPE_2A")
	public String docType2A;

	@XmlElement(name = "DOCTYPE_PR")
	public String docTypePR;

	@XmlElement(name = "DOCNUMBER_2A")
	public String docNumber2A;

	@XmlElement(name = "DOCNUMBER_PR")
	public String docNumberPR;

	@XmlElement(name = "DOCDATE_2A")
	public String docDate2A;

	@XmlElement(name = "DOCDATE_PR")
	public String docDatePR;

	@XmlElement(name = "POS_2A")
	public String pos2A;

	@XmlElement(name = "POS_PR")
	public String posPR;

	@XmlElement(name = "TAX_VALUE_2A")
	public BigDecimal taxableValue2A = BigDecimal.ZERO;

	@XmlElement(name = "TAX_VALUE_PR")
	public BigDecimal taxableValuePR = BigDecimal.ZERO;

	@XmlElement(name = "IGST_2A")
	public BigDecimal igst2A = BigDecimal.ZERO;

	@XmlElement(name = "IGST_PR")
	public BigDecimal igstPR = BigDecimal.ZERO;

	@XmlElement(name = "CGST_2A")
	public BigDecimal cgst2A = BigDecimal.ZERO;

	@XmlElement(name = "CGST_PR")
	public BigDecimal cgstPR = BigDecimal.ZERO;

	@XmlElement(name = "SGST_2A")
	public BigDecimal sgst2A = BigDecimal.ZERO;

	@XmlElement(name = "SGST_PR")
	public BigDecimal sgstPR = BigDecimal.ZERO;

	@XmlElement(name = "CESS_2A")
	public BigDecimal cess2A = BigDecimal.ZERO;

	@XmlElement(name = "CESS_PR")
	public BigDecimal cessPR = BigDecimal.ZERO;

	@XmlElement(name = "INV_VALUE_2A")
	public BigDecimal invoiceValue2A = BigDecimal.ZERO;

	@XmlElement(name = "INV_VALUE_PR")
	public BigDecimal invoiceValuePR = BigDecimal.ZERO;

	@XmlElement(name = "ITC_AVAIL_2B")
	public String itcAvailable2B;

	@XmlElement(name = "ITC_UNAVAIL_2B")
	public String itcUnavailable2B;

	@XmlElement(name = "GSTR1_FS")
	public String gstr1FilingStat;

	@XmlElement(name = "GSTR1_FD")
	public String gstr1FilingDate;

	@XmlElement(name = "GSTR1_FPRD")
	public String gstr1FilingPeriod;

	@XmlElement(name = "GSTR3B_FS")
	public String gstr3BFilingStat;

	@XmlElement(name = "GSTR3B_FD")
	private String gstr3BFilingDate;

	@XmlElement(name = "SGSTIN_STATUS")
	private String supplierGstinStatus;

	@XmlElement(name = "REV_CHRG_FLG_2A")
	public String revChrgFlag2A;

	@XmlElement(name = "REV_CHRG_FLG_PR")
	public String revChrgFlagPR;

	@XmlElement(name = "PLANTCODE")
	public String plantCode;

	@XmlElement(name = "DIVISION")
	public String division;

	@XmlElement(name = "PURCHASEORG")
	public String purchaseOrg;

	@XmlElement(name = "TABLETYPE_2A")
	public String tableType2A;

	@XmlElement(name = "SUPPLYTYPE_2A")
	public String supplyType2A;

	@XmlElement(name = "SUPPLYTYPE_PR")
	public String supplyTypePR;

	@XmlElement(name = "ACCNO")
	public String accNo;

	@XmlElement(name = "ACCDATE")
	public String accDate;

	@XmlElement(name = "APPROVAL_STATUS")
	public String approvalStatus;

	@XmlElement(name = "ISDELINK")
	public String isDelink;

	@XmlElement(name = "DELINKREASON")
	public String deLinkReason;

	@XmlElement(name = "CREATEDON")
	public String createdOn;

	@XmlElement(name = "RECON_DATE")
	public String reconDate;

	@XmlElement(name = "CREATEDATE")
	public String createdDate;

	@XmlElement(name = "IRN_2A")
	public String irn2A;

	@XmlElement(name = "IRNDATE_2A")
	public String irnDate2A;

	@XmlElement(name = "USR_DEF_FD1")
	public String userDefinedField1;

	@XmlElement(name = "USR_DEF_FD2")
	public String userDefinedField2;

	@XmlElement(name = "USR_DEF_FD3")
	public String userDefinedField3;

	@XmlElement(name = "USR_DEF_FD4")
	public String userDefinedField4;

	@XmlElement(name = "USR_DEF_FD5")
	public String userDefinedField5;

	@XmlElement(name = "SYSDEFFD1")
	private String systemDefinedField1;

	@XmlElement(name = "SYSDEFFD2")
	private String systemDefinedField2;

	@XmlElement(name = "SYSDEFFD3")
	private String systemDefinedField3;

	@XmlElement(name = "SYSDEFFD4")
	private String systemDefinedField4;

	@XmlElement(name = "SYSDEFFD5")
	private String systemDefinedField5;

	@XmlElement(name = "SYSDEFFD6")
	private String systemDefinedField6;

	@XmlElement(name = "SYSDEFFD7")
	private String systemDefinedField7;

	@XmlElement(name = "SYSDEFFD8")
	private String systemDefinedField8;

	@XmlElement(name = "SYSDEFFD9")
	private String systemDefinedField9;

	@XmlElement(name = "SYSDEFFD10")
	private String systemDefinedField10;

	@XmlElement(name = "REQUEST_ID")
	public Long requestId;

	@XmlElement(name = "ID_PR")
	public Long idPR;

	@XmlElement(name = "ID_2A")
	public Long id2A;

	@XmlElement(name = "RESP_REMARK")
	public String responseRemarks;

	@XmlElement(name = "ELG_IND")
	public String eligibilityIndicator;

	@XmlElement(name = "AVAILABLE_IGST")
	public String availableIgst;

	@XmlElement(name = "AVAILABLE_CGST")
	public String availableCgst;

	@XmlElement(name = "AVAILABLE_SGST")
	public String availableSgst;

	@XmlElement(name = "AVAILABLE_CESS")
	public String availableCess;

	@XmlElement(name = "RETURN_F_FREQ")
	public String returnFilingFreq;

	@XmlElement(name = "SGSTIN_CAN_DATE")
	public String sgstinCanDate;

	@XmlElement(name = "VEN_COMP_TREND")
	public String vendorCompTrend;

	@XmlElement(name = "SUPPLIER_CODE")
	public String supplierCode;

	@XmlElement(name = "BOE_REF_DATE2A")
	public String boeRefDate2A;

	@XmlElement(name = "PORT_CODE_2A")
	public String portCode2A;

	@XmlElement(name = "PORT_CODE_PR")
	public String portCodePR;

	@XmlElement(name = "BOE_2A")
	public String billOfEntry2A;

	@XmlElement(name = "BOE_PR")
	public String billOfEntryPR;

	@XmlElement(name = "BOE_DATE_2A")
	public String boeDate2A;

	@XmlElement(name = "BOE_DATE_PR")
	public String boeDatePR;

	@XmlElement(name = "COMPANY_CODE")
	public String companyCode;

	@XmlElement(name = "SOURCE_IDENT")
	public String sourceIdentifier;

	@XmlElement(name = "VENDOR_TYPE")
	public String vendorType;

	@XmlElement(name = "HSN")
	public String hsn;

	@XmlElement(name = "VEND_RISK_CATY")
	public String vendorRiskCateg;

	@XmlElement(name = "VEND_PYMT_TERM")
	public String vendorPymntTerm;

	@XmlElement(name = "VEND_REMARKS")
	public String vendorRemarks;

	@XmlElement(name = "EINV_APPL")
	public String einvApplicability;

	@XmlElement(name = "QR_CODECHECK")
	public String qrCodeCheck;

	@XmlElement(name = "QR_CODERES")
	public String qrValidResult;

	@XmlElement(name = "QR_CMATCHCOUNT")
	public String qrCodeMatchCount;

	@XmlElement(name = "QR_CMISMATCH")
	public String qrCodeMisMatchCount;

	@XmlElement(name = "QR_MISMATCHATTR")
	public String qrMismatchAttr;

	@XmlElement(name = "REP_TYPE")
	public String retType;

	@XmlElement(name = "SUGG_RESP")
	public String suggestedResp;

	@XmlElement(name = "ENTITY_NAME")
	public String entityName;

	@XmlElement(name = "ENTITY_PAN")
	public String entityPan;

}
