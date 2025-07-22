package com.ey.advisory.admin.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "TEMPLATE_MAPPING")
@Data
public class TemplateMappingEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "GROUP_ID")
	private Long groupId;

	@Column(name = "ENTITY_ID")
	private Long entityId;

	@Column(name = "GSTIN_ID")
	private Long gstinId;

	@Column(name = "TEMPLATE_ID")
	private Long templateId;

	@Column(name = "TEMPLATE_CODE")
	private String templateCode;

	@Column(name = "TEMPLATE_TYPE")
	private String templateType;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;
}
