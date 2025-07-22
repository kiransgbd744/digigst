package com.ey.advisory.app.data.entities.gstr9;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Shashikant Shukla
 *
 */

@Data
@Entity
@Setter
@Getter
@Table(name = "TBL_GSTR9_DIGI_PROCEDURE")
public class Gstr9ComputeDigiProcedureEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	protected Long id;

	@Expose
	@Column(name = "SECTION")
	protected String section;

	@Expose
	@Column(name = "PROCEDURE_NAME")
	protected String procName;

	@Expose
	@Column(name = "SEQUENCE_OF_EXECUTION")
	protected Integer seqId;

	@Expose
	@Column(name = "CREATED_ON")
	protected LocalDateTime createdOn;

	@Expose
	@Column(name = "IS_DELETE")
	protected boolean isDelete;

}
