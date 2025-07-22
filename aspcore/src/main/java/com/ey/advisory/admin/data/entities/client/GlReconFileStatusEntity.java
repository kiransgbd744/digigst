package com.ey.advisory.admin.data.entities.client;

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
 * 
 * @author Sakshi.jain
 *
 */
@Entity
@Table(name = "GL_RECON_FILE_STATUS")
@Setter
@Getter
@ToString
public class GlReconFileStatusEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "FILE_NAME")
	private String fileName;

	@Column(name = "GSTIN_IDS")
	private Clob gstins;

	@Column(name = "FILE_TYPE")
	private String fileType;

	@Column(name = "FILE_STATUS")
	private String fileStatus;

	@Column(name = "DOCUMENT_ID")
	private String docId;

	@Column(name = "UPLOAD_START_TIME")
	private LocalDateTime startOfUploadTime;

	@Column(name = "UPLOAD_END_TIME")
	private LocalDateTime endOfUploadTime;

	@Column(name = "CREATED_BY")
	private String craetedBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime updatedOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "ENTITY_ID")
	private Long entityId;

	@Column(name = "IS_DELETE")
	private Boolean isDelete;

	@Column(name = "IS_ACTIVE")
	private Boolean isActive;

	@Column(name = "SOURCE")
	private String source;

	@Column(name = "PAYLOAD_ID")
	private String payloadId;

	@Column(name = "TRANSFORMATION_Rule")
	private String transformationRule;
	
	@Column(name = "TRANSFORMATION_STATUS")
	private String transformationStatus;

	@Column(name = "TRANSFORMATION_RULE_NAME")
	private String transformationRuleName;
	
	 @Column(name = "ERROR_CODE")
	 private String errorCode;
	 
	 @Column(name = "ERROR_DESC")
	 private String errorDesc;
	 
	 @Column(name = "ERP_DMS_GL_DUMP_DOCID")
	 private String erpDmsGlDumpDocId;
	 
	 @Column(name = "ERP_PAYLOAD_ID")
	 private String erpPayloadId;

	 
	 @Column(name = "ERP_SOURCE_ID")
	 private String erpSourceId;
	 
	 @Column(name = "ERP_COMPANY_CODE")
	 private String erpCompanyCode;
	 

}