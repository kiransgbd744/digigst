/**
 * 
 */
package com.ey.advisory.admin.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @author Sakshi.jain
 *
 */
@Entity
@Table(name = "GETGSTR2A_SFTP_PUSH_ERP_BATCHID_LOG")
@Data
public class AnxErpSFTPBatchIdLogEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "ERP_BATCH_ID")
	private Long erpBatchId;

	@Column(name = "BATCH_ID")
	private Long batchId;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;
	
	@Column(name = "STATUS")
	private String sftpPushStatus;
	
	@Column(name = "SECTION")
	private String section;
	
	@Column(name = "TAX_PERIOD")
	private String taxPeriod;
	
	@Column(name = "IS_DELETE")
	private boolean isDelete;
	
	@Column(name = "FILEINDEX")
	private String fileIndex;
	
}
