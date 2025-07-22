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
import jakarta.persistence.Table;

import lombok.Data;

/**
 * @author Laxmi.Salukuti
 *
 */
@Entity
@Data
@Table(name = "PAYLOAD_METADATA_DOCSERIES")
public class InvDocSeriesPayloadEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "CNTRL_SGSTIN")
	private String controlSgstin;

	@Column(name = "CNTRL_TAX_PERIOD")
	private String controlRetPeriod;

	@Column(name = "COMPANY_CODE")
	private String companyCode;

	@Column(name = "CNTRL_COUNT")
	private Integer controlCount;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "CNTRL_PAYLOADID")
	private String controlPayloadID;

	@Column(name = "CNTRL_CHKSUM")
	private String controlCheckSum;

	@Column(name = "ERROR_CODE")
	private String errorCode;

	@Column(name = "JSON_ERROR_RESPONSE")
	private String jsonErrorResponse;

	@Column(name = "CREATED_ON")
	protected LocalDateTime createdOn;

	@Column(name = "MODIFIED_ON")
	protected LocalDateTime modifiedOn;

	@Column(name = "CREATED_BY")
	protected String createdBy;

	@Column(name = "MODIFIED_BY")
	protected String modifiedBy;

}
