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
@Table(name = "TBL_GSTR9_TRANS_GSTR3B_SMRY")
public class Gstr9TransGstr3bSmryEntity {

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

	@Column(name = "TABLE_HEADING")
	private String tableHeading;

	@Column(name = "TABLE_DECSRIPTION")
	private String tableDesc;
	
	@Column(name = "POS")
	private String pos;

	@Column(name = "TAXABLE_VALUE")
	private BigDecimal txVal = BigDecimal.ZERO;

	@Column(name = "IGST_AMT")
	private BigDecimal igstAmt = BigDecimal.ZERO;

	@Column(name = "CGST_AMT")
	private BigDecimal cgstAmt = BigDecimal.ZERO;

	@Column(name = "SGST_AMT")
	private BigDecimal sgstAmt = BigDecimal.ZERO;

	@Column(name = "CESS_AMT")
	private BigDecimal cessAmt = BigDecimal.ZERO;

	@Column(name = "GSTR9_TABLE_TYPE")
	private String gstr9TableType;

	@Column(name = "GSTR9_FY")
	private String gstr9Fy;

	@Column(name = "GSTR9_COMPUTE_STATUS")
	private String gstr9ComputeStatus;

	@Column(name = "GSTR9_COMPUTE_DATE_TIME")
	private LocalDateTime gstr9CompDateTime;

	// Need to Change it to GSTR1 to GSTR3B
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
