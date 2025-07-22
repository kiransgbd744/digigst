package com.ey.advisory.app.data.entities.gstr9;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * @author Hema G M
 *
 */

@Data
@Entity
@Table(name = "TBL_GSTR9_FIN_YEAR_FILING_CAL")
public class Gstr9FinYearFilingCallEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	protected Long id;

	@Expose
	@Column(name = "FINANCIAL_YEAR")
	protected Integer fy;

	@Expose
	@Column(name = "FIN_YEAR_ST")
	protected Integer fyStart;

	@Expose
	@Column(name = "FIN_YEAR_END")
	protected Integer fyEnd;

	@Expose
	@Column(name = "LATE_FILED_FY_ST")
	protected Integer lateFiledFyStart;

	@Expose
	@Column(name = "LATE_FILED_FY_END")
	protected Integer lateFiledFyEnd;

	@Expose
	@Column(name = "CREATED_ON")
	protected LocalDateTime createdOn;

	@Expose
	@Column(name = "IS_DELETE")
	protected boolean isDelete;

}
