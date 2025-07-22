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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import lombok.Data;

/**
 * @author Ravindra
 *
 */

@Entity
@Table(name = "GETGSTR2B_STAGING_B2BA_HEADER")
@Data
public class GetGstr2bStagingB2baInvoicesHeaderEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GETGSTR2B_STAGING_B2BA_HEADER_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Column(name = "CHECKSUM")
	private String checksum;

	@Column(name = "RGSTIN")
	private String rGstin;

	@Column(name = "TAX_PERIOD")
	private String taxPeriod;

	@Column(name = "VERSION")
	private String version;

	@Column(name = "GENDT")
	private LocalDateTime genDate;

	@Column(name = "SGSTIN")
	private String sGstin;

	@Column(name = "SUPPLIER_TRADE_NAME")
	private String supTradeName;

	@Column(name = "SUPPLIER_FILING_DT")
	private LocalDateTime supFilingDate;

	@Column(name = "SUPPLIER_FILING_PRD")
	private String supFilingPeriod;

	@Column(name = "INV_NUM")
	private String invoiceNumber;

	@Column(name = "INV_DATE")
	private LocalDateTime invoiceDate;

	@Column(name = "INV_TYPE")
	private String invoiceType;

	@Column(name = "ORG_INV_NUM")
	private String orgInvoiceNumber;

	@Column(name = "ORG_INV_DATE")
	private LocalDateTime orgInvoiceDate;

	@Column(name = "SUPPLIER_INV_VAL")
	private BigDecimal supInvoiceValue;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "POS")
	private String pos;

	@Column(name = "REV")
	private String rev;

	@Column(name = "ITCAVAL")
	private String itcAvailable;

	@Column(name = "RSN")
	private String rsn;

	@Column(name = "DIFF_PERCENT")
	private BigDecimal diffPercent;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "IS_DELETE")
	private Boolean isDelete;
	
	@Column(name = "DOC_KEY")
	private String docKey;
	
	@Column(name = "LINKING_DOC_KEY")
	private String lnkingDocKey;

	@OneToMany(mappedBy = "header")
	@OrderColumn(name = "ITEM_INDEX")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	protected List<GetGstr2bStagingB2baInvoicesItemEntity> lineItemss = new ArrayList<>();

}
