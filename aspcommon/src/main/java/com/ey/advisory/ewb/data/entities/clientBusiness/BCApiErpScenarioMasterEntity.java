package com.ey.advisory.ewb.data.entities.clientBusiness;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Hemasundar.J
 *
 */
@Entity
@Table(name = "SCENARIO_MASTER")
@Setter
@Getter
public class BCApiErpScenarioMasterEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "SCENARIO_NAME")
	private String scenarioName;

	@Column(name = "SCENARIO_DESCR")
	private String scenarioDesc;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "JOB_TYPE")
	private String jobType;

	@Column(name = "DATA_TYPE")
	private String dataType;

}

