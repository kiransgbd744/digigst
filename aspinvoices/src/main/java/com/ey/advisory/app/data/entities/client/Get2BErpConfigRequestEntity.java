package com.ey.advisory.app.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Sruthi.P
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TBL_AUTO2B_ERP_CONFIG_REQUEST")
public class Get2BErpConfigRequestEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "REQUEST_CONFIG_ID")
	private Long requestId;

	@Column(name = "GSTIN_LIST")
	private String gstinList;

	@Column(name = "RETURN_PERIOD_LIST")
	private String returnPeriodList;

	@Column(name = "ITC")
	private String itc;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "INVOCATION_ID")
	private Long invocationId;
	
	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;
	
	@Column(name = "IS_ACTIVE")
	private Boolean isActive;

	@Lob
	@Column(name = "REQ_PAYLOAD")
	protected String reqPayload;
	
}
