package com.ey.advisory.app.service.ims.supplier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "TBL_GETIMS_GSTR1A_CDNR_HEADER")
@Data
public class SupplierImsGstr1ACDNRHeaderEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "TBL_GETIMS_GSTR1A_CDNR_HEADER_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Column(name = "SUPPLIER_GSTIN")
	private String supplierGstin;

	@Column(name = "CUSTOMER_GSTIN")
	private String customerGstin;

	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Column(name = "DERIVED_RET_PERIOD")
	private Long derivedRetPeriod;

	/*
	 * @Column(name = "CFS") private String cfs;
	 */

	@Column(name = "INVOICE_STATUS")
	private String invoiceStatus;

	/*
	 * @Column(name = "INVOICE_NUMBER") private String invoiceNumber;
	 */

	@Column(name = "INV_TYPE")
	private String invoiceType;

	@Column(name = "INVOICE_DATE")
	private Date invoiceDate;

	/*
	 * @Column(name = "INVOICE_VALUE") private BigDecimal invoiceValue;
	 */

	@Column(name = "POS")
	private String pos;

	@Column(name = "REVERSE_CHARGE")
	private String reverseCharge;

	@Column(name = "ETIN")
	private String eTin;

	@Column(name = "COUNTER_PARTY_FLAG")
	private String counterPatryFlag;

	@Column(name = "ORG_PERIOD")
	private String orgPeriod;

	@Column(name = "SRC_IRN")
	private String srcIrn;

	@Column(name = "IRN_NUM")
	private String irnNum;

	@Column(name = "IRN_GEN_DATE")
	private Date irnGenDate;

	@Column(name = "DIFF_PERCENTAGE")
	private BigDecimal diffPercentage;

	@Column(name = "IMS_ACTION")
	private String imsAction;

	@Column(name = "UPLOADED_BY")
	private String uploadedBy;

	@Column(name = "IMS_UNIQUE_ID")
	private String imsUniqueId;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "DELTA_INV_STATUS")
	private String deltaInvStatus;

	@Column(name = "INVOICE_KEY")
	private String invoiceKey;

	@Column(name = "IS_DELETE")
	private Boolean isDelete;

	@Column(name = "BATCH_ID")
	private Long batchId;

	@Column(name = "CHECKSUM")
	private String chksum;

	@Column(name = "NOTE_TYPE")
	private String noteType;

	@Column(name = "NOTE_NUMBER")
	private String noteNo;

	@Column(name = "NOTE_DATE")
	private Date noteDate;

	@Column(name = "P_GST")
	private String pGst;

	@Column(name = "ORG_INVOICE_NUMBER")
	private String orgInvoiceNo;

	@Column(name = "TOTAL_NOTE_VALUE")
	private BigDecimal totNoteVal;

	@Column(name = "DELINK_FLAG")
	private String delinkFlag;

	@OneToMany(mappedBy = "header")
	@OrderColumn(name = "ITEM_INDEX")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	protected List<SupplierImsGstr1ACDNRItemEntity> lineItems = new ArrayList<>();

}
