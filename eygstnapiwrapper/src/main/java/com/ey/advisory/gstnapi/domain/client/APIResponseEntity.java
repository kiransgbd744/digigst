/**
 * 
 */
package com.ey.advisory.gstnapi.domain.client;

import java.sql.Clob;
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
@Data
@Table(name = "API_RESPONSE")
public class APIResponseEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "API_RESPONSE_SEQ", allocationSize = 1)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Column(name = "INVOCATION_ID")
	private Long invocationId;

	@Column(name = "RESPONSE_JSON")
	private Clob responseJson;
	
	@Column(name = "STATUS", length = 60)
	private String status;
	
	@Column(name = "ERROR_CODE")
	private String errorCode;
	
	@Column(name = "ERROR_DESC", length = 100)
	private String errorDesc;

	@Column(name = "CREATED_BY", length = 60)
	private String createdBy;

	@Column(name = "CREATED_DATE")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_BY", length = 60)
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;
	
	

	public APIResponseEntity(Long invocationId, Clob responseJson,
			String status, String createdBy, LocalDateTime createdOn,
			String modifiedBy,
			LocalDateTime modifiedOn) {
		super();
		this.invocationId = invocationId;
		this.responseJson = responseJson;
		this.status = status;
		this.createdBy = createdBy;
		this.createdOn = createdOn;
		this.modifiedBy = modifiedBy;
		this.modifiedOn = modifiedOn;
	}



	public APIResponseEntity() {
		super();
	}



	public APIResponseEntity(String errorCode, String errorDesc,
			Long invocationId, String status) {
		this.invocationId = invocationId;
		this.errorCode = errorCode;
		this.errorDesc = errorDesc;
		this.status = status;
	}

}
