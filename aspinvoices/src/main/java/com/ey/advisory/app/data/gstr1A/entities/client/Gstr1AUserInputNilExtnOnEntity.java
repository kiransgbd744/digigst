package com.ey.advisory.app.data.gstr1A.entities.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "GSTR1A_USERINPUT_NILEXTNON")
public class Gstr1AUserInputNilExtnOnEntity {

	@Expose
	@SerializedName("id")
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR1A_USERINPUT_NILEXTNON_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Column(name = "DERIVED_RET_PERIOD")
	private int derivedRetPeriod;

	@Column(name = "SUPPLIER_GSTIN")
	private String supplierGstin;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "DESCRIPTION_KEY")
	private String descriptioKey;

	@Column(name = "NIL_RATED_SUPPLIES")
	private BigDecimal nilRatedSupplies = BigDecimal.ZERO;

	@Column(name = "EXMPTED_SUPPLIES")
	private BigDecimal exmptedSupplies = BigDecimal.ZERO;

	@Column(name = "NON_GST_SUPPLIES")
	private BigDecimal nonGstSupplies = BigDecimal.ZERO;

	@Column(name = "AUDIT_TRAIL_VERSION")
	private String auditTrailVersion;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "CREATE_BY")
	private String createdBy;

	@Column(name = "CREATE_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "DOC_KEY")
	private String docKey;
	
	@Expose
	@SerializedName("isSaved")
	@Column(name = "IS_SAVED_TO_GSTN")
	private boolean isSaved;

	@Expose
	@SerializedName("isSent")
	@Column(name = "IS_SENT_TO_GSTN")
	private boolean isSent;

	@Expose
	@SerializedName("sentToGSTNDate")
	@Column(name = "SENT_TO_GSTN_DATE")
	private LocalDate sentToGSTNDate;

	@Expose
	@SerializedName("savedToGSTNDate")
	@Column(name = "SAVED_TO_GSTN_DATE")
	private LocalDate savedToGSTNDate;

	@Expose
	@SerializedName("isGstnError")
	@Column(name = "GSTN_ERROR")
	private boolean isGstnError;

	@Expose
	@SerializedName("gstnBatchId")
	@Column(name = "BATCH_ID")
	private Long gstnBatchId;

}
