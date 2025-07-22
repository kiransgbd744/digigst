package com.ey.advisory.app.gstr2b;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Siva.Reddy
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "GET_GSTR2B_MANUAL_API_STATUS")
public class Gstr2BManualApiStatus {

	@Expose
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Expose
	@SerializedName("invocationId")
	@Column(name = "INVOCATION_ID")
	private Long invocationId;

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
	
	@Column(name = "FILE_CNT_STATUS")
	private String fileCntStatus;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "TAX_PERIOD")
	private String taxPeriod;

}
