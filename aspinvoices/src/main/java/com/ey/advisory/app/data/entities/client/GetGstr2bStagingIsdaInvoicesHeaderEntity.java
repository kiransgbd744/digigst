package com.ey.advisory.app.data.entities.client;

import java.time.LocalDateTime;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @author Ravindra
 *
 */

@Entity
@Table(name = "GETGSTR2B_ISDA_HEADER")
@Data
public class GetGstr2bStagingIsdaInvoicesHeaderEntity{

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GETGSTR2B_STAGING_ISDA_HEADER_SEQ", allocationSize = 100)
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
	private String suppSstin;

	@Column(name = "SUPPLIER_TRADE_NAME")
	private String supTradeName;

	@Column(name = "SUPPLIER_FILING_DT")
	private LocalDateTime supFilingDate;

	@Column(name = "SUPPLIER_FILING_PRD")
	private String supFilingPeriod;

	@Column(name = "DOC_NUM")
	private String docNumber;

	@Column(name = "DOC_DATE")
	private LocalDateTime docDate;

	@Column(name = "ISD_DOC_TYPE")
	private String isdDocType;

	@Column(name = "ORG_INV_NUM")
	private String orgInvoiceNumber;
	
	@Column(name = "ORG_INV_DATE")
	private LocalDateTime orgInvoiceDate;
	
	@Column(name = "ORG_DOC_DATE")
	private LocalDateTime orgDocDate;
	
	@Column(name = "ORG_DOC_TYPE")
	private String orgDocType;
	
	@Column(name = "ORG_DOC_NUM")
	private String orgdocNumber;
	
	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "IS_DELETE")
	private Boolean isDelete;
	
	@Column(name = "DOC_KEY")
	private String docKey;
	
	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "INVOCATION_ID")
	private Long invocationId;
	
	@OneToOne(mappedBy = "header")
	@OrderColumn(name = "ITEM_INDEX")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	protected GetGstr2bStagingIsdaInvoicesItemEntity lineItemss;

}
