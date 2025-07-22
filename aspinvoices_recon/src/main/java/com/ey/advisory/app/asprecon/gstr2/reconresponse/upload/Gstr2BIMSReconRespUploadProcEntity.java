package com.ey.advisory.app.asprecon.gstr2.reconresponse.upload;

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
@Table(name = "TBL_2BPR_RECON_RESP_UPLOAD_IMS")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Gstr2BIMSReconRespUploadProcEntity {

	@Id
	@Column(name = "ID")
	private Integer id;
	
	@Column(name = "PROCEDURE_NAME")
	private String procName;
	
	@Column(name = "SEQUENCE_OF_EXECUTION")
	private Integer seqId;
	
	@Column(name = "IS_ACTIVE")
	private Boolean isActive;
	
}
