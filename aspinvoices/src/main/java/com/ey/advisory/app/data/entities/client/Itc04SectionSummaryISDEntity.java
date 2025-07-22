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
@Table(name = "GETITC04_SUMMARY")
@Data
public class Itc04SectionSummaryISDEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	
	@Column(name="BATCH_ID")
	private Long batchId;
	
	@Column(name="SUPPLIER_GSTIN")
	private String gstin;
	
	@Column(name="RETURN_PERIOD")
	private String returnPeriod;
	
	@Column(name="DERIVED_RET_PERIOD")
	private int devreturnPeriod;
	
	@Column(name = "TOT_REC_TABLE_4")
	private String totalRecTable4;

	
	@Column(name = "TOT_TAXABLE_VALUE_TABLE_4")
	private BigDecimal totalTaxableValue;
	
	@Column(name = "TOT_REC_TABLE_5A")
	private String totalRecTable5A;

	@Column(name = "TOT_REC_TABLE_5B")
	private String totalRecTable5B;

	@Column(name = "TOT_REC_TABLE_5C")
	private String totalRecTable5C;

	@Column(name = "CHKSUM_TABLE_4")
	private String checkSumTable4;
	
	@Column(name = "CHKSUM_TABLE_5A")
	private String checkSumTable5a;

	@Column(name = "CHKSUM_TABLE_5B")
	private String checkSumTable5b;

	@Column(name = "CHKSUM_TABLE_5C")
	private String checkSumTable5c;

	

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
