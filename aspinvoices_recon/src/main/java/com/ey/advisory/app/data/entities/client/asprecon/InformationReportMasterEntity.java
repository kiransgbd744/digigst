package com.ey.advisory.app.data.entities.client.asprecon;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author vishal.verma
 *
 */

@Entity
@Setter
@Getter
@ToString
@Table(name = "TBL_INFORMATION_REPORT_MASTER")
public class InformationReportMasterEntity {

	@Id
	@Column(name = "INFORMATION_REPORT_ID")
	private Integer infoReportId;
	
	@Column(name = "INFORMATION_REPORT_NAME")
	private String infoReportName;
	
	@Column(name = "CREATED_DATE")
	private LocalDateTime createdDate;
	
	@Column(name = "MODIFIED_DATE")
	private LocalDateTime modifiedDate;
	
	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Column(name = "MODIFIED_BY")
	private String modifiedBy;
	
	@Column(name = "IS_ACTIVE")
	private Boolean isActive;
	
	@Column(name = "IS_DELETE")
	private Boolean isDeleted;
	
	
}


