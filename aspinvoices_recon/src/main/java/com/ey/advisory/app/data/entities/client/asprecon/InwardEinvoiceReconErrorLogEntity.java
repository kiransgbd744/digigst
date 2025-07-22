package com.ey.advisory.app.data.entities.client.asprecon;

import lombok.*;

import jakarta.persistence.*;
import java.sql.Clob;
import java.time.LocalDateTime;

/**
 * @author Vishal.verma
 *
 */
@Entity
@Table(name = "TBL_EINVPR_ERROR_LOG")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InwardEinvoiceReconErrorLogEntity {

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
