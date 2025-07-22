package com.ey.advisory.app.data.entities.client.gstr7trans;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
@Entity
@Table(name = "GSTR7_TRANS_DOC_HEADER")
public class Gstr7TransDocHeaderEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR7_TRANS_DOC_HEADER_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	
	@Expose
	@SerializedName("userId")
	@Column(name = "USER_ID")
	private String userId;

	@Expose
	@SerializedName("deductorGstin")
	@Column(name = "DEDUCTOR_GSTIN")
	private String deductorGstin;

	@Expose
	@SerializedName("fiYear")
	@Column(name = "FI_YEAR")
	private String fiYear;

	@Expose
	@SerializedName("returnPeriod")
	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Expose
	@SerializedName("derivedRetPeriod")
	@Column(name = "DERIVED_RET_PERIOD")
	private Integer derivedRetPeriod;

	@Expose
	@SerializedName("supplyType")
	@Column(name = "SUPPLY_TYPE")
	private String supplyType;

	@Expose
	@SerializedName("docType")
	@Column(name = "DOC_TYPE")
	private String docType;

	@Expose
	@SerializedName("docNum")
	@Column(name = "DOC_NUM")
	private String docNum;

	@Expose
	@SerializedName("docDate")
	@Column(name = "DOC_DATE")
	private LocalDate docDate;

	@Expose
	@SerializedName("originalDocNum")
	@Column(name = "ORIGINAL_DOC_NUM")
	private String originalDocNum;

	@Expose
	@SerializedName("originalDocDate")
	@Column(name = "ORIGINAL_DOC_DATE")
	private LocalDate originalDocDate;

	@Expose
	@SerializedName("originalDocPeriod")
	@Column(name = "ORIGINAL_DOC_PERIOD")
	private String originalDocPeriod;

	@Expose
	@SerializedName("deducteeGstin")
	@Column(name = "DEDUCTEE_GSTIN")
	private String deducteeGstin;

	@Expose
	@SerializedName("originalDeducteeGstin")
	@Column(name = "ORIGINAL_DEDUCTEE_GSTIN")
	private String originalDeducteeGstin;

	@Expose
	@SerializedName("originalReturnPeriod")
	@Column(name = "ORIGINAL_RETURN_PERIOD")
	private String originalReturnPeriod;

	@Expose
	@SerializedName("originalDerivedRetPeriod")
	@Column(name = "ORIGINAL_DERIVED_RET_PERIOD")
	private Integer originalDerivedRetPeriod;

	@Expose
	@SerializedName("originalTaxableValue")
	@Column(name = "ORIGINAL_TAXABLE_VALUE")
	private BigDecimal originalTaxableValue;
	
	@Expose
	@SerializedName("originalInvoiceValue")
	@Column(name = "ORIGINAL_INVOICE_VALUE")
	private BigDecimal originalInvoiceValue;

	@Expose
	@SerializedName("invoiceValue")
	@Column(name = "INVOICE_VALUE")
	private BigDecimal invoiceValue;

	@Expose
	@SerializedName("dataOriginTypeCode")
	@Column(name = "DATAORIGINTYPECODE")
	private String dataOriginTypeCode;

	@Expose
	@SerializedName("batchId")
	@Column(name = "BATCH_ID")
	private Long batchId;

	@Expose
	@SerializedName("isError")
	@Column(name = "IS_ERROR")
	private boolean isError;

	@Expose
	@SerializedName("isProcessed")
	@Column(name = "IS_PROCESSED")
	private boolean isProcessed;

	@Expose
	@SerializedName("isInformation")
	@Column(name = "IS_INFORMATION")
	private boolean isInformation;

	@Expose
	@SerializedName("isSavedToGstn")
	@Column(name = "IS_SAVED_TO_GSTN")
	private boolean isSavedToGstn;

	@Expose
	@SerializedName("isSentToGstn")
	@Column(name = "IS_SENT_TO_GSTN")
	private boolean isSentToGstn;

	@Expose
	@SerializedName("isDelete")
	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Expose
	@SerializedName("gstnError")
	@Column(name = "GSTN_ERROR")
	private boolean gstnError;

	@Expose
	@SerializedName("docKey")
	@Column(name = "DOC_KEY")
	private String docKey;

	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	private Long fileId;

	@Expose
	@SerializedName("sentToGSTNDate")
	@Column(name = "SENT_TO_GSTN_DATE")
	protected LocalDate sentToGSTNDate;

	@Expose
	@SerializedName("savedToGSTNDate")
	@Column(name = "SAVED_TO_GSTN_DATE")
	protected LocalDate savedToGSTNDate;

	@Expose
	@SerializedName("errorCodes")
	@Column(name = "ERROR_CODES")
	private String errorCodes;

	@Expose
	@SerializedName("informationCodes")
	@Column(name = "INFORMATION_CODES")
	private String informationCodes;

	@Expose
	@SerializedName("payloadId")
	@Column(name = "PAYLOAD_ID")
	private String payloadId;

	@Expose
	@SerializedName("isFiled")
	@Column(name = "IS_FILED")
	private boolean isFiled;

	@Expose
	@SerializedName("filedDate")
	@Column(name = "FILED_DATE")
	private LocalDate filedDate;

	@Expose
	@SerializedName("gstnSaveRefId")
	@Column(name = "GSTN_SAVE_REF_ID")
	private String gstnSaveRefId;

	@Expose
	@SerializedName("contractNumber")
	@Column(name = "CONTRACT_NUMBER")
	private String contractNumber;

	@Expose
	@SerializedName("contractDate")
	@Column(name = "CONTRACT_DATE")
	private LocalDate contractDate;

	@Expose
	@SerializedName("contractValue")
	@Column(name = "CONTRACT_VALUE")
	private BigDecimal contractValue;

	@Expose
	@SerializedName("paymentAdviceNumber")
	@Column(name = "PAYMENT_ADVICE_NUMBER")
	private String paymentAdviceNumber;

	@Expose
	@SerializedName("paymentAdviceDate")
	@Column(name = "PAYMENT_ADVICE_DATE")
	private LocalDate paymentAdviceDate;

	@Expose
	@SerializedName("sourceIdentifier")
	@Column(name = "SOURCE_IDENTIFIER")
	private String sourceIdentifier;

	@Expose
	@SerializedName("sourceFilename")
	@Column(name = "SOURCE_FILENAME")
	private String sourceFilename;

	@Expose
	@SerializedName("division")
	@Column(name = "DIVISION")
	private String division;
	
	@Expose
	@SerializedName("plantCode")
	@Column(name = "PLANT_CODE")
	private String plantCode;

	@Expose
	@SerializedName("profitCentre1")
	@Column(name = "PROFIT_CENTRE1")
	private String profitCentre1;

	@Expose
	@SerializedName("profitCentre2")
	@Column(name = "PROFIT_CENTRE2")
	private String profitCentre2;

	@Expose
	@SerializedName("userdefinedField1")
	@Column(name = "USERDEFINED_FIELD1")
	private String userdefinedField1;

	@Expose
	@SerializedName("userdefinedField2")
	@Column(name = "USERDEFINED_FIELD2")
	private String userdefinedField2;

	@Expose
	@SerializedName("userdefinedField3")
	@Column(name = "USERDEFINED_FIELD3")
	private String userdefinedField3;

	@Expose
	@SerializedName("userdefinedField4")
	@Column(name = "USERDEFINED_FIELD4")
	private String userdefinedField4;

	@Expose
	@SerializedName("userdefinedField5")
	@Column(name = "USERDEFINED_FIELD5")
	private String userdefinedField5;

	@Expose
	@SerializedName("userdefinedField6")
	@Column(name = "USERDEFINED_FIELD6")
	private String userdefinedField6;

	@Expose
	@SerializedName("userdefinedField7")
	@Column(name = "USERDEFINED_FIELD7")
	private String userdefinedField7;

	@Expose
	@SerializedName("userdefinedField8")
	@Column(name = "USERDEFINED_FIELD8")
	private String userdefinedField8;

	@Expose
	@SerializedName("userdefinedField9")
	@Column(name = "USERDEFINED_FIELD9")
	private String userdefinedField9;

	@Expose
	@SerializedName("userdefinedField10")
	@Column(name = "USERDEFINED_FIELD10")
	private String userdefinedField10;

	@Expose
	@SerializedName("createdBy")
	@Column(name = "CREATED_BY")
	private String createdBy;

	@Expose
	@SerializedName("createdOn")
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Expose
	@SerializedName("modifiedBy")
	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Expose
	@SerializedName("modifiedOn")
	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Expose
	@SerializedName("section")
	@Column(name = "SECTION")
	private String section;

	@Expose
	@SerializedName("taxableValue")
	@Column(name = "TAXABLE_VALUE")
	private BigDecimal taxableValue;

	@Expose
	@SerializedName("igstAmt")
	@Column(name = "IGST_AMT")
	private BigDecimal igstAmt;

	@Expose
	@SerializedName("cgstAmt")
	@Column(name = "CGST_AMT")
	private BigDecimal cgstAmt;

	@Expose
	@SerializedName("sgstAmt")
	@Column(name = "SGST_AMT")
	private BigDecimal sgstAmt;

	@Expose
	@SerializedName("gstnErrorCode")
	@Column(name = "GSTN_ERROR_CODE")
	private String gstnErrorCode;

	@Expose
	@SerializedName("gstnErrorDesc")
	@Column(name = "GSTN_ERROR_DESC")
	private String gstnErrorDesc;

	@Expose
	@SerializedName("lineItems")
	@OneToMany(mappedBy = "document")
	@OrderColumn(name = "ITEM_INDEX")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	protected List<Gstr7TransDocItemEntity> lineItems = new ArrayList<>();

	public Integer getItemNoForIndex(Integer itemIndex) {

		int lineItemCount = this.lineItems.size();

		if (itemIndex < lineItemCount && itemIndex >= 0) {
			Gstr7TransDocItemEntity lineItem = lineItems.get(itemIndex);
			Integer lineNo = 0;
			if (lineItem.getLineItemNumber() != null) {
				lineNo = lineItem.getLineItemNumber();
			}
			return lineNo;
		}

		return null;
	}
}
