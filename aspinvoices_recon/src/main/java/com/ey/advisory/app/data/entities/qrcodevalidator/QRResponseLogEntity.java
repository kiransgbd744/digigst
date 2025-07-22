package com.ey.advisory.app.data.entities.qrcodevalidator;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Data
@Entity
@Table(name = "QR_RESPONSE_LOG")
public class QRResponseLogEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Column(name = "FILE_ID")
	private Long fileId;

	@Column(name = "FILE_NAME")
	private String fileName;

	@Lob
	@Column(name = "QR_RESPONSE")
	private String qrResponse;

	@Lob
	@Column(name = "PDF_RESPONSE")
	private String pdfResponse;

	@Lob
	@Column(name = "QRPDF_RECON_RESPONSE")
	private String qrPdfReconResponse;

	@Lob
	@Column(name = "JSONPDF_RECON_RESPONSE")
	private String jsonPdfReconResponse;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;
	//add new column json clob
	@Lob
	@Column(name = "IRN_JSON_RESPONSE")
	private String irnJsonResponse;




}
