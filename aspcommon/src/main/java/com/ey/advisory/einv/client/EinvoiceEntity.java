package com.ey.advisory.einv.client;

import java.sql.Clob;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @author Arun K.A
 *
 */
@Entity
@Table(name = "EINV_MASTER")
@Data
public class EinvoiceEntity {
	
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "EINV_MASTER_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "EINV_ID")
	private Long einvoiceId;
	
	@SerializedName("Irn")
	@Expose
	@Column(name = "IRN")
	private String irn;
	
	@SerializedName("SignedInvoice")
	@Expose
	@Column(name = "SIGNED_INV")
	private Clob signedInv;
	
	@SerializedName("SignedQRCode")
	@Expose
	@Column(name = "SIGNED_QR")
	private String signedQR;
	
	@SerializedName("AckNo")
	@Expose
	@Column(name = "ACK_NUM")
	private String ackNo;
	
	@SerializedName("AckDt")
	@Expose
	@Column(name = "ACK_DATE")
	private LocalDateTime ackDt;
	
	@Column(name = "STATUS_DESCRIPTION")
	private String statusDesc;
	
	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;
	
	@Column(name = "MODIFIED_BY")
	private String modifiedBy;
	
	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;
	
	@Column(name = "DOC_HEADER_ID")
	private Long docHeaderId;
	
	@Column(name = "IS_DELETE")
	private boolean isDelete;
	
	@Column(name = "QR_CODE")
	private String qrCode;
	
	@Column(name = "FORMATTED_QR_CODE")
	private String formattedQRCode;
	
	@Column(name = "CANCELLATION_DATE")
	private LocalDateTime cancelDate;
	
	@Column(name = "CANCELLATION_REASON")
	private String cancelReason;
	
	@Column(name = "CANCELLATION_REMARKS")
	private String cancelRemarks;

}
