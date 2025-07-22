package com.ey.advisory.admin.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "TBL_GSTR3B_42_RATIO_USER")
public class Gstr3bRatioUserInputEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "INPUT_ID")
	private Long inputId;

	@Column(name = "ENTITY_ID")
	private Long entityId;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "TAX_PERIOD")
	private String taxPeriod;

	@Column(name = "RATIO1")
	private BigDecimal userInputRatio1 = BigDecimal.ZERO;

	@Column(name = "RATIO2")
	private BigDecimal userInputRatio2 = BigDecimal.ZERO;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "IS_DELETE")
	private Boolean isDeleted;

}
