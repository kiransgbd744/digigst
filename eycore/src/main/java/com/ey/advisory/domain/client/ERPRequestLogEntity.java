/**
 * 
 */
package com.ey.advisory.domain.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Sai.Pakanati
 *
 */
@Entity
@Getter
@Setter
@ToString
@Table(name = "ERP_REQUEST_LOG")
public class ERPRequestLogEntity {

	@Id	
	@SequenceGenerator(name = "sequence", sequenceName = "ERP_REQUEST_LOG_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;

	@Column(name = "REQ_URL")
	protected String reqUrl;

	@Column(name = "REQ_QUERY_PARAMS")
	protected String qryParams;

	@Column(name = "PAYLOAD_ID")
	protected String payloadId;

	@Column(name = "COMPANY_CODE")
	protected String companyCode;

	@Column(name = "CHECKSUM")
	protected String checkSum;

	@Column(name = "REQ_TYPE")
	protected String reqType;

	@Lob
	@Column(name = "REQ_BODY")
	protected String reqPayload;

	@Lob
	@Column(name = "NIC_REQ_PAYLOAD")
	protected String nicReqPayload;

	@Lob
	@Column(name = "NIC_RESP_PAYLOAD")
	protected String nicResPayload;
	
	@Lob
	@Column(name = "NIC_ERROR_RESP_PAYLOAD")
	protected String nicErrRespPayload;

	@Column(name = "NIC_STATUS")
	protected boolean nicStatus;

	@Column(name = "ERP_TIMESTAMP")
	protected LocalDateTime erpTimestamp;

	@Column(name = "CLOUD_TIMESTAMP")
	protected LocalDateTime cloudTimestamp;

	@Column(name = "NIC_RESP_TIMESTAMP")
	protected LocalDateTime nicRespTimestamp;

	@Column(name = "NIC_REQ_TIMESTAMP")
	protected LocalDateTime nicReqTimestamp;

	@Column(name = "NIC_RAW_RESP_TIMESTAMP")
	protected LocalDateTime nicRawRespTimestamp;

	@Column(name = "NIC_RETRY_REQ_TIMESTAMP")
	protected LocalDateTime nicRetryReqTimestamp;

	@Column(name = "NIC_RETRY_RAW_RESP_TIMESTAMP")
	protected LocalDateTime nicRetryRawRespTimestamp;

	@Column(name = "IS_DUPLICATE")
	protected boolean isDuplicate;

	@Column(name = "TIME_TAKEN_BY_NIC_SECS")
	protected Long timeTakenbyNICSecs;

	@Column(name = "BATCH_ID")
	protected String batchId;

	@Column(name = "API_TYPE")
	protected String apiType;

	@Column(name = "DOC_TYPE")
	protected String docType;

	@Column(name = "SGSTIN")
	protected String sGstin;

	@Column(name = "DOC_NUM")
	protected String docNum;

	@Column(name = "IRN_NUM")
	protected String irnNum;

	@Column(name = "ACK_NUM")
	private String ackNum;

	@Column(name = "ACK_DT")
	private LocalDateTime ackDate;

	@Column(name = "EWB_NUM")
	private String ewbNo;

	@Column(name = "EWB_DATE")
	private LocalDateTime ewbDate;

	@Column(name = "SOURCE_ID")
	protected String sourceId;

	@Column(name = "IDENTIFIER")
	protected String identifer;

	@Column(name = "SWITCH_IDENTIFIER")
	protected String switchIdentifer;
	
	@Column(name = "CLOUD_TIMESTAMP_IST")
	protected LocalDateTime cloudTimestampIst;
	
	@Column(name = "ORIGIN_REGION")
	protected String originRegion;

	@Column(name = "IS_AUTODRAFTED")
	protected boolean isAutoDrafted;

}
