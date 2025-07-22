package com.ey.advisory.admin.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "LOGO_CONFIG")
@Data
public class LogoConfigEntity {

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

	
	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "LOGO_FILE",nullable=false)
	private  byte[] logofile;

	@Column(name = "LOGO_TYPE")
	private String logoType;

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
