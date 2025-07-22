/**
 * 
 */
package com.ey.advisory.app.service.upload.way3recon;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;

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
@Table(name = "GETEWB_CNTRL_TBL")
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GetEwbCntrlEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Expose
	@Column(name = "GSTIN")
	private String gstin;

	@Expose
	@Column(name = "GET_STATUS")
	private String getStatus;

	@Expose
	@Column(name = "GET_CALL_DATE")
	private LocalDate getCallDate;

	@Expose
	@Column(name = "ERR_MSG")
	private String errMsg;

	@Expose
	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;

	@Expose
	@Column(name = "UPDATED_BY")
	private String updatedBy;

	@Expose
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Expose
	@Column(name = "CREATED_BY")
	protected String createdBy;

}
