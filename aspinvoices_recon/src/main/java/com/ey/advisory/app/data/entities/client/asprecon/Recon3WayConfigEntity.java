package com.ey.advisory.app.data.entities.client.asprecon;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Sakshi.jain
 *
 */
@Entity
@Table(name = "TBL_3WAY_RECON_CONFIG")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Recon3WayConfigEntity {

	@Id
	@Column(name = "RECON_CONFIG_ID")
	private Long configId;

	@Column(name = "RECON_TYPE")
	private String reconType;

	@Column(name = "REQUEST_TYPE")
	private String requestType;

	@Column(name = "ENTITY_ID")
	private Long entityId;

	@Column(name = "IS_GSTR1")
	private Boolean isGSTR1;

	@Column(name = "IS_EINV")
	private Boolean isEINV;

	@Column(name = "IS_EWB")
	private Boolean isEWB;

	@Column(name = "GSTR1_TYPE")
	private String gstr1Type;

	@Column(name = "EINV_TYPE")
	private String einvType;

	@Column(name = "EWB_TYPE")
	private String ewbType;

	@Column(name = "FROM_DOC_DATE")
	private LocalDate fromDocDate;

	@Column(name = "TO_DOC_DATE")
	private LocalDate toDocDate;

	@Column(name = "FROM_RET_PERIOD")
	private Integer fromRetPeriod;

	@Column(name = "TO_RET_PERIOD")
	private Integer toRetPeriod;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "CREATED_DATE")
	private LocalDateTime createdDate;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "COMPLETED_ON")
	private LocalDateTime completedOn;

}
