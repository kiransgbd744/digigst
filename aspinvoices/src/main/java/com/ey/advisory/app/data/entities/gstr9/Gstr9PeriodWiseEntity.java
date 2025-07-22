/**
 * 
 */
package com.ey.advisory.app.data.entities.gstr9;

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
 * @author Arun.KA
 *
 */

@Data
@Entity
@Table(name = "TBL_GSTR9_READY_STATUS_PERIOD_WISE")
public class Gstr9PeriodWiseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Column(name = "GSTIN")
	private String gstin;
	
	@Column(name = "FY")
	private Integer fy;

	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;
	
	@Column(name = "DERIVED_RETURN_PERIOD")
	private Integer derivedReturnPeriod;

	@Column(name = "IS_GSTR1_GET_CALL_GREATER_FILING_DATE")
	private boolean isGstr1GetCallGreaterFilingDate;

	@Column(name = "IS_GSTR1_RETURN_FILED")
	private boolean isGstr1ReturnFiled;

	@Column(name = "GSTR1_RETURN_FILED_DT")
	private LocalDate gstr1ReturnFiledDt;

	@Column(name = "IS_GSTR1_GET_COMPLETED")
	private boolean isGstr1GetCompleted;

	@Column(name = "GSTR1_GET_DT")
	private LocalDate Gstr1GetDt;
	
	@Column(name = "IS_GSTR3B_GET_CALL_GREATER_FILING_DATE")
	private boolean isGstr3BGetCallGreaterFilingDate;

	@Column(name = "IS_GSTR3B_RETURN_FILED")
	private boolean isGstr3BReturnFiled;

	@Column(name = "GSTR3B_RETURN_FILED_DT")
	private LocalDate gstr3BReturnFiledDt;

	@Column(name = "IS_GSTR3B_GET_COMPLETED")
	private boolean isGstr3BGetCompleted;

	@Column(name = "GSTR3B_GET_DT")
	private LocalDate Gstr3BGetDt;
	
	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "UPDATED_ON")
	private LocalDateTime  updatedOn;

	public Gstr9PeriodWiseEntity(String gstin, Integer fy, String returnPeriod,
			Integer derivedReturnPeriod, String createdBy,
			LocalDateTime createdOn) {
		super();
		this.gstin = gstin;
		this.fy = fy;
		this.returnPeriod = returnPeriod;
		this.derivedReturnPeriod = derivedReturnPeriod;
		this.createdBy = createdBy;
		this.createdOn = createdOn;
	}
}
