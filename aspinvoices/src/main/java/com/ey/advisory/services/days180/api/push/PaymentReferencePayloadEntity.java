/**
 * 
 */
package com.ey.advisory.services.days180.api.push;

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
 * @author vishal.verma
 *
 */
@Entity
@Getter
@Setter
@ToString
@Table(name = "TBL_PAYLOAD_METADATA_PAYMENT_REFERENCE_180_DAYS")
public class PaymentReferencePayloadEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "PAYLOAD_ID")
	private String payloadId;

	@Column(name = "ERROD_CODE")
	private String errorCode;

	@Column(name = "JSON_ERROR_RESPONSE")
	private String jsonErrorResponse;

	@Column(name = "NOTIFICATION_STATUS")
	private String notificationStatus;
	
	@Column(name = "REVERSE_INT_PUSH_STATUS")
	private Integer revIntPushStatus;

	@Column(name = "COMPANY_CODE")
	private String companyCode;

	@Column(name = "DOC_COUNT")
	private Integer docCount;

	@Column(name = "PUSH_TYPE")
	private Integer pushType;

	@Column(name = "CHECK_SUM")
	private String checkSum;

	@Column(name = "CLOUD_CHECK_SUM")
	private String cloudCheckSum;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "ERROR_COUNT")
	private Integer errorCount;

	@Column(name = "TOTAL_COUNT")
	private Integer totalCount;

	@Column(name = "DERIVED_SOURCE_ID")
	private String derivedSourceId;

	@Column(name = "CREATED_ON")
	protected LocalDateTime createdOn;

	@Column(name = "MODIFIED_ON")
	protected LocalDateTime modifiedOn;

	@Column(name = "CREATED_BY")
	protected String createdBy;

	@Column(name = "MODIFIED_BY")
	protected String modifiedBy;
	
	@Lob
	@Column(name = "REQ_PAYLOAD")
	protected String reqPlayload;

}
