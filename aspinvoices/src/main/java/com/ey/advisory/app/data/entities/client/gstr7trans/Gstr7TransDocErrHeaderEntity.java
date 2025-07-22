package com.ey.advisory.app.data.entities.client.gstr7trans;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.hibernate.annotations.Cascade;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Entity
@Table(name = "GSTR7_TRANS_ERR_HEADER")
public class Gstr7TransDocErrHeaderEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR7_TRANS_ERR_HEADER_SEQ", allocationSize = 1)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Column(name = "USER_ID")
	private String userId;

	@Column(name = "DEDUCTOR_GSTIN")
	private String deductorGstin;

	@Column(name = "FI_YEAR")
	private String fiYear;

	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;
	
	@Column(name = "DERIVED_RETURN_PERIOD")
	private Integer derivedRetPeriod;

	@Column(name = "SUPPLY_TYPE")
	private String supplyType;

	@Column(name = "DOC_TYPE")
	private String docType;

	@Column(name = "DOC_NUM")
	private String docNum;

	@Column(name = "DOC_DATE")
	private String docDate;

	@Column(name = "ORIGINAL_DOC_NUM")
	private String originalDocNum;

	@Column(name = "ORIGINAL_DOC_DATE")
	private String originalDocDate;

	@Column(name = "ORIGINAL_DOC_PERIOD")
	private String originalDocPeriod;

	@Column(name = "DEDUCTEE_GSTIN")
	private String deducteeGstin;

	@Column(name = "ORIGINAL_DEDUCTEE_GSTIN")
	private String originalDeducteeGstin;

	@Column(name = "ORIGINAL_RETURN_PERIOD")
	private String originalReturnPeriod;

	@Column(name = "ORIGINAL_DERIVED_RET_PERIOD")
	private String originalDerivedRetPeriod;

	@Column(name = "ORIGINAL_TAXABLE_VALUE")
	private String originalTaxableValue;
	
	@Column(name = "ORIGINAL_INVOICE_VALUE")
	private String originalInvoiceValue;

	@Column(name = "INVOICE_VALUE")
	private String invoiceValue;

	@Column(name = "DATAORIGINTYPECODE")
	private String dataOriginTypeCode;

	@Column(name = "BATCH_ID")
	private String batchId;

	@Column(name = "IS_ERROR")
	private String isError;

	@Column(name = "IS_PROCESSED")
	private String isProcessed;

	@Column(name = "IS_INFORMATION")
	private String isInformation;

	@Column(name = "IS_SAVED_TO_GSTN")
	private String isSavedToGstn;

	@Column(name = "IS_SENT_TO_GSTN")
	private String isSentToGstn;

	@Column(name = "IS_DELETE")
	private String isDelete;

	@Column(name = "GSTN_ERROR")
	private String gstnError;

	@Column(name = "DOC_KEY")
	private String docKey;

	@Column(name = "FILE_ID")
	private Long fileId;
	

	@Column(name = "SENT_TO_GSTN_DATE")
	private String sentToGstnDate;

	@Column(name = "SAVED_TO_GSTN_DATE")
	private String savedToGstnDate;

	@Column(name = "ERROR_CODES")
	private String errorCodes;

	@Column(name = "INFORMATION_CODES")
	private String informationCodes;

	@Column(name = "PAYLOAD_ID")
	private String payloadId;

	@Column(name = "IS_FILED")
	private String isFiled;

	@Column(name = "FILED_DATE")
	private String filedDate;

	@Column(name = "GSTN_SAVE_REF_ID")
	private String gstnSaveRefId;

	@Column(name = "CONTRACT_NUMBER")
	private String contractNumber;

	@Column(name = "CONTRACT_DATE")
	private String contractDate;

	@Column(name = "CONTRACT_VALUE")
	private String contractValue;

	@Column(name = "PAYMENT_ADVICE_NUMBER")
	private String paymentAdviceNumber;

	@Column(name = "PAYMENT_ADVICE_DATE")
	private String paymentAdviceDate;

	@Column(name = "SOURCE_IDENTIFIER")
	private String sourceIdentifier;

	@Column(name = "SOURCE_FILENAME")
	private String sourceFilename;

	@Column(name = "DIVISION")
	private String division;
	
	@Column(name = "PLANT_CODE")
	private String plantCode;

	@Column(name = "PROFIT_CENTRE1")
	private String profitCentre1;

	@Column(name = "PROFIT_CENTRE2")
	private String profitCentre2;
	
	@Column(name = "USERDEFINED_FIELD1")
	private String userdefinedField1;

	@Column(name = "USERDEFINED_FIELD2")
	private String userdefinedField2;

	@Column(name = "USERDEFINED_FIELD3")
	private String userdefinedField3;

	@Column(name = "USERDEFINED_FIELD4")
	private String userdefinedField4;

	@Column(name = "USERDEFINED_FIELD5")
	private String userdefinedField5;

	@Column(name = "USERDEFINED_FIELD6")
	private String userdefinedField6;

	@Column(name = "USERDEFINED_FIELD7")
	private String userdefinedField7;

	@Column(name = "USERDEFINED_FIELD8")
	private String userdefinedField8;

	@Column(name = "USERDEFINED_FIELD9")
	private String userdefinedField9;

	@Column(name = "USERDEFINED_FIELD10")
	private String userdefinedField10;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "SECTION")
	private String section;

	@Column(name = "TAXABLE_VALUE")
	private String taxableValue;

	@Column(name = "IGST_AMT")
	private String igstAmt;

	@Column(name = "CGST_AMT")
	private String cgstAmt;

	@Column(name = "SGST_AMT")
	private String sgstAmt;

	@Expose
	@SerializedName("lineItems")
	@OneToMany(mappedBy = "document", fetch = FetchType.EAGER)
	@OrderColumn(name = "ITEM_INDEX")
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	protected List<Gstr7TransDocErrItemEntity> lineItems = new ArrayList<>();

	public Integer getItemNoForIndex(Integer itemIndex) {

		int lineItemCount = this.lineItems.size();
		if (itemIndex < lineItemCount && itemIndex >= 0) {
			Gstr7TransDocErrItemEntity lineItem = lineItems.get(itemIndex);
			try {
				return Integer.valueOf(lineItem.getLineItemNumber());
			} catch (Exception e) {
				if (lineItem.getLineItemNumber() == null
						|| lineItem.getLineItemNumber().trim().isEmpty()) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("lineItem null or empty ");
					}
					LOGGER.error("Line item error " + e.getMessage());
					return null;
				} else {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"lineItem " + lineItem.getLineItemNumber());
					}
					LOGGER.error("Line item error " + e.getMessage());
					return -1;
				}
			}
		}
		return null;
	}
}
