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
@Table(name = "QRPDF_RESPONSE_SUMMARY")
public class QRPDFResponseSummaryEntity {

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
    @SerializedName("sellerGstinQR")
    @Column(name = "SELLER_GSTIN_QR")
    private String sellerGstinQR;

    @Expose
    @SerializedName("sellerPanQR")
    @Column(name = "SELLER_PAN_QR")
    private String sellerPanQR;

    @Expose
    @SerializedName("buyerGstinQR")
    @Column(name = "BUYER_GSTIN_QR")
    private String buyerGstinQR;

    @Expose
    @SerializedName("buyerPanQR")
    @Column(name = "BUYER_PAN_QR")
    private String buyerPanQR;

    @Expose
    @SerializedName("docNoQR")
    @Column(name = "DOC_NO_QR")
    private String docNoQR;

    @Expose
    @SerializedName("docDtQR")
    @Column(name = "DOC_DT_QR")
    private String docDtQR;

    @Expose
    @SerializedName("docTypeQR")	
    @Column(name = "DOC_TYPE_QR")
    private String docTypeQR;

    @Expose
    @SerializedName("totInvValQR")	
    @Column(name = "TOT_INV_VAL_QR")
    private String totInvValQR;

    @Expose
    @SerializedName("mainHsnCodeQR")		
    @Column(name = "MAIN_HSN_CODE_QR")
    private String mainHsnCodeQR;

    @Expose
    @SerializedName("irnQR")	
    @Column(name = "IRN_QR")
    private String irnQR;

    @Expose
    @SerializedName("irnDateQR")	
    @Column(name = "IRN_DATE_QR")
    private String irnDateQR;

    @Expose
    @SerializedName("itemCntQR")	
    @Column(name = "ITEM_CNT_QR")
    private Integer itemCntQR;

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

    @Column(name = "DOC_KEY_QR")
    private String docKeyQR;

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
//    @Getter(value = AccessLevel.NONE)
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
    @SerializedName("einvAppli")
    @Column(name = "EINV_APPLI")
    private String einvAppli;

    @Transient
    @Expose
    @SerializedName("Filename")
    private String tempFileName;
    
    @Expose
	@SerializedName("entityId")
	@Column(name = "ENTITY_ID")
	private Long entityId;
}
