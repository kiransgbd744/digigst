package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 
 * @author Dibyakanta.Sahoo
 *
 */
@Entity
@Table(name = "GETGSTR6_REFUND_LATEFEE_OFFSET")
@Data
public class Getgstr6LateFeeOffSetEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private long id;

	@Column(name = "BATCH_ID")
	private String batchId;

	@Column(name = "GSTIN")
	private String gstIn;

	@Column(name = "TAX_PERIOD")
	private String taxperiod;

	@Column(name = "CHKSUM")
	private String chksum;

	@Column(name = "FEE_VALUE")
	private BigDecimal feeValue;

	@Column(name = "OTHERS_VALUE")
	private BigDecimal otherValue;

	@Column(name = "BANK_ACC_NUM")
	private String bankAccNum;

	@Column(name = "DEBIT_NUM")
	private String dbitNm;

	@Column(name = "CGST_AMT")
	private BigDecimal ttcgst;

	@Column(name = "SGST_AMT")
	private BigDecimal ttsgst;

	@Column(name = "LIABILITY_ID")
	private int liabilityId;

	@Column(name = "TRANSCATION_CODE")
	private int transactionCode;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "DERIVED_RET_PERIOD")
	private int deriviedReturnPeriod;

	@Column(name = "IS_DELETE")
	private boolean isdelete;

	/*
	 * @OneToMany(mappedBy = "headerId",cascade = CascadeType.ALL)
	 * // @JoinColumn(name = "SEC_SUMM_ID"); private
	 * Set<GetAnx2ActionSummaryEntity> secSumId;
	 */

}
