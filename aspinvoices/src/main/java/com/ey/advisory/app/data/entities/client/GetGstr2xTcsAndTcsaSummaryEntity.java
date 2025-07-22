package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Entity
@Table(name = "GETGSTR2X_SUMMARY")
@Data
public class GetGstr2xTcsAndTcsaSummaryEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GETGSTR2X_SUMMARY_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;

	@Column(name = "GSTIN")
	protected String sgstin;

	@Column(name = "RET_PERIOD")
	protected String retPeriod;
	
	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derReturnPeriod;

	@Column(name = "CHKSUM")
	protected String chkSum;

	@Column(name = "RECORD_TYPE")
	protected String recordType;

	@Column(name = "RECORD_TYPE_PARAMTR")
	protected String recordTypeParameter;

	@Column(name = "TOT_RECORD")
	protected Integer totalRecords;

	@Column(name = "TOT_IGST")
	protected BigDecimal totalIgst;

	@Column(name = "TOT_CGST")
	protected BigDecimal totalCgst;

	@Column(name = "TOT_SGST")
	protected BigDecimal totaSgst;

	@Column(name = "TOT_VALUE")
	protected BigDecimal totalValue;

	@Column(name = "BATCH_ID")
	protected Long tcsAndTcsaBatchIdGstr2x;

	@Column(name = "IS_DELETE")
	protected boolean isDelete;

	@Column(name = "CREATED_ON")
	protected LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	protected String createdBy;

	@Column(name = "MODIFIED_ON")
	protected LocalDateTime modifiedOn;

	@Column(name = "MODIFIED_BY")
	protected String modifiedBy;
}
