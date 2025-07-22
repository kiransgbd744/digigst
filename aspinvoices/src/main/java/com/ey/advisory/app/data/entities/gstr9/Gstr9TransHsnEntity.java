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
@Table(name = "TBL_GSTR9_TRANS_HSN")
public class Gstr9TransHsnEntity {

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

	@Column(name = "HSN")
	private Integer hsnNum;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "UQC")
	private String uqc;

	@Column(name = "TOTAL_QUANTIY")
	private Integer totalQuantity;

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

	@Column(name = "TOTAL_VAL")
	private BigDecimal totVal = BigDecimal.ZERO;

	@Column(name = "GSTR9_FY")
	private String gstr9Fy;

	@Column(name = "IS_FILED")
	private boolean isFiled;

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
