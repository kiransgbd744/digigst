package com.ey.advisory.app.data.gstr1A.entities.client;

import java.math.BigDecimal;
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
import jakarta.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

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
@Table(name = "GETGSTR1A_ECOMSUP_AMD_HEADER")
public class GetGstr1AEcomSupAmdHeaderEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	protected Long id;

	@Column(name = "BATCH_ID")
	protected Long batchId;

	@Column(name = "CTIN")
	protected String ctin;

	@Column(name = "ACTION")
	private String action;

	@Column(name = "FLAG")
	private String flag;

	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedTaxperiod;

	@Expose
	@SerializedName("tableSection")
	@Column(name = "TABLE_SECTION")
	protected String tableSection;

	@Column(name = "INV_DATE")
	private String invDate;

	@Column(name = "ORG_INV_NUM")
	private String orgInvNum;

	@Column(name = "ORG_INV_DATE")
	private String orgInvDate;

	@Column(name = "INV_VALUE")
	protected BigDecimal invValue;

	@Column(name = "ESTIMATED_TIME")
	private String estimTime;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "ETIN")
	private String etin;

	@Column(name = "DOC_KEY")
	private String docKey;

	@Column(name = "GSTIN")
	protected String gstin;

	@Column(name = "ORG_PERIOD")
	protected String orgPeriod;

	@Column(name = "ORG_SUPP_GSTIN")
	protected String orgSuppGstin;

	@Column(name = "ORG_CUST_GSTIN")
	protected String orgCustGstin;

	@Column(name = "TAXABLE_VALUE")
	protected BigDecimal taxValue;

	@Column(name = "IGST_AMT")
	protected BigDecimal igstAmt;

	@Column(name = "CGST_AMT")
	protected BigDecimal cgstAmt;

	@Column(name = "SGST_AMT")
	protected BigDecimal sgstAmt;

	@Column(name = "CESS_AMT")
	protected BigDecimal cessAmt;

	@Column(name = "POS")
	protected String pos;

	@Column(name = "INV_NUM")
	private String invNum;

	@Column(name = "INV_TYPE")
	private String invType;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "CHKSUM")
	private String chksum;

	@Column(name = "SUPPLY_TYPE")
	private String supplyType;

	@OneToMany(mappedBy = "document")
	@OrderColumn(name = "ITEM_INDEX")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	protected List<GetGstr1AEcomSupAmdItemEntity> lineItems = new ArrayList<>();

}
