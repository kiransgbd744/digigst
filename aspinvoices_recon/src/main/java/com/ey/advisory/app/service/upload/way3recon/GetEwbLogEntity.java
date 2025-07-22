/**
 * 
 */
package com.ey.advisory.app.service.upload.way3recon;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Siva.Reddy
 *
 */

@Entity
@Table(name = "GETEWB_LOG_TBL")
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GetEwbLogEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Expose
	@SerializedName("id")
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GETEWB_LOG_TBL_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;

	@Expose
	@Column(name = "SUPP_GSTIN")
	private String suppGstin;

	@Expose
	@Column(name = "EWB_NO")
	private String ewbNo;

	@Expose
	@Column(name = "EWB_RESPONSE")
	@Lob
	private String ewbResp;
	
	@Expose
	@Column(name = "ERR_MSG")
	private String errMsg;

	@Expose
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

}
