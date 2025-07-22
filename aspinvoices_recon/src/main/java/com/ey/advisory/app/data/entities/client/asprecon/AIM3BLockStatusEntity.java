/**
 * 
 */
package com.ey.advisory.app.data.entities.client.asprecon;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Entity
@Data
@Table(name = "TBL_AIM_3BLOCK_STATUS")
public class AIM3BLockStatusEntity {

	@Id
	@Column(name = "BATCHID")
	private Long batchId;

	@Column(name = "ENTITY_ID")
	private Long entityId;

	@Column(name = "TOTAL_RECORDS")
	private Integer totalRecords;

	@Column(name = "TOTAL_PSD")
	private Integer totalPsd;

	@Column(name = "TOTAL_ERR")
	private Integer totalError;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "COMPLETED_ON")
	private LocalDateTime completedOn;

	@Column(name = "PROC1_ERRMSG")
	private String errorMsgProc1;

	@Column(name = "PROC2_ERRMSG")
	private String errorMsgProc2;

}
