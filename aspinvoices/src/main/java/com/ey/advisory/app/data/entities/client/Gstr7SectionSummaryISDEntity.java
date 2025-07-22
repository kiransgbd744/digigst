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
 * @author Siva
 *
 */
@Entity
@Table(name = "GETGSTR7_SECTIONWISE_SUMMARY_TDS")
@Data
public class Gstr7SectionSummaryISDEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	
	@Column(name="BATCH_ID")
	private String batchId;
	
	@Column(name = "GSTIN")
	private String gstIn;

	@Column(name = "RETURN_PERIOD")
	private String taxperiod;

	@Column(name = "DERIVED_RET_PERIOD")
	private int deriviedReturnPeriod;

	@Column(name = "SECTION_NAME")
	private String sectionName;

	@Column(name = "TOT_RECORD")
	private Integer totalRecord;
	@Column(name = "TOT_TAX")
	private BigDecimal totalTax;

	@Column(name = "TOT_IGST")
	private BigDecimal ttigst;

	@Column(name = "TOT_CGST")
	private BigDecimal ttcgst;

	@Column(name = "TOT_SGST")
	private BigDecimal ttsgst;

	@Column(name = "TOT_AMT_DED")
	private BigDecimal totalAmtDed;

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
