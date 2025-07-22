/**
 * 
 */
package com.ey.advisory.app.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Siva.Reddy
 *
 */
@Entity
@Getter
@Setter
@ToString
@Table(name = "BCAPI_PUSH_CTRL_DETAILS")
public class BCAPIPushCtrlEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	protected Long id;

	@Column(name = "BATCH_ID")
	protected String batchId;

	@Column(name = "FROM_ID")
	protected Long fromId;

	@Column(name = "TO_ID")
	protected Long toId;

	@Column(name = "PAYLOAD_SIZE")
	protected String payloadSize;

	@Column(name = "PUSH_STATUS")
	protected String pushStatus;

	@Column(name = "BATCH_CREATED_DATE")
	protected LocalDateTime batchCreatedDate;

	@Column(name = "BATCH_PUSHED_DATE")
	protected LocalDateTime batchPushedDate;

	@Column(name = "BATCH_COMPLETED_DATE")
	protected LocalDateTime batchCompletedDate;

	@Lob
	@Column(name = "RESP_PAYLOAD")
	protected String respPayload;

	@Lob
	@Column(name = "REQ_PAYLOAD")
	protected String reqPayload;

	@Column(name = "IS_RETRY")
	protected boolean isRetry;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modfOn;

}
