/**
 * 
 */
package com.ey.advisory.app.gstr1.einv;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */
@Data
@Entity
@Table(name = "GSTR1_GSTN_SAVE_RESPONSE_DATA")
public class Gstr1VsEInvExcSaveEntity {
	
	@Expose
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR1_GSTN_SAVE_RESPONSE_DATA_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "FILE_ID")
	private Integer fileId;

	@Column(name = "SUPPLIER_GSTIN")
	private String sGstin;

	@Column(name = "RETURN_PERIOD")
	private String taxPeriod;

	@Column(name = "DOCUMENT_TYPE")
	private String docType;

	@Column(name = "DOCUMENT_NUMBER")
	private String docNum;
	
	@Column(name = "DOCUMENT_DATE")
	private LocalDate docDate;
	
	@Column(name = "RESPONSE")
	private String resp;
	
	@Column(name = "ERROR_CODE")
	private String errorCode;
	
	@Column(name = "ERROR_DESC")
	private String errorDesc;
	
	@Column(name = "IS_DELETE")
	private boolean isDelete;
	
	@Column(name = "IS_PROCESSED")
	private boolean isPsd;
	
	@Column(name = "DOC_KEY")
	private String docKey;
	
	@Column(name = "FY")
	private Integer fy;
	
	@Column(name = "CREATED_USER")
	private String createdBy;

	@Column(name = "CREATED_DATE")
	private LocalDateTime createDate;

}

