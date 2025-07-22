package com.ey.advisory.admin.data.entities.client;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "TEMPLATE_MASTER")
@Data
public class TemplateMasterEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "TEMPLATE_CODE")
	private String tempateCode;

	@Column(name = "TEMPLATE_TYPE")
	private String templateType;

	@Column(name = "IS_DELETE")
	private Boolean isDelete;

	@Column(name = "CREATED_BY")
	private String createdBy;

}
