/**
 * 
 */
package com.ey.advisory.admin.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TBL_RECON_RESP_PSD")
public class Gstr2ReconResultRespPsdEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "BatchID")
	private String batchID;

	@Column(name = "RECON_REPORT_CONFIG_ID")
	private String configId;

	@Column(name = "INVOICEKEYPR")
	private String invoicekeyPR;

	@Column(name = "INVOICEKEYA2")
	private String invoicekeyA2;

	@Column(name = "ISFM")
	private Integer isFM;

	@Column(name = "FMRESPONSE")
	private String fMResponse;

	@Column(name = "RSPTAXPERIOD3B")
	private String rspTaxPeriod3B;

	@Column(name = "TAXPERIOD2A")
	private String taxPeriod2A;

	@Column(name = "TAXPERIODPR")
	private String taxPeriodPR;

	@Column(name = "RGSTIN2A")
	private String rGSTIN2A;

	@Column(name = "SGSTIN2A")
	private String sGSTIN2A;

	@Column(name = "SGSTINPR")
	private String sGSTINPR;

	@Column(name = "RGSTINPR")
	private String rGSTINPR;

	@Column(name = "DOCTYPE2A")
	private String docType2A;

	@Column(name = "DOCTYPEPR")
	private String docTypePR;

	@Column(name = "DOCUMENTNUMBERPR")
	private String documentNumberPR;

	@Column(name = "DOCUMENTNUMBER2A")
	private String documentNumber2A;

	@Column(name = "DOCDATE2A")
	private Date docDate2A;

	@Column(name = "DOCDATEPR")
	private Date docDatePR;

	@Column(name = "CFSFLAG")
	private String cfsFlag;

	@Column(name = "IGSTPR")
	private BigDecimal iGSTPR;

	@Column(name = "CGSTPR")
	private BigDecimal cGSTPR;

	@Column(name = "SGSTPR")
	private BigDecimal sGSTPR;

	@Column(name = "CESSPR")
	private BigDecimal cESSPR;

	@Column(name = "TAXABLEPR")
	private BigDecimal taxablePR;

	@Column(name = "UPLDAVBLIGSTPR")
	private BigDecimal upldAvblIGSTPR;

	@Column(name = "UPLDAVBLCGSTPR")
	private BigDecimal upldAvblCGSTPR;

	@Column(name = "UPLDAVBLSGSTPR")
	private BigDecimal upldAvblSGSTPR;

	@Column(name = "UPLDAVBLCESSPR")
	private BigDecimal upldAvblCessPR;

	@Column(name = "AVBLIGSTPR")
	private BigDecimal avblIGSTPR;

	@Column(name = "AVBLCGSTPR")
	private BigDecimal avblCGSTPR;

	@Column(name = "AVBLSGSTPR")
	private BigDecimal avblSGSTPR;

	@Column(name = "AVBLCESSPR")
	private BigDecimal avblCessPR;

	@Column(name = "IGST2A")
	private BigDecimal iGST2A;

	@Column(name = "CGST2A")
	private BigDecimal cGST2A;

	@Column(name = "SGST2A")
	private BigDecimal sGST2A;

	@Column(name = "CESS2A")
	private BigDecimal cESS2A;

	@Column(name = "TAXABLE2A")
	private BigDecimal taxable2A;

	@Column(name = "TABLETYPE")
	private String tableType;

	@Column(name = "IDPR")
	private long iDPR;

	@Column(name = "ID2A")
	private long iD2A;

	@Column(name = "ITEMID2A")
	private Long itemID2A;

	@Column(name = "SAVE3BIND")
	private char save3BInd;

	@Column(name = "RCDUPDTFLG")
	private char rcdUpdtFlg;

	@Column(name = "CREATEDBY")
	private String createdBy;

	@Column(name = "CREATEDTM")
	private LocalDateTime createDTM;

	@Column(name = "UPDATEDTM")
	private LocalDateTime updateDTM;

	@Column(name = "STARTDTM")
	private LocalDateTime startDtm;

	@Column(name = "DELETEDTM")
	private LocalDateTime deleteDtm;

	@Column(name = "ENDDTM")
	private LocalDateTime endDtm;

	@Column(name = "ITC_PERC")
	private Integer itcPerc;
	
	@Column(name = "STGID")
	private Long sTGID;
	
	//IMS changes
	
	@Column(name = "IMS_USER_RESPONSE")
	private String imsUserResponse;
	
	@Column(name = "IMS_UNIQUE_ID")
	private String imsUniqueId;
	
	@Column(name = "IMS_RESPONSE_REMARKS")
	private String imsResponseRemark;
	
	@Column(name = "IMS_ACTION_GSTIN")
	private String imsActionGstn;
	
	@Column(name = "IMS_PENDING_ACTION_BLOCKED")
	private String imsPendingActionBlocked;
	
	@Column(name = "IMS_GETCALL_DATE_TIME")
	private LocalDateTime imsGetCallDateTime;
	
	@Column(name = "IMS_ACTION_DIGI")
	private String imsActionDigi;
	
	@Column(name = "IMS_ACTION_DIGI_DATE_TIME")
	private LocalDateTime imsActionDigiDateTime;
	
	@Column(name = "IMS_SAVED_TO_GSTIN")
	private Boolean imsSavedToGstn;
	
	@Column(name = "IMS_IS_ACTIVE")
	private Boolean ImsIsActive;
}
