package com.ey.advisory.app.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TBL_3B_RECON_COMPUTE_PROCEDURE")
public class Gstr3bProcedureEntity {

	// need variables and columns from PROCEDURE_ID	PROCEDURE_NAME	SEQ_SUP_A	SEQ_SUP_B	CREATED_ON	CREATED_BY	IS_ACTIVE	MODIFIED_DATE	MODIFIED_BY
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PROCEDURE_ID")
	private Long procedureId;

	@Column(name = "PROCEDURE_NAME")
	private String procedureName;

	@Column(name = "SEQ_SUP_A")
	private Integer seqSupA;

	@Column(name = "SEQ_SUP_B")
	private Integer seqSupB;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "IS_ACTIVE")
	private Boolean isActive;

	@Column(name = "MODIFIED_DATE")
	private LocalDateTime modifiedDate;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;
}

