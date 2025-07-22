package com.ey.advisory.app.reconewbvsitc04;

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
 * @author Ravindra V S
 *
 */
@Entity
@Table(name = "TBL_EWAYVSITC04_PROCEDURE")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EwbVsItc04ReconProcedureEntity {
	
	@Id
	@Column(name = "RECON_PROCEDURE_ID")
	private Integer procId;
	
	@Column(name = "DETAILED_REPORT_TYPE")
	private String detailedReportType;
	
	@Column(name = "REPORT_TYPE")
	private String reportType;
	
	@Column(name = "PROCEDURE_NAME")
	private String procName;
	
	@Column(name = "SEQUENCE_OF_EXECUTION")
	private Integer seqId;
	
	@Column(name = "CREATED_DATE")
	private LocalDateTime createdDate;
	
	@Column(name = "MODIFIED_DATE")
	private LocalDateTime modifiedDate;
	
	@Column(name = "IS_ACTIVE")
	private Boolean isActive;
	
	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Column(name = "MODIFIED_BY")
	private String modifiedby;
	
	
	
	
}
