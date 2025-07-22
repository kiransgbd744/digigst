/**
 * 
 */
package com.ey.advisory.app.service.ims;

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
 * @author Shashikant.Shukla
 *
 */

@Entity
@Table(name = "TBL_IMS_SAVE_JOB_QUEUE")
@Data
public class ImsSaveJobQueueEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "TBL_GETIMS_QUEUE_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "SECTION")
	private String section;

	@Column(name = "JOB_POST_DATE_TIME")
	private LocalDateTime jobPostDateTime;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "BATCH_ID")
	private String batchId;

	@Column(name = "SAVE_RESPONSE")
	private String saveResponse;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "IS_ACTIVE")
	private Boolean isActive;

	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;
	
	@Column(name = "ACTION")
	private String action;

}
