package com.ey.advisory.app.inward.einvoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class JsonPdfValidatorFinalRespDto {
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

	@SerializedName("RecipientGstin_JSON")
	@Expose
	private String recipientGstinJSON;

	@SerializedName("RecipientGstin_PDF")
	@Expose
	private String recipientGstinPDF;

	@SerializedName("SupplierGstin_JSON")
	@Expose
	private String supplierGstinJSON;

	@SerializedName("SupplierGstin_PDF")
	@Expose
	private String supplierGstinPDF;

	@SerializedName("DocNo_JSON")
	@Expose
	private String docNoJSON;

	@SerializedName("DocNo_PDF")
	@Expose
	private String docNoPDF;

	@SerializedName("DocDt_JSON")
	@Expose
	private LocalDate docDtJSON;

	@SerializedName("DocDt_PDF")
	@Expose
	private LocalDate docDtPDF;

	@SerializedName("DocTyp_JSON")
	@Expose
	private String docTypJSON;

	@SerializedName("DocTyp_PDF")
	@Expose
	private String docTypPDF;

	@SerializedName("TotInvVal_JSON")
	@Expose
	private BigDecimal totalInvoiceValueJSON;

	@SerializedName("TotInvVal_PDF")
	@Expose
	private BigDecimal totalInvoiceValuePDF;

	@SerializedName("ItemCnt_JSON")
	@Expose
	private Integer itemCountJSON;

	@SerializedName("ItemCnt_PDF")
	@Expose
	private Integer itemCountPDF;

	@SerializedName("MainHsnCode_JSON")
	@Expose
	private String mainHsnCodeJSON;

	@SerializedName("MainHsnCode_PDF")
	@Expose
	private String mainHsnCodePDF;

	@SerializedName("Irn_JSON")
	@Expose
	private String irnJSON;

	@SerializedName("Irn_PDF")
	@Expose
	private String irnPDF;

	@SerializedName("IrnDt_JSON")
	@Expose
	private LocalDateTime irnDateJSON;

	@SerializedName("IrnDt_PDF")
	@Expose
	private LocalDate irnDatePDF;

	@SerializedName("Signature_QR")
	@Expose
	private String signature;

	@SerializedName("IrnStatus_JSON")
	@Expose
	private String irnStatusJSON;

	@SerializedName("IrnCancellationDt_JSON")
	@Expose
	private LocalDateTime irnCancelDtJSON;

	@SerializedName("Pos_JSON")
	@Expose
	private Integer posJSON;

	@SerializedName("Pos_PDF")
	@Expose
	private Integer posPdf;

	@SerializedName("Taxable_JSON")
	@Expose
	private BigDecimal taxableJSON;

	@SerializedName("Taxable_PDF")
	@Expose
	private BigDecimal taxablePdf;

	@SerializedName("Total_Tax_JSON")
	@Expose
	private BigDecimal totalJSON;

	@SerializedName("Total_Tax_PDF")
	@Expose
	private BigDecimal totalPdf;

	@SerializedName("Igst_JSON")
	@Expose
	private BigDecimal igstJSON;

	@SerializedName("Igst_PDF")
	@Expose
	private BigDecimal igstPdf;

	@SerializedName("Cgst_JSON")
	@Expose
	private BigDecimal cgstJSON;

	@SerializedName("Cgst_PDF")
	@Expose
	private BigDecimal cgstPdf;

	@SerializedName("Sgst/Ugst_JSON")
	@Expose
	private BigDecimal sgstUgstJSON;

	@SerializedName("Sgst/Ugst_PDF")
	@Expose
	private BigDecimal sgstUgstPdf;

	@SerializedName("Cess_JSON")
	@Expose
	private BigDecimal cessJSON;

	@SerializedName("Cess_PDF")
	@Expose
	private BigDecimal CessPdf;

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

	@SerializedName("RCM_JSON")
	@Expose
	private String rcmJSON;

	@SerializedName("RCM_PDF")
	@Expose
	private String rcmPDF;

	@SerializedName("Quantity_JSON")
	@Expose
	private String quantityJSON;

	@SerializedName("Quantity_PDF")
	@Expose
	private String quantityPDF;

	@SerializedName("UnitPrice_JSON")
	@Expose
	private String unitPriceJSON;

	@SerializedName("UnitPrice_PDF")
	@Expose
	private String unitPricePDF;

	@SerializedName("Line_Item_Amount_JSON")
	@Expose
	private String lineItemJSON;

	@SerializedName("Line_Item_Amount_PDF")
	@Expose
	private String lineItemPDF;
	
	@SerializedName("Purchase_Order_No_JSON")
	@Expose
	private String purchaseOrderJson;
	
	@SerializedName("Purchase_Order_No_PDF")
	@Expose
	private String purchaseOrderPdf;
	
	@SerializedName("FileName")
	@Expose
	private String fileName;

	@SerializedName("Validated_Date")
	@Expose
	private LocalDate validatedDate;

	//private method to accept the irn json payload

	private String jsonResponse;
}
