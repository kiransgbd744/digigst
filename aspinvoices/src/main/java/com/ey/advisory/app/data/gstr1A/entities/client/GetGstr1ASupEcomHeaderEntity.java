package com.ey.advisory.app.data.gstr1A.entities.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Siva.Reddy
 *
 */

@Data
@Entity
@Table(name = "GETGSTR1A_SUPECOM_HEADER")
public class GetGstr1ASupEcomHeaderEntity {

	@Id
	// @SequenceGenerator(name = "sequence", sequenceName =
	// "GETGSTR1_SUPECOM_HEADER_SEQ", allocationSize = 100)
	// @GeneratedValue(generator = "sequence", strategy =
	// GenerationType.SEQUENCE)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	protected Long id;

	@Expose
	@SerializedName("gstin")
	@Column(name = "GSTIN")
	protected String gstin;

	@Expose
	@SerializedName("batchId")
	@Column(name = "BATCH_ID")
	protected Long batchId;

	@Expose
	@SerializedName("ret_period")
	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Expose
	@SerializedName("derivedTaxperiod")
	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedTaxperiod;

	@Expose
	@SerializedName("tableSection")
	@Column(name = "TABLE_SECTION")
	protected String tableSection;

	@Expose
	@SerializedName("flag")
	@Column(name = "FLAG")
	protected String flag;

	@Expose
	@SerializedName("etin")
	@Column(name = "ETIN")
	protected String etin;

	@Expose
	@SerializedName("suppval")
	@Column(name = "SUPPLIER_VALUE")
	protected BigDecimal suppVal;

	@Expose
	@SerializedName("igst")
	@Column(name = "IGST_AMT")
	protected BigDecimal igstAmt;

	@Expose
	@SerializedName("cgst")
	@Column(name = "CGST_AMT")
	protected BigDecimal cgstAmt;

	@Expose
	@SerializedName("sgst")
	@Column(name = "SGST_AMT")
	protected BigDecimal sgstAmt;

	@Expose
	@SerializedName("cess")
	@Column(name = "CESS_AMT")
	protected BigDecimal cessAmt;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Expose
	@SerializedName("docKey")
	@Column(name = "DOC_KEY")
	protected String docKey;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	// @OneToMany(mappedBy = "document")
	// @OrderColumn(name = "ITEM_INDEX")
	// @LazyCollection(LazyCollectionOption.FALSE)
	// @Cascade({ org.hibernate.annotations.CascadeType.ALL })
	// protected List<GetGstr1SupEcomItemEntity> lineItems = new ArrayList<>();

}
