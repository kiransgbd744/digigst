package com.ey.advisory.app.data.entities.client.gstr7trans;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "GSTR7_TRANS_ERR_ITEM")
public class Gstr7TransDocErrItemEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR7_TRANS_ERR_ITEM_SEQ", allocationSize = 1)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Column(name = "LINE_ITEM_NUMBER")
	private String lineItemNumber;

	@Column(name = "GL_ACCOUNT_CODE")
	private String glAccountCode;

	@Column(name = "SUB_DIVISION")
	private String subDivision;

	@Column(name = "PLANT_CODE")
	private String plantCode;

	@Column(name = "TAXABLE_VALUE")
	private String taxableValue;

	@Column(name = "IGST_AMT")
	private String igstAmt;

	@Column(name = "CGST_AMT")
	private String cgstAmt;

	@Column(name = "SGST_AMT")
	private String sgstAmt;

	@Column(name = "USERDEFINED_FIELD11")
	private String userdefinedField11;

	@Column(name = "USERDEFINED_FIELD12")
	private String userdefinedField12;

	@Column(name = "USERDEFINED_FIELD13")
	private String userdefinedField13;

	@Column(name = "USERDEFINED_FIELD14")
	private String userdefinedField14;

	@Column(name = "USERDEFINED_FIELD15")
	private String userdefinedField15;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private String modifiedOn;

	@Column(name = "ITEM_INDEX")
	private String itemIndex;
	
	@Column(name = "DERIVED_RET_PERIOD")
	private Integer derRetPeriod;

	@Column(name = "ORIGINAL_TAXABLE_VALUE")
	private String originalTaxableValue;
	
	@Column(name = "ERROR_CODES")
	private String errorCodes;

	@Column(name = "IS_ERROR")
	private String isError;

	@Column(name = "IS_PROCESSED")
	private String isProcessed;

	@Column(name = "IS_DELETE")
	protected String isDeleted;

	@Column(name = "DOC_KEY")
	protected String docKey;
	
	@ManyToOne
	@JoinColumn(name = "DOC_HEADER_ID", referencedColumnName = "ID", nullable = false)
	private Gstr7TransDocErrHeaderEntity document;

	public Gstr7TransDocErrItemEntity() {
		// Currently, there is not additional data members here. We can add
		// if additional data has to be stored as part of outward document
		// line item.
	}
}
