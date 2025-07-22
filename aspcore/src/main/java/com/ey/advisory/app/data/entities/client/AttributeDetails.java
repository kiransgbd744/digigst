/**
 * 
 */
package com.ey.advisory.app.data.entities.client;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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

/**
 * @author Laxmi.Salukuti
 *
 */
@Entity
@Table(name = "ITEM_ATTRIBUTES")
@Data
public class AttributeDetails {
	
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "ITEM_ATTRIBUTES_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Expose
	@SerializedName("suppGstin")
	@Column(name = "SUPPLIER_GSTIN")
	protected String sgstin;

	@Expose
	@SerializedName("returnPeriod")
	@Column(name = "RETURN_PERIOD")
	protected String taxperiod;
	
	@Expose
	@SerializedName("derivedTaxperiod")
	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedTaxperiod;

	@Expose
	@SerializedName("docType")
	@Column(name = "DOC_TYPE")
	protected String docType;

	@Expose
	@SerializedName("supplyType")
	@Column(name = "SUPPLY_TYPE")
	protected String supplyType;

	@Expose
	@SerializedName("docNo")
	@Column(name = "DOC_NUM")
	protected String docNo;

	@Expose
	@SerializedName("docDate")
	@Column(name = "DOC_DATE")
	protected LocalDate docDate;

	@Expose
	@SerializedName("attributeName")
	@Column(name = "ATTRIBUTE_NAME")
	protected String attributeName;

	@Expose
	@SerializedName("attributeValue")
	@Column(name = "ATTRIBUTE_VALUE")
	protected String attributeValue;

	@Expose
	@SerializedName("itemNo")
	@Column(name = "ITM_NO")
	protected Integer lineNo;

	@Column(name = "DOC_KEY")
	protected String docKey;

	@Expose
	@SerializedName("isDeleted")
	@Column(name = "IS_DELETE")
	protected boolean isDeleted;

	@Column(name = "CREATED_ON")
	protected LocalDateTime createdDate;

	@Column(name = "MODIFIED_ON")
	protected LocalDateTime updatedDate;

	@Column(name = "CREATED_BY")
	protected String createdBy;

	@Column(name = "MODIFIED_BY")
	protected String modifiedBy;
	
	@Column(name = "DOC_HEADER_ID")
	private Long docHeaderID;

	@ManyToOne // (fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "DOC_ITEM_ID", referencedColumnName = "ID", nullable = false)
	private OutwardTransDocLineItem attDetails;
	
	public AttributeDetails(){
		
	}

}
