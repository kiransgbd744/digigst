/**
 * 
 */
package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * @author Hemasundar.J
 *
 */
@Entity
@Data
@Table(name = "TRANSACTIONAL_MONITORING_INWARD")
public class TransactionalInwardEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "TAX_PERIOD")
	private String taxPeriod;

	@Column(name = "DERIVED_RET_PERIOD")
	private Integer derivedRetPeriod;
	
	@Column(name = "ENTITY_ID")
	private Long entityId;

	@Column(name = "DATE_OF_REPORTING")
	private LocalDate dateOfReporting;

	@Column(name = "SOURCE_ID")
	private String sourceId;

	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "DERIVED_STATUS")
	private String derivedStatus;

	@Column(name = "DOC_TYPE")
	private String docType;

	@Column(name = "ERP_NO_OF_INVOICES")
	private Integer erpNumOfInvoices;

	@Column(name = "ERP_TOTAL_INV_VALUE")
	private BigDecimal erpTotalInvValue;
	
	@Column(name = "ERP_ASSESSABLE_VALUE")
	private BigDecimal erpAssessableValue;

	@Column(name = "ERP_IGST_AMT")
	private BigDecimal erpIgstAmt;

	@Column(name = "ERP_CGST_AMT")
	private BigDecimal erpCgstAmt;

	@Column(name = "ERP_SGST_AMT")
	private BigDecimal erpSgstAmt;

	@Column(name = "ERP_CESS_AMT")
	private BigDecimal erpCessAmt;
	
	@Column(name = "CLOUD_NO_OF_INVOICES")
	private Integer cloudNumOfInvoices;

	@Column(name = "CLOUD_TOTAL_INV_VALUE")
	private BigDecimal cloudTotalInvValue;
	
	@Column(name = "CLOUD_ASSESSABLE_VALUE")
	private BigDecimal cloudAssessableValue;

	@Column(name = "CLOUD_IGST_AMT")
	private BigDecimal cloudIgstAmt;

	@Column(name = "CLOUD_CGST_AMT")
	private BigDecimal cloudCgstAmt;

	@Column(name = "CLOUD_SGST_AMT")
	private BigDecimal cloudSgstAmt;

	@Column(name = "CLOUD_CESS_AMT")
	private BigDecimal cloudCessAmt;
	
	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "IS_DELETE")
	private Boolean isDelete;


}
