/**
 * 
 */
package com.ey.advisory.gstnapi.domain.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @author Khalid1.Khan
 *
 */
@Entity
@Table(name = "API_INVOCATION_REQ")
@Data
public class APIInvocationReqEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "API_INVOCATION_REQ_SEQ", allocationSize = 1)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Column(name = "INVOCATION_ID")
	private Long refReqId;

	@Column(name = "GSTIN", length = 15)
	private String gstin;

	@Column(name = "API_IDENTIFIER", length = 100)
	private String apiIdentifier;

	@Column(name = "API_PARAMS", length = 500)
	private String apiParams;

	@Column(name = "API_PARAMS_HASH_VALUE", length = 64)
	private String apiParamsHashValue;

	@Column(name = "STATUS", length = 64)
	private String status;

	@Column(name = "SUCCESS_HANDLER", length = 256)
	private String successHandler;

	@Column(name = "FAILURE_HANDLER", length = 256)
	private String failureHandler;

	@Column(name = "CREATED_BY", length = 60)
	private String createdBy;

	@Column(name = "CREATED_DATE")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_BY", length = 60)
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;
	
	public APIInvocationReqEntity(String gstin, String apiIdentifier,
			String apiParams, String apiParamsHashValue, String status,
			String successHandler, String failureHandler, String createdBy,
			LocalDateTime createdOn, String modifiedBy, 
			LocalDateTime modifiedOn) {
		super();
		this.gstin = gstin;
		this.apiIdentifier = apiIdentifier;
		this.apiParams = apiParams;
		this.apiParamsHashValue = apiParamsHashValue;
		this.status = status;
		this.successHandler = successHandler;
		this.failureHandler = failureHandler;
		this.createdBy = createdBy;
		this.createdOn = createdOn;
		this.modifiedBy = modifiedBy;
		this.modifiedOn = modifiedOn;
	}
	
	public APIInvocationReqEntity(){
		
	}
	

}
