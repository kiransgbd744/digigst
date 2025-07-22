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
@Table(name = "TBL_GSTR9_TRANS_B2CS_B2CSA")
public class Gstr9TransB2CSB2CSAEntity {

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

	@Column(name = "ORG_RET_PERIOD")
	private String orgRetPeriod;

	@Column(name = "SUPPLY_TYPE")
	private String supplyType;

	@Column(name = "STATE_NAME")
	private String stateName;

	@Column(name = "POS")
	private String pos;

	@Column(name = "ORG_POS")
	private String orgPos;

	@Column(name = "TAX_RATE")
	private BigDecimal taxRt = BigDecimal.ZERO;

	@Column(name = "DIFF_PERCENT_RATE")
	private BigDecimal diffPercRt = BigDecimal.ZERO;

	@Column(name = "TYPE")
	private String type;

	@Column(name = "ECOM_GSTIN")
	private String ecomGstin;

	@Column(name = "GSTR9_TABLE_TYPE")
	private String gstr9TableType;

	@Column(name = "GSTR9_FY")
	private String gstr9Fy;

	@Column(name = "ORG_TAXABLE_VALUE")
	private BigDecimal orgTxVal = BigDecimal.ZERO;

	@Column(name = "ORG_IGST_AMT")
	private BigDecimal orgIgstAmt = BigDecimal.ZERO;

	@Column(name = "ORG_CGST_AMT")
	private BigDecimal orgCgstAmt = BigDecimal.ZERO;

	@Column(name = "ORG_SGST_AMT")
	private BigDecimal orgSgstAmt = BigDecimal.ZERO;

	@Column(name = "ORG_CESS_AMT")
	private BigDecimal orgCessAmt = BigDecimal.ZERO;

	@Column(name = "ORG_INV_VAL")
	private BigDecimal orgInvVal = BigDecimal.ZERO;

	@Column(name = "REV_TAXABLE_VALUE")
	private BigDecimal revTxVal = BigDecimal.ZERO;

	@Column(name = "REV_IGST_AMT")
	private BigDecimal revIgstAmt = BigDecimal.ZERO;

	@Column(name = "REV_CGST_AMT")
	private BigDecimal revCgstAmt = BigDecimal.ZERO;

	@Column(name = "REV_SGST_AMT")
	private BigDecimal revSgstAmt = BigDecimal.ZERO;

	@Column(name = "REV_CESS_AMT")
	private BigDecimal revCessAmt = BigDecimal.ZERO;

	@Column(name = "NET_TAXABLE_VALUE")
	private BigDecimal netTxVal = BigDecimal.ZERO;

	@Column(name = "NET_IGST_AMT")
	private BigDecimal netIgstAmt = BigDecimal.ZERO;

	@Column(name = "NET_CGST_AMT")
	private BigDecimal netCgstAmt = BigDecimal.ZERO;

	@Column(name = "NET_SGST_AMT")
	private BigDecimal netSgstAmt = BigDecimal.ZERO;

	@Column(name = "NET_CESS_AMT")
	private BigDecimal netCessAmt = BigDecimal.ZERO;

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
