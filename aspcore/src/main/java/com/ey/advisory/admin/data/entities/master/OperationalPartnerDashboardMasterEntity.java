package com.ey.advisory.admin.data.entities.master;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "OPS_DASHBOARD")
@Data
public class OperationalPartnerDashboardMasterEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Column(name = "GROUP_NAME")
	private String groupName;

	@Column(name = "CREATED_DTTM")
	private LocalDateTime digigstCreatedOn;

	@Column(name = "GROUP_CODE")
	private String groupCode;

	@Column(name = "APP")
	private String app;

	@Column(name = "ENTITY_COUNT")
	private Long entityCount;

	@Column(name = "GSTIN_COUNT")
	private Long gstinCount;

	@Column(name = "EINV_DTTM")
	private LocalDateTime einvDttm;

	@Column(name = "EWB_DTTM")
	private LocalDateTime ewbDttm;

	@Column(name = "RET_DTTM")
	private LocalDateTime retDttm;

	@Column(name = "AIM_DTTM")
	private LocalDateTime aimDttm;

	@Column(name = "DMS_DTTM")
	private LocalDateTime dmsDttm;

	@Column(name = "MORPHES_DTTM")
	private LocalDateTime morphesDttm;

	@Column(name = "IRP_DTTM")
	private LocalDateTime irpDttm;

	@Column(name = "INWARD_JSN_DTTM")
	private LocalDateTime inwardJsnDttm;

	@Column(name = "USERS")
	private Long users;

	@Column(name = "IMS_DTTM")
	private LocalDateTime imsDttm;
	
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "IS_DELETE")
	private boolean isDelete;
	
	@Column(name = "CREATED_BY")
	private String createdBy;
}