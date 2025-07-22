/**
 * 
 */
package com.ey.advisory.app.ims.handlers;

import java.time.LocalDateTime;

/**
 * @author vishal.verma
 *
 */
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "TBL_IMS_ERP_REQUEST")
public class ImsERPRequestEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "REQUEST_ID")
	private Long requestId;

	@Column(name = "BATCH_ID")
	private Long batchId;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "CHECKSUM")
	private String checkSum;

	@Column(name = "SUPPLY_TYPE")
	private String supplyType;
	
	@Column(name = "SRC_FILE_STATUS")
	private String srcFileStatus;

	@Column(name = "ERROR_DESC")
	private String errorDescription;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;

	@Column(name = "IS_ERP_PUSH")
	private Boolean isErpPush;

	@Column(name = "ERP_PUSH_DATE")
	private LocalDateTime erpPushDate;

	@Column(name = "TOTAL_RECORD")
	private Long totalRecord;

	
}
