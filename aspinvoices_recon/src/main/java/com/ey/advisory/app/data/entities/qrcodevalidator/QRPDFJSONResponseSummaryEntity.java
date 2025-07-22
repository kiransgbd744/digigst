package com.ey.advisory.app.data.entities.qrcodevalidator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Data
@Entity
@Table(name = "QRPDFJSON_RESPONSE_SUMMARY")
@EqualsAndHashCode
public class QRPDFJSONResponseSummaryEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	private Long fileId;

	@Column(name = "FILE_NAME")
	private String fileName;

	@Expose
	@SerializedName("sellerGstinJson")
	@Column(name = "SELLER_GSTIN_JSON")
	private String sellerGstinJson;

	@Expose
	@SerializedName("sellerPanJson")
	@Column(name = "SELLER_PAN_JSON")
	private String sellerPanJson;

	@Expose
	@SerializedName("buyerGstinJson")
	@Column(name = "BUYER_GSTIN_JSON")
	private String buyerGstinJson;

	@Expose
	@SerializedName("buyerPanJson")
	@Column(name = "BUYER_PAN_JSON")
	private String buyerPanJson;

	@Expose
	@SerializedName("docNoJson")
	@Column(name = "DOC_NO_JSON")
	private String docNoJson;

	@Expose
	@SerializedName("docDtJson")
	@Column(name = "DOC_DT_JSON")
	private String docDtJson;

	@Expose
	@SerializedName("docTypeJson")
	@Column(name = "DOC_TYPE_JSON")
	private String docTypeJson;

	@Expose
	@SerializedName("totInvValJson")
	@Column(name = "TOT_INV_VAL_JSON")
	private String totInvValJson;

	@Expose
	@SerializedName("mainHsnCodeJson")
	@Column(name = "MAIN_HSN_CODE_JSON")
	private String mainHsnCodeJson;

	@Expose
	@SerializedName("irnJson")
	@Column(name = "IRN_JSON")
	private String irnJson;

	@Expose
	@SerializedName("irnDateJson")
	@Column(name = "IRN_DATE_JSON")
	private String irnDateJson;

	@Expose
	@SerializedName("signatureQR")
	@Column(name = "SIGNATURE_QR")
	private String signatureQR;

	@Expose
	@SerializedName("sellerGstinPDF")
	@Column(name = "SELLER_GSTIN_PDF")
	private String sellerGstinPDF;

	@Expose
	@SerializedName("sellerPanPDF")
	@Column(name = "SELLER_PAN_PDF")
	private String sellerPanPDF;

	@Expose
	@SerializedName("buyerGstinPDF")
	@Column(name = "BUYER_GSTIN_PDF")
	private String buyerGstinPDF;

	@Expose
	@SerializedName("buyerPanPDF")
	@Column(name = "BUYER_PAN_PDF")
	private String buyerPanPDF;

	@Expose
	@SerializedName("docNoPDF")
	@Column(name = "DOC_NO_PDF")
	private String docNoPDF;

	@Expose
	@SerializedName("docDtPDF")
	@Column(name = "DOC_DT_PDF")
	private String docDtPDF;

	@Expose
	@SerializedName("docTypePDF")
	@Column(name = "DOC_TYPE_PDF")
	private String docTypePDF;

	@Expose
	@SerializedName("totInvValPDF")
	@Column(name = "TOT_INV_VAL_PDF")
	private String totInvValPDF;

	@Expose
	@SerializedName("mainHsnCodePDF")
	@Column(name = "MAIN_HSN_CODE_PDF")
	private String mainHsnCodePDF;

	@Expose
	@SerializedName("irnPDF")
	@Column(name = "IRN_PDF")
	private String irnPDF;

	@Expose
	@SerializedName("irnDatePDF")
	@Column(name = "IRN_DATE_PDF")
	private String irnDatePDF;

	@Expose
	@SerializedName("itemCntPDF")
	@Column(name = "ITEM_CNT_PDF")
	private Integer itemCntPDF;

	@Expose
	@SerializedName("itemCntJson")
	@Column(name = "ITEM_CNT_JSON")
	private Integer itemCntJson;

	@Expose
	@SerializedName("irnCancellationDateJson")
	@Column(name = "IRN_CANCELLATION_DATE_JSON")
	private String irnCancellationDateJson;

	@Expose
	@SerializedName("posJson")
	@Column(name = "POS_JSON")
	private Integer posJson;

	@Expose
	@SerializedName("posPdf")
	@Column(name = "POS_PDF")
	private Integer posPdf;

	@Expose
	@SerializedName("taxableValueJson")
	@Column(name = "TAXABLE_VALUE_JSON")
	private BigDecimal taxableValueJson;

	@Expose
	@SerializedName("taxableValuePdf")
	@Column(name = "TAXABLE_VALUE_PDF")
	private BigDecimal taxableValuePdf;

	@Expose
	@SerializedName("igstJson")
	@Column(name = "IGST_JSON")
	private BigDecimal igstJson;

	@Expose
	@SerializedName("igstPdf")
	@Column(name = "IGST_PDF")
	private BigDecimal igstPdf;

	@Expose
	@SerializedName("cgstJson")
	@Column(name = "CGST_JSON")
	private BigDecimal cgstJson;

	@Expose
	@SerializedName("cgstPdf")
	@Column(name = "CGST_PDF")
	private BigDecimal cgstPdf;

	@Expose
	@SerializedName("sgstUtgstJson")
	@Column(name = "SGST_UTGST_JSON")
	private BigDecimal sgstUtgstJson;

	@Expose
	@SerializedName("sgstUtgstPdf")
	@Column(name = "SGST_UTGST_PDF")
	private BigDecimal sgstUtgstPdf;

	@Expose
	@SerializedName("cessJson")
	@Column(name = "CESS_JSON")
	private BigDecimal cessJson;

	@Expose
	@SerializedName("cessPdf")
	@Column(name = "CESS_PDF")
	private BigDecimal cessPdf;

	@Expose
	@SerializedName("totalTaxJson")
	@Column(name = "TOTAL_TAX_JSON")
	private BigDecimal totalTaxJson;

	@Expose
	@SerializedName("totalTaxPdf")
	@Column(name = "TOTAL_TAX_PDF")
	private BigDecimal totalTaxPdf;

	@Expose
	@SerializedName("reverseChargeJson")
	@Column(name = "REVERSE_CHARGE_JSON")
	private String reverseChargeJson;

	@Expose
	@SerializedName("reverseChargePdf")
	@Column(name = "REVERSE_CHARGE_PDF")
	private String reverseChargePdf;

	@Expose
	@SerializedName("purchaseOrderNoJson")
	@Column(name = "PURCHASE_ORDER_NO_JSON")
	private String purchaseOrderNoJson;

	@Expose
	@SerializedName("purchaseOrderNoPdf")
	@Column(name = "PURCHASE_ORDER_NO_PDF")
	private String purchaseOrderNoPdf;

	@Expose
	@SerializedName("quantityJson")
	@Column(name = "QUANTITY_JSON")
	private String quantityJson;

	@Expose
	@SerializedName("quantityPdf")
	@Column(name = "QUANTITY_PDF")
	private String quantityPdf;

	@Expose
	@SerializedName("unitPriceJson")
	@Column(name = "UNIT_PRICE_JSON")
	private String unitPriceJson;

	@Expose
	@SerializedName("unitPricePdf")
	@Column(name = "UNIT_PRICE_PDF")
	private String unitPricePdf;

	@Expose
	@SerializedName("lineItemAmountJson")
	@Column(name = "LINE_ITEM_AMOUNT_JSON")
	private String lineItemAmountJson;

	@Expose
	@SerializedName("lineItemAmountPdf")
	@Column(name = "LINE_ITEM_AMOUNT_PDF")
	private String lineItemAmountPdf;

	@Expose
	@SerializedName("irnStatusJson")
	@Column(name = "IRN_STATUS_JSON")
	private String irnStatusJson;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Expose
	@SerializedName("validatedDate")
	@Column(name = "VALIDATED_DATE")
	private LocalDate validatedDate;

	@Column(name = "UPDATED_BY")
	private String updatedBy;

	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;

	@Expose
	@SerializedName("matchCount")
	@Column(name = "MATCH_COUNT")
	private Integer matchCount;

	@Expose
	@SerializedName("mismatchCount")
	@Column(name = "MISMATCH_COUNT")
	private Integer mismatchCount;

	@Expose
	@SerializedName("mismatchAttributes")
	@Column(name = "MISMAT_ATTR")
	private String mismatchAttributes;

	@Column(name = "DOC_KEY_JSON")
	private String docKeyJson;

	@Column(name = "DOC_KEY_PDF")
	private String docKeyPDF;

	@Expose
	@SerializedName("isFullMatch")
	@Column(name = "IS_FULLMATCH")
	private Boolean isFullMatch;

	@Expose
	@SerializedName("isProcessedQR")
	@Column(name = "IS_PROCESSED_QR")
	private Boolean isProcessedQR;

	@Expose
	@SerializedName("isSignatureMismatch")
	@Column(name = "IS_SIGMISMAT")
	private Boolean isSigMisMat;

	@Expose
	@SerializedName("reportCategory")
	@Column(name = "REPORT_CATEGORY")
	private String reportCategory;

	@Expose
	@SerializedName("matchReasons")
	@Column(name = "MATCH_REASONS")
	private String matchReasons;

	@Expose
	@SerializedName("misMatchReasons")
	@Column(name = "MISMATCH_REASONS")
	private String misMatchReasons;

	@Expose
	@SerializedName("declaration")
	@Column(name = "DECLARATION")
	private String declaration;

	@Expose
	@Transient
	private int sINo;

	@Transient
	@Expose
	@SerializedName("validatedDateStr")
	private String validatedDateStr;

	public String getValidatedDateStr() {
		return this.validatedDate
				.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	}

	@Expose
	@SerializedName("sellerGstinMatch")
	@Column(name = "SELLER_GSTIN_MATCH")
	private String sellerGstinMatch;

	@Expose
	@SerializedName("buyerGstinMatch")
	@Column(name = "BUYER_GSTIN_MATCH")
	private String buyerGstinMatch;

	@Expose
	@SerializedName("docNoMatch")
	@Column(name = "DOCNO_MATCH")
	private String docNoMatch;

	@Expose
	@SerializedName("docTypeMatch")
	@Column(name = "DOCTYPE_MATCH")
	private String docTypeMatch;

	@Expose
	@SerializedName("docDtMatch")
	@Column(name = "DOCDT_MATCH")
	private String docDtMatch;

	@Expose
	@SerializedName("totInvValMatch")
	@Column(name = "TOTAL_INV_MATCH")
	private String totInvValMatch;

	@Expose
	@SerializedName("mainHsnCodeMatch")
	@Column(name = "MAINHSNCODE_MATCH")
	private String mainHsnCodeMatch;

	@Expose
	@SerializedName("irnMatch")
	@Column(name = "IRN_MATCH")
	private String irnMatch;

	@Expose
	@SerializedName("irnDateMatch")
	@Column(name = "IRNDate_MATCH")
	private String irnDateMatch;

	@Expose
	@SerializedName("signatureMatch")
	@Column(name = "SIGNATURE_MATCH")
	private String signatureMatch;

	@Expose
	@SerializedName("irnStatusMatch")
	@Column(name = "IRN_STATUS_MATCH")
	private String irnStatusMatch;

	@Expose
	@SerializedName("posMatch")
	@Column(name = "POS_MATCH")
	private String posMatch;
	@Expose
	@SerializedName("taxableValueMatch")
	@Column(name = "TAXABLE_VALUE_MATCH")
	private String taxableValueMatch;

	@Expose
	@SerializedName("igstMatch")
	@Column(name = "IGST_MATCH")
	private String igstMatch;
	@Expose
	@SerializedName("cgstMatch")
	@Column(name = "CGST_MATCH")
	private String cgstMatch;
	@Expose
	@SerializedName("sgstMatch")
	@Column(name = "SGST_MATCH")
	private String sgstMatch;

	@Expose
	@SerializedName("cessMatch")
	@Column(name = "CESS_MATCH")
	private String cessMatch;

	@Expose
	@SerializedName("totalTaxMatch")
	@Column(name = "TOTAL_TAX_MATCH")
	private String totalTaxMatch;

	@Expose
	@SerializedName("reverseChargeMatch")
	@Column(name = "REVERSE_CHARGE_MATCH")
	private String reverseChargeMatch;

	@Expose
	@SerializedName("einvAppli")
	@Column(name = "EINV_APPLI")
	private String einvAppli;

	@Transient
	@Expose
	@SerializedName("Filename")
	private String tempFileName;
	
	@Expose
	@Column(name = "JSON_RESPONSE")
	private String jsonResponse;
	
	@Expose
	@SerializedName("entityId")
	@Column(name = "ENTITY_ID")
	private Long entityId;
	
}
