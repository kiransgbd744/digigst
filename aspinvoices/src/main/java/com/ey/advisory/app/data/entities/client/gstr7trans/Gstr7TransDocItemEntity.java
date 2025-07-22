package com.ey.advisory.app.data.entities.client.gstr7trans;

import java.math.BigDecimal;
import java.time.LocalDate;
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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
@Entity
@Table(name = "GSTR7_TRANS_DOC_ITEM")
public class Gstr7TransDocItemEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR7_TRANS_DOC_ITEM_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Expose
	@SerializedName("lineItemNumber")
	@Column(name = "LINE_ITEM_NUMBER")
	private Integer lineItemNumber;

	@Expose
	@SerializedName("glAccountCode")
	@Column(name = "GL_ACCOUNT_CODE")
	private String glAccountCode;

	@Expose
	@SerializedName("subDivision")
	@Column(name = "SUB_DIVISION")
	private String subDivision;

	@Expose
	@SerializedName("plantCode")
	@Column(name = "PLANT_CODE")
	private String plantCode;

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
	@SerializedName("userdefinedField11")
	@Column(name = "USERDEFINED_FIELD11")
	private String userdefinedField11;

	@Expose
	@SerializedName("userdefinedField12")
	@Column(name = "USERDEFINED_FIELD12")
	private String userdefinedField12;

	@Expose
	@SerializedName("userdefinedField13")
	@Column(name = "USERDEFINED_FIELD13")
	private String userdefinedField13;

	@Expose
	@SerializedName("userdefinedField14")
	@Column(name = "USERDEFINED_FIELD14")
	private String userdefinedField14;

	@Expose
	@SerializedName("userdefinedField15")
	@Column(name = "USERDEFINED_FIELD15")
	private String userdefinedField15;

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
	private LocalDate modifiedOn;

	@Expose
	@SerializedName("itemIndex")
	@Column(name = "ITEM_INDEX")
	private Integer itemIndex;

	@Expose
	@SerializedName("taxperiod")
	@Column(name = "RETURN_PERIOD")
	protected String taxperiod;

	@Expose
	@SerializedName("derivedTaxperiod")
	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedTaxperiod;

	@Expose
	@SerializedName("originalTaxableValue")
	@Column(name = "ORIGINAL_TAXABLE_VALUE")
	private BigDecimal originalTaxableValue;

	@Expose
	@SerializedName("errorCodes")
	@Column(name = "ERROR_CODES")
	private String errorCodes;


	@ManyToOne // (fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "DOC_HEADER_ID", referencedColumnName = "ID", nullable = false)
	private Gstr7TransDocHeaderEntity document;

	public Gstr7TransDocItemEntity() {
		// Currently, there is not additional data members here. We can add
		// if additional data has to be stored as part of outward document
		// line item.
	}

}
