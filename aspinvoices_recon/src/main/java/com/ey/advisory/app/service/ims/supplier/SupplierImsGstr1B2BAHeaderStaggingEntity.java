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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Table(name = "TBL_GETIMS_STAGING_GSTR1_B2BA_HEADER")
@Data
public class SupplierImsGstr1B2BAHeaderStaggingEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "TBL_GETIMS_GSTR1_B2BA_HEADER_SEQ", allocationSize = 100)
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

    @Column(name = "CFS")
    private String cfs;

    @Column(name = "INVOICE_STATUS")
    private String invoiceStatus;

    @Column(name = "INVOICE_NUMBER")
    private String invoiceNumber;

    @Column(name = "INVOICE_TYPE")
    private String invoiceType;

    @Column(name = "INVOICE_DATE")
    private Date invoiceDate;

    @Column(name = "INVOICE_VALUE")
    private BigDecimal invoiceValue;

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
    
    @Column(name = "ORG_INVOICE_NUMBER")
    private String orgInvoiceNumber;
    
    @Column(name = "ORG_INVOICE_DATE")
    private Date orgInvoiceDate;
    
    @Column(name = "REMARKS")
    private String remarks;
    
    @Column(name = "BATCH_ID")
    private Long batchId;
    
    @Column(name = "IS_DELETE")
    private Boolean isDelete;
    
	@Column(name = "CHKSUM")
	private String chksum;
    
    @OneToMany(mappedBy = "header")
   	//@OrderColumn(name = "ITEM_INDEX")
   	@LazyCollection(LazyCollectionOption.FALSE)
   	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	protected List<SupplierImsGstr1B2BAItemStaggingEntity> lineItems = new ArrayList<>();

}
