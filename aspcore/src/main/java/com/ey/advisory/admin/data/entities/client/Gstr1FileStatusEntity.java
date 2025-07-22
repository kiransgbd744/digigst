package com.ey.advisory.admin.data.entities.client;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;

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
 * 
 * @author Mahesh.Golla
 *
 */
@Entity
@Table(name = "FILE_STATUS")
@Setter
@Getter
@ToString
public class Gstr1FileStatusEntity {

	@Expose
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "FILE_STATUS_SEQ", allocationSize = 1)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Column(name = "FILE_NAME")
	private String fileName;

	@Column(name = "FILE_TYPE")
	private String fileType;

	@Column(name = "FILE_STATUS")
	private String fileStatus;

	@Column(name = "UPLOAD_START_TIME")
	private LocalDateTime startOfUploadTime;

	@Column(name = "UPLOAD_END_TIME")
	private LocalDateTime endOfUploadTime;

	@Column(name = "TOTAL_RECORDS")
	private Integer total;

	@Column(name = "PROCESSED_RECORDS")
	private Integer processed;

	@Column(name = "ERROR_RECORDS")
	private Integer error;

	@Column(name = "INFORMATION_RECORDS")
	private Integer information;

	@Column(name = "CREATED_BY")
	private String updatedBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime updatedOn;
	
	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "RECEIVED_DATE")
	protected LocalDate receivedDate;

	@Column(name = "SOURCE")
	private String source;

	@Column(name = "DATA_TYPE")
	private String dataType;
	
	@Column(name ="ENTITY_ID")
	private Long entityId;
	
	@Column(name = "ERROR_FILE_NAME")
	private String errorFileName;
	
	@Column(name = "FILE_HASH")
	private String fileHash;
	
	@Column(name = "ERROR_DESC") 
	private String errorDesc;
	
	@Column(name = "IS_REVERSE_INTEGRATION")
	private boolean isRevIntegration;
	
	@Column(name = "IS_CHILD_CREATED")
	private boolean isChildCreated;
	
	@Column(name = "PROCESSED_REPORT_ID")
	private Long processedRptId;
	
	@Column(name = "ERROR_REPORT_ID")
	private Long errorRptId;
	
	@Column(name = "INFO_RPEPORT_ID")
	private Long infoRptId;
	
	@Column(name = "DOC_ID") 
	private String docId;
	
	@Lob
	@Column(name = "REQ_PAYLOAD") 
	private String req;
	
	@Column(name = "TRANSFORMATION_STATUS")
	private String transformationStatus;
	
	@Column(name = "TRANSFORMATION_Rule")
	private String transformationRule;

	@Column(name = "UUID")
	private String uuid;
	
	@Column(name = "TRANSFORMATION_RULE_NAME")
	private String transformationRuleName;
	
	@Column(name = "ACTION_TAKEN")
	private String actionTaken;
	
}