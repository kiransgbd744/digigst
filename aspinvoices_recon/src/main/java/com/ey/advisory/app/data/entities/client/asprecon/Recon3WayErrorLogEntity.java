package com.ey.advisory.app.data.entities.client.asprecon;

import java.sql.Clob;
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

/**
 * @author Sakshi.jain
 *
 */
@Entity
@Table(name = "TBL_3WAY_ERROR_LOG")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Recon3WayErrorLogEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "T_ID")
	private Long toleranceId;

	@Column(name = "RECON_CONFIG_ID")
	private Long configId;

	@Column(name = "GSTIN")
	private String gstin;
	
	@Column(name = "PROCEDURE_ID")
	private Integer procId;
	
	@Column(name = "PROCEDURE_NAME")
	private String procName;
	
	@Column(name = "ERROR_CODE")
	private String errCode;
	
	@Column(name = "ERROR_MESSAGE")
	private Clob errMessage;

	@Column(name = "CREATED_DATE")
	private LocalDateTime createdDate;

}
