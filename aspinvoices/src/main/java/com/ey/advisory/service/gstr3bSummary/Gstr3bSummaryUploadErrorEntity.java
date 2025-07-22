package com.ey.advisory.service.gstr3bSummary;

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
 * @author Shashikant.Shukla 
 *
 */

@Data
@Entity
@Table(name = "TBL_GSTR3BSUMMARYUPLOAD_ERROR")
public class Gstr3bSummaryUploadErrorEntity {

	@Expose
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "TBL_GSTR3BSUMMARYUPLOAD_ERROR_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Column(name = "FILE_ID")
	private Long fileId;

	@Column(name = "RETURN_PERIOD")
	private String ReturnPeriod;

	@Column(name = "GSTIN")
	private String gstin;
	
	@Column(name = "TABLE_NUMBER")
	private String tableNumber;

	@Column(name = "TOTAL_TAXABLE_VALUE")
	private String totalTaxablevalue;

	@Column(name = "IGST_AMOUNT")
	private String igstAmount;

	@Column(name = "CGST_AMOUNT")
	private String cgstAmount;

	@Column(name = "SGST_AMOUNT")
	private String sgstAmount;

	@Column(name = "CESS_AMOUNT")
	private String cessAmount;

	@Column(name = "POS")
	private String pos;
	
	@Column(name = "IS_ACTIVE")
	private Boolean isActive;

	@Column(name = "ERROR_CODE")
	private String errorCode;
	
	@Column(name = "ERROR_MESSAGE")
	private String errorMessage;
	
	@Column(name = "CREATE_BY")
	private String createBy;
	
	@Column(name = "CREATE_DATE")
	private LocalDateTime createDate;
	
	@Column(name = "UPDATE_BY")
	private String updatedBy;
	
	@Column(name = "UPDATE_DATE")
	private LocalDateTime updateDate;
	
	@Column(name = "Doc_Key")
	private String docKey;
}
