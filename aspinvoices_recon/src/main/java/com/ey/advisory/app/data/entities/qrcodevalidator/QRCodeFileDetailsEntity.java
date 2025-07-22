package com.ey.advisory.app.data.entities.qrcodevalidator;

import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Data
@Entity
@Table(name = "QR_CODE_FILE_DETAILS")
public class QRCodeFileDetailsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	private Long fileId;

	@Expose
	@SerializedName("fileName")
	@Column(name = "FILE_NAME")
	private String fileName;

	@Column(name = "FILE_PATH")
	private String filePath;
	
	@Column(name = "ERR_MSG")
	private String errMsg;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;
	
	@Transient
	private int sINo;
}
