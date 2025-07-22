package com.ey.advisory.app.data.entities.client.asprecon;


import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Arun.KA
 *
 */

@Entity
@Table(name = "ANX2_USER_RESPONSE_STATUS")
@Setter
@Getter
@ToString
public class ReconUserResponseError {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	@Column(name = "ID")
	protected Long Id;

	@Expose
	@Column(name = "RECON_REPORT_ID")
	protected Long reportId;

	@Expose
	@Column(name = "CREATED_ON")
	protected LocalDateTime createdOn;

	@Expose
	@Column(name = "CREATED_BY")
	protected String createdBy;

	@Expose
	@Column(name = "UPDATED_ON")
	protected LocalDateTime updatedOn;

	@Expose
	@Column(name = "A2_INVOICE_KEY")
	protected String a2InvoiceKey;
	
	@Expose
	@Column(name = "PR_INVOICE_KEY")
	protected String prInvoiceKey;

	@Expose
	@Column(name = "ERROR_COUNT")
	protected String errCount;

	@Expose
	@Column(name = "STATUS")
	protected String status;
	
	@Expose
	@Column(name = "ERROR_DESCRIPTION")
	protected String errDesc;
	
	@Expose
	@Column(name = "FILE_ID")
	protected Long fileId;
	
	@Expose
	@Column(name = "USER_RESPONSE")
	protected String userResponse;

}
