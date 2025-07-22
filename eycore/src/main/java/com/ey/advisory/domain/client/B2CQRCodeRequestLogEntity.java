/**
 * 
 */
package com.ey.advisory.domain.client;

import java.sql.Clob;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "B2CQR_REQUEST_LOG")
public class B2CQRCodeRequestLogEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	protected Long id;

	@Column(name = "REQ_URL")
	protected String reqUrl;

	@Column(name = "DOC_HEADER_ID")
	protected Long docHeaderId;

	@Column(name = "PAN")
	protected String pan;

	@Column(name = "REQ_BODY")
	protected Clob reqPayload;

	@Column(name = "RESPONSE_PAYLOAD")
	protected String respPayload;

	@Column(name = "CREATED_ON")
	protected LocalDateTime createdOn;

	@Column(name = "REQ_RECEIVED_ON")
	protected LocalDateTime reqReceivedOn;

	@Column(name = "URL_CREATED_ON")
	protected LocalDateTime urlCreatedOn;

	@Column(name = "RESPONDED_ON")
	protected LocalDateTime respondedOn;

	@Column(name = "URL_STATUS")
	protected boolean urlStatus;

	@Column(name = "COMPANY_CODE")
	protected String companyCode;
	
	@Column(name = "DOC_KEY")
	protected String dockey;

}
