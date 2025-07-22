package com.ey.advisory.app.data.entities.client;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;

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
 * @author Shashikant.Shukla
 *
 */

@Entity
@Table(name = "TBL_AIM_ONB_IMS_CONFIG")
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class ImsReconConfigEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "IMS_CONFIG_ID")
	protected Long batchId;

	@Expose
	@Column(name = "ENTITY_ID")
	protected Long entityId;

	@Expose
	@Column(name = "TAX_PERIOD")
	protected String taxPeriod;

	@Expose
	@Column(name = "CREATED_DATE")
	protected LocalDateTime createdDate;

	@Expose
	@Column(name = "CREATED_BY")
	protected String createdBy;

	@Expose
	@Column(name = "COMPLETED_ON")
	protected LocalDateTime completedOn;

	@Expose
	@Column(name = "STATUS")
	protected String status;

}
