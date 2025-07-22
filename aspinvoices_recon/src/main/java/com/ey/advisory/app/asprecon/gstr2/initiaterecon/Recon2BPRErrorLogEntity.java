package com.ey.advisory.app.asprecon.gstr2.initiaterecon;

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
 * @author Vishal.verma
 *
 */
@Entity
@Table(name = "TBL_2BPR_ERROR_LOG")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Recon2BPRErrorLogEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ERR_ID")
	private Long errorId;

	@Column(name = "RECON_REPORT_CONFIG_ID")
	private Long configId;
	
	@Column(name = "PROCEDURE_ID")
	private Integer procId;
	
	@Column(name = "PROCEDURE_NAME")
	private String procName;
	
	@Column(name = "ERR_CODE")
	private String errCode;
	
	@Column(name = "ERR_DESC")
	private Clob errMessage;

	@Column(name = "CREATE_ON")
	private LocalDateTime createdDate;

}
