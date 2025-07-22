package com.ey.advisory.app.data.entities.client;

import java.sql.Clob;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Entity
@Table(name = "TBL_GL_DUMP_API_UPLOAD")
@Setter
@Getter
@ToString
public class GlreconDumpApiUploadStatusEntity {

	@Id
	@Column(name = "REF_ID")
	private String refId;

	@Column(name = "API_STATUS")
	private String apiStatus;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;

	@Column(name = "ERROR_DESC")
	private String errorDesc;

	@Column(name = "REQUEST_PAYLOAD")
	private Clob reqPayload;

	@Column(name = "RESPONSE_PAYLOAD")
	private Clob respPayload;

}