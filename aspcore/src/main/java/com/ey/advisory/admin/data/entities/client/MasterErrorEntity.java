package com.ey.advisory.admin.data.entities.client;

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

@Entity
@Table(name = "MASTER_ERROR")
@Setter
@Getter
@ToString
public class MasterErrorEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "MASTER_FILE_ID")
	private Long masterFileId;

	@Column(name = "ENTITY_ID")
	private Long entityId;

	@Column(name = "ERROR_RECORD")
	private String errorRecords;

	@Column(name = "ERROR_CODE")
	private String errorCode;

	@Column(name = "ERROR_DESCRIPTION")
	private String errorDec;

	@Column(name = "ERROR_KEY")
	protected String errorKey;

	@Column(name = "FILE_NAME")
	private String fileName;

	@Column(name = "FILE_TYPE")
	private String fileType;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "CREATED_BY")
	protected String createdBy;

	@Column(name = "CREATED_ON")
	protected LocalDateTime createdOn;

	@Column(name = "MODIFIED_BY")
	protected String modifiedBy;

	@Column(name = "MODIFIED_ON")
	protected LocalDateTime modifiedOn;

}