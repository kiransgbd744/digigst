package com.ey.advisory.app.data.entities.client.asprecon;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Siva.Reddy
 *
 */

@Entity
@Table(name = "TBL_AUTO_2APR_ERP_ADD_PRM")
@Getter
@Setter
@ToString
public class AutoRecon2APROnBrdAdParEntity {

	@Id
	@Column(name = "AP_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long apId;
	
	@Column(name = "ID")
	private Integer id;
	
	@Column(name = "ENTITY_ID")
	private String entityId;

	@Column(name = "PARTICULARS")
	private String particulars;

	@Column(name = "ENTITY_NAME")
	private String entityName;

	@Column(name = "AUTO_LOCK")
	private String autoLock;

	@Column(name = "REVERSE_FEED")
	private String reverseFeed;

	@Column(name = "ERP_REPORT_TYPE")
	private String erpReportType;

	@Column(name = "APPROVAL_STATUS")
	private String approvalStatus;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_DATE")
	private LocalDateTime createdDate;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_DATE")
	private LocalDateTime modifiedDate;

	@Column(name = "IS_ACTIVE")
	private boolean isActive;

	@Column(name = "CATEG_ORDER")
	private int categOrder;
	
	@Column(name = "IMS_ACTION_ALLOWED")
	private String imsActionAllowed;

	@Column(name = "IMS_ACTION_BLOCKED")
	private String imsActionBlocked;

}
