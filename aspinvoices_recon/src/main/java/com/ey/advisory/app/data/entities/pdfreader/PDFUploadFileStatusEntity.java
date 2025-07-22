package com.ey.advisory.app.data.entities.pdfreader;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Akhilesh.Yadav
 *
 */
@Data
@Entity
@Table(name = "PDF_UPLOAD_FILE_STATUS")
public class PDFUploadFileStatusEntity {

	@Expose
	@SerializedName("fileId")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Expose
	@SerializedName("uploadedBy")
	@Column(name = "UPLOADED_BY")
	private String uploadedBy;

	@Expose
	@SerializedName("dateOfUpload")
	@Column(name = "DATE_OF_UPLOAD")
	private LocalDate dateOfUpload;

	@Expose
	@SerializedName("fileName")
	@Column(name = "FILE_NAME")
	private String fileName;

	@Expose
	@SerializedName("fileStatus")
	@Column(name = "FILE_STATUS")
	private String fileStatus;

	@Expose
	@SerializedName("filePath")
	@Column(name = "FILE_PATH")
	private String filePath;

	@Expose
	@SerializedName("totUplDoc")
	@Column(name = "TOTAL_UPLOAD_DOC_COUNT")
	private int totUplDoc;
	
	@Expose
	@SerializedName("countOfMisMat")
	@Column(name = "COUNT_OF_MISMATCHES")
	private int countOfMisMat;

	@Expose
	@SerializedName("createdBy")
	@Column(name = "CREATED_BY")
	private String createdBy;

	@Expose
	@SerializedName("createdOn")
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Expose
	@SerializedName("updatedOn")
	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;

	@Expose
	@Column(name = "START_TIME")
	private LocalDateTime startTime;

	@Expose
	@Column(name = "END_TIME")
	private LocalDateTime endTime;

	@Expose
	@SerializedName("updatedBy")
	@Column(name = "UPDATED_BY")
	private String updatedBy;

	@Expose
	@SerializedName("source")
	@Column(name = "SOURCE")
	private String source;

	@Column(name = "DOC_ID")
	private String docId;
	
	@Expose
	@SerializedName("entityId")
	@Column(name = "ENTITY_ID")
	private Long entityId;

}

