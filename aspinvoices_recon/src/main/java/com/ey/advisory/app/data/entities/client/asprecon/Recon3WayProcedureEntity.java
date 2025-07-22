package com.ey.advisory.app.data.entities.client.asprecon;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Sakshi.jain
 *
 */
@Entity
@Table(name = "TBL_3WAY_PROCEDURE")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Recon3WayProcedureEntity {

	@Id
	@Column(name = "RECON_PROCEDURE_ID")
	private Integer procedureId;

	@Column(name = "REPORT_TYPE")
	private String reportType;

	@Column(name = "DETAILED_REPORT_TYPE")
	private String detailedReportType;
	
	@Column(name = "PROCEDURE_NAME")
	private String procName;
	
	@Column(name = "SEQUENCE_OF_EXECUTION")
	private Integer seqId;
	
	@Column(name = "IS_GSTR1")
	private Boolean isGSTR1;

	@Column(name = "IS_EINV")
	private Boolean isEINV;

	@Column(name = "IS_EWB")
	private Boolean isEWB;

	@Column(name = "IS_ACTIVE")
	private Boolean isActive;
	
	@Column(name = "CREATED_DATE")
	private LocalDateTime createdDate;

	@Column(name = "MODIFIED_DATE")
	private LocalDateTime modifiedDate;

	@Column(name = "CREATED_BY")
	private Integer createdBy;
	
	@Column(name = "MODIFIED_BY")
	private Integer modifiedBy;
	
	
	
}
