package com.ey.advisory.app.data.entities.gstr9;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import lombok.Data;


/**
 * 
 * @author Siva.Reddy
 *
 */
@Data
@Entity
@Table(name = "TBL_GSTR9_TRANS_GSTR3B_TAX_PRD")
public class Gstr9TransGstr3bTaxPrdEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Column(name = "COMPUTE_ID")
	private Long computeId;

	@Column(name = "CHUNK_ID")
	private Long chunkId;

	@Column(name = "SGSTIN")
	private String sgstin;

	@Column(name = "RET_PERIOD")
	private String retPeriod;

	@Column(name = "TABLE_TYPE")
	private String tableType;

	@Column(name = "DESCRIPTION")
	private String descrp;

	@Column(name = "TAX_PAYABLE")
	private BigDecimal txPayable = BigDecimal.ZERO;

	@Column(name = "PAID_THROUGH_ITC_IGST")
	private BigDecimal paidThroItcIgst = BigDecimal.ZERO;

	@Column(name = "PAID_THROUGH_ITC_CGST")
	private BigDecimal paidThroItcCgst = BigDecimal.ZERO;

	@Column(name = "PAID_THROUGH_ITC_SGST")
	private BigDecimal paidThroItcSgst = BigDecimal.ZERO;

	@Column(name = "PAID_THROUGH_ITC_CESS")
	private BigDecimal paidThroItcCess = BigDecimal.ZERO;
	
	@Column(name = "PAID_IN_CASH")
	private BigDecimal paidInCash = BigDecimal.ZERO;

	@Column(name = "INTEREST")
	private BigDecimal interest = BigDecimal.ZERO;

	@Column(name = "LATE_FEE")
	private BigDecimal lateFee = BigDecimal.ZERO;

	@Column(name = "GSTR9_TABLE_TYPE")
	private String gstr9TableType;

	@Column(name = "GSTR9_FY")
	private String gstr9Fy;

	@Column(name = "GSTR9_COMPUTE_STATUS")
	private String gstr9ComputeStatus;

	@Column(name = "GSTR9_COMPUTE_DATE_TIME")
	private LocalDateTime gstr9CompDateTime;

	@Column(name = "GSTR3B_GET_CALL_STATUS")
	private String gstr3BGetCallStatus;

	@Column(name = "GSTR3B_GET_CALL_DATE_TIME")
	private LocalDateTime gstr3BGetCallDateTime;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Transient
	private int srNo;

}
