package com.ey.advisory.app.data.entities.qrcodevalidator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Data
@Entity
@Table(name = "QR_RESPONSE_SUMMARY")
public class QRResponseSummaryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	private Long fileId;

	@Expose
	@SerializedName("SellerGstin")
	@Column(name = "SELLER_GSTIN")
	private String sellerGstin;

	@Column(name = "SELLER_PAN")
	private String sellerPan;

	@Expose
	@SerializedName("BuyerGstin")
	@Column(name = "BUYER_GSTIN")
	private String buyerGstin;

	@Column(name = "BUYER_PAN")
	private String buyerPan;

	@Expose
	@SerializedName("DocNo")
	@Column(name = "DOC_NO")
	private String docNo;

	@Expose
	@SerializedName("DocDt")
	@Column(name = "DOC_DT")
	private String docDt;

	@Expose
	@SerializedName("DocTyp")
	@Column(name = "DOC_TYPE")
	private String docType;

	@Expose
	@SerializedName("TotInvVal")
	@Column(name = "TOT_INV_VAL")
	private String totInvVal;

	@Expose
	@SerializedName("ItemCnt")
	@Column(name = "ITEM_CNT")
	private Integer itemCnt;

	@Expose
	@SerializedName("File_Name")
	@Column(name = "FILE_NAME")
	private String fileName;

	@Expose
	@SerializedName("MainHsnCode")
	@Column(name = "MAIN_HSN_CODE")
	private String mainHsnCode;

	@Expose
	@SerializedName("Irn")
	@Column(name = "IRN")
	private String irn;

	@Expose
	@SerializedName("IrnDt")
	@Column(name = "IRN_DATE")
	private String irnDate;

	@Expose
	@SerializedName("Signature")
	@Column(name = "SIGNATURE")
	private String signature;

	@Expose
	@SerializedName("SellerGstin_Match")
	@Column(name = "SELLER_GSTIN_MATCH")
	private String sellerGstinMatch;

	@Expose
	@SerializedName("BuyerGstin_Match")
	@Column(name = "BUYER_GSTIN_MATCH")
	private String buyerGstinMatch;

	@Expose
	@SerializedName("DocNo_Match")
	@Column(name = "DOCNO_MATCH")
	private String docNoMatch;

	@Expose
	@SerializedName("DocTyp_Match")
	@Column(name = "DOCTYPE_MATCH")
	private String docTypeMatch;

	@Expose
	@SerializedName("DocDt_Match")
	@Column(name = "DOCDT_MATCH")
	private String docDtMatch;

	@Expose
	@SerializedName("TotInvVal_Match")
	@Column(name = "TOTAL_INV_MATCH")
	private String totInvValMatch;

	@Expose
	@SerializedName("MainHsnCode_Match")
	@Column(name = "MAINHSNCODE_MATCH")
	private String mainHsnCodeMatch;

	@Expose
	@SerializedName("Irn_Match")
	@Column(name = "IRN_MATCH")
	private String irnMatch;

	@Expose
	@SerializedName("Validated_Date")
	@Column(name = "VALIDATED_DATE")
	private LocalDate validatedDate;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;

	@Column(name = "UPDATED_BY")
	private String updatedBy;

	@Expose
	@SerializedName("Match_Count")
	@Column(name = "MATCH_COUNT")
	private Integer matchCount;

	@Expose
	@SerializedName("Mismatch_Count")
	@Column(name = "MISMATCH_COUNT")
	private Integer mismatchCount;

	@Column(name = "MISMAT_ATTR")
	private String misMatAtt;

	@Column(name = "DOC_KEY")
	private String docKey;

	@Transient
	@Expose
	@SerializedName("errMsg")
	private String errMsg;

	@Transient
	@Expose
	@SerializedName("File Name")
	private String tempFileName;

	@Transient
	private int sINo;

	@Column(name = "IS_FULLMATCH")
	private boolean isFullMatch;

	@Column(name = "IS_PROCESSEDQR")
	private boolean isProcessedQR;

	@Column(name = "IS_SIGMISMAT")
	private boolean isSigMisMat;

	@Transient
	@Expose
	@SerializedName("validatedDateStr")
//	@Getter(value = AccessLevel.NONE)
	private String validatedDateStr;

	public String getValidatedDateStr() {
		return this.validatedDate
				.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	}
	@Expose
	@SerializedName("Declaration_Rule48_(4)")
	@Column(name = "DECLARATION")
	private String declaration;
	
	@Expose
	@SerializedName("entityId")
	@Column(name = "ENTITY_ID")
	private Long entityId;
}
