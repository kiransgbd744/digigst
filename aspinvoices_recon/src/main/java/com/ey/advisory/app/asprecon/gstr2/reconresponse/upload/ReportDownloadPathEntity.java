package com.ey.advisory.app.asprecon.gstr2.reconresponse.upload;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author vishal.verma
 *
 */

@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Entity
@Table(name = "TBL_APPDB_LOG")
public class ReportDownloadPathEntity {
	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name  = "ID")
	private Long id;
	
	@Column(name  = "BATCH_ID")
	private Long batchId;
	
	@Column(name  = "ERROR_PATH")
	private String errorPath;
	
	@Column(name  = "PSDPATH")
	private String proceesedPath;
	
	@Column(name  = "TOTALPATH")
	private String totalPath;
	
	@Column(name  = "RUN_DATE")
	private LocalDateTime createdOn;
	
	@Column(name  = "FILE_ID")
	private Long fileId;
	
	@Column(name  = "INFO_PATH")
	private String infoPath;
}
