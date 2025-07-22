package com.ey.advisory.app.data.entities.client;

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
@Table(name = "GETGSTR1_ECOMSUP_HEADER")
public class GetGstr1EcomSupHeaderEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	protected Long id;

	@Expose
	@SerializedName("gstin")
	@Column(name = "GSTIN")
	protected String gstin;

	@Column(name = "BATCH_ID")
	protected Long batchId;

	@Column(name = "RTIN")
	protected String rTin;

	@Column(name = "STIN")
	protected String sTin;

	@Expose
	@SerializedName("tableSection")
	@Column(name = "TABLE_SECTION")
	protected String tableSection;

	@Column(name = "SUPPLY_TYPE")
	private String supplyType;

	@Column(name = "INV_CHKSUM")
	private String chksum;

	@Column(name = "DOC_KEY")
	private String docKey;

	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedTaxperiod;

	@Column(name = "INV_NUM")
	private String invNum;

	@Column(name = "INV_TYPE")
	private String invType;

	@Column(name = "INV_DATE")
	private String invDate;

	@Column(name = "INV_VALUE")
	protected BigDecimal invValue;

	@Column(name = "POS")
	private String pos;

	@Column(name = "FLAG")
	private String flag;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "TAX_RATE")
	protected BigDecimal taxRate;

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

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@OneToMany(mappedBy = "document")
	@OrderColumn(name = "ITEM_INDEX")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	protected List<GetGstr1EcomSupItemEntity> lineItems = new ArrayList<>();
	
	@Override
	public String toString() {
	    return "GetGstr1EcomSupHeaderEntity{" +
	            "id=" + id +
	            ", gstin='" + gstin + '\'' +
	            ", batchId=" + batchId +
	            ", rTin='" + rTin + '\'' +
	            ", sTin='" + sTin + '\'' +
	            ", tableSection='" + tableSection + '\'' +
	            ", supplyType='" + supplyType + '\'' +
	            ", chksum='" + chksum + '\'' +
	            ", docKey='" + docKey + '\'' +
	            ", returnPeriod='" + returnPeriod + '\'' +
	            ", derivedTaxperiod=" + derivedTaxperiod +
	            ", invNum='" + invNum + '\'' +
	            ", invType='" + invType + '\'' +
	            ", invDate='" + invDate + '\'' +
	            ", invValue=" + invValue +
	            ", pos='" + pos + '\'' +
	            ", flag='" + flag + '\'' +
	            ", isDelete=" + isDelete +
	            ", taxRate=" + taxRate +
	            ", taxValue=" + taxValue +
	            ", igstAmt=" + igstAmt +
	            ", cgstAmt=" + cgstAmt +
	            ", sgstAmt=" + sgstAmt +
	            ", cessAmt=" + cessAmt +
	            ", createdOn=" + createdOn +
	            ", createdBy='" + createdBy + '\'' +
	            ", modifiedOn=" + modifiedOn +
	            ", modifiedBy='" + modifiedBy + '\'' +
	            ", lineItemsSize=" + (lineItems != null ? lineItems.size() : 0) + // Prevent recursion by printing size
	            '}';
	}


}
