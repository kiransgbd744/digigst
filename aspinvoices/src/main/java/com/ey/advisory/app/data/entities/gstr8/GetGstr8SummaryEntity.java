package com.ey.advisory.app.data.entities.gstr8;

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
 * @author Siva.Reddy
 *
 */
@Entity
@Table(name = "GETGSTR8_SUMMARY")
@Data
public class GetGstr8SummaryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "BATCH_ID")
	private String batchId;

	@Column(name = "GSTIN")
	private String gstIn;

	@Column(name = "RETURN_PERIOD")
	private String taxperiod;

	@Column(name = "CHKSUM")
	private String chkSum;

	@Column(name = "DEFAULT_INTEREST_AMT")
	private BigDecimal defaultIntAmt;

	@Column(name = "DERIVED_RET_PERIOD")
	private int deriviedReturnPeriod;

	@Column(name = "SECTION")
	private String section;

	@Column(name = "TOT_RECORD_CNT")
	private Integer totalRecord;

	@Column(name = "TOT_AMT_DEDUCTED")
	private BigDecimal totAmtDeducted;

	@Column(name = "TOT_IGST")
	private BigDecimal ttigst;

	@Column(name = "TOT_CGST")
	private BigDecimal ttcgst;

	@Column(name = "TOT_SGST")
	private BigDecimal ttsgst;

	@Column(name = "GROSS_SUPP_MADE")
	private BigDecimal grossSupMade;

	@Column(name = "GROSS_SUPP_RETURNED")
	private BigDecimal grossSupRet;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "IS_DELETE")
	private boolean isdelete;

}
