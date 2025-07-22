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
@Table(name = "MAKER_CHECKER_WORK_FLOW")
@Data
public class MakerCheckerWorkFlowEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "ENTITY_ID")
	private Long entityId;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "MKR_CHR_ENT_MAP_ID")
	private Long mkrChrEntMapId;

	@Column(name = "RETURN_TYPE")
	private String returnType;

	@Column(name = "WORK_FLOW_TYPE")
	private String workFlowType;

	@Column(name = "MAKER_CHECKER_TYPE")
	private String makerCheckerType;

	@Column(name = "CHECKER_NOTIFICATTION_TO")
	private String checkerNotificationTo;

	@Column(name = "MAKER_CHECKER_ID")
	private String makerCheckerId;

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
