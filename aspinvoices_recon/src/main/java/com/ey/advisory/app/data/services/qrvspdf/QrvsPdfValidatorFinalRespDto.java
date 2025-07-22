package com.ey.advisory.app.data.services.qrvspdf;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class QrvsPdfValidatorFinalRespDto {
	@SerializedName("ReportCategory")
	@Expose
	private String reportCategory;

	@SerializedName("MatchReasons")
	@Expose
	private String matchReasons;

	@SerializedName("MismatchReasons")
	@Expose
	private String mismatchReasons;

	@SerializedName("MatchCount")
	@Expose
	private Integer matchCount;

	@SerializedName("MismatchCount")
	@Expose
	private Integer mismatchCount;

	@SerializedName("RecipientGstin_QR")
	@Expose
	private String recipientGstinQR;

	@SerializedName("RecipientGstin_PDF")
	@Expose
	private String recipientGstinPDF;

	@SerializedName("SupplierGstin_QR")
	@Expose
	private String supplierGstinQR;

	@SerializedName("SupplierGstin_PDF")
	@Expose
	private String supplierGstinPDF;

	@SerializedName("DocNo_QR")
	@Expose
	private String docNoQR;

	@SerializedName("DocNo_PDF")
	@Expose
	private String docNoPDF;

	@SerializedName("DocDt_QR")
	@Expose
	private LocalDate docDtQR;

	@SerializedName("DocDt_PDF")
	@Expose
	private LocalDate docDtPDF;

	@SerializedName("DocTyp_QR")
	@Expose
	private String docTypQR;

	@SerializedName("DocTyp_PDF")
	@Expose
	private String docTypPDF;

	@SerializedName("TotInvVal_QR")
	@Expose
	private BigDecimal totalInvoiceValueQR;

	@SerializedName("TotInvVal_PDF")
	@Expose
	private BigDecimal totalInvoiceValuePDF;

	@SerializedName("ItemCnt_QR")
	@Expose
	private Integer itemCountQR;

	@SerializedName("MainHsnCode_QR")
	@Expose
	private String mainHsnCodeQR;

	@SerializedName("MainHsnCode_PDF")
	@Expose
	private String mainHsnCodePDF;

	@SerializedName("Irn_QR")
	@Expose
	private String irnQR;

	@SerializedName("Irn_PDF")
	@Expose
	private String irnPDF;

	@SerializedName("IrnDt_QR")
	@Expose
	private LocalDateTime irnDateQR;

	@SerializedName("IrnDt_PDF")
	@Expose
	private LocalDate irnDatePDF;

	@SerializedName("Signature")
	@Expose
	private String signature;

	@SerializedName("FileName")
	@Expose
	private String fileName;

	@SerializedName("Validated_Date")
	@Expose
	private LocalDate validatedDate;

	@SerializedName("Declaration_Rule48_(4)")
	@Expose
	private String declarationRule48;

	@SerializedName("EInvoice_Applicability")
	@Expose
	private String eInvoiceApplicability;

	@SerializedName("errMsg")
	@Expose
	private String errMsg;

	@SerializedName("fileId")
	@Expose
	private Long fileId;
	
	@SerializedName("entityId")
	@Expose
	private Long entityId;
}
