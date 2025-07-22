package com.ey.advisory.app.recipientmasterupload;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.ey.advisory.app.itcmatching.vendorupload.ErrorTypeDescription;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Hema G M
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TBL_RECIPIENT_MASTER_ERROR")
public class RecipientMasterErrorReportEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "RECIPIENT_ERROR_ID")
	private Long id;

	@Column(name = "FILE_ID")
	private Long fileId;

	@Column(name = "ERROR_DESC")
	private String error;

	@Column(name = "INFORMATION_DESC")
	private String information;

	@Column(name = "ERROR_TYPE_DESC")
	private ErrorTypeDescription errorTypeDeascription;

	@Column(name = "INVOICE_KEY")
	private String invoiceKey;

	@Column(name = "RECIPIENT_PAN")
	private String recipientPAN;

	@Column(name = "RECIPIENT_GSTIN")
	private String recipientGstin;

	@Column(name = "RECIPIENT_PRIMARY_EMAIL_ID")
	private String recipientPrimEmailId;

	@Column(name = "RECIPIENT_EMAIL_ID_2")
	private String recipientEmailId2;

	@Column(name = "RECIPIENT_EMAIL_ID_3")
	private String recipientEmailId3;

	@Column(name = "RECIPIENT_EMAIL_ID_4")
	private String recipientEmailId4;

	@Column(name = "RECIPIENT_EMAIL_ID_5")
	private String recipientEmailId5;

	@Column(name = "CCE_EMAIL_ID_1")
	private String cceEmailId1;
	
	@Column(name = "CCE_EMAIL_ID_2")
	private String cceEmailId2;
	
	@Column(name = "CCE_EMAIL_ID_3")
	private String cceEmailId3;
	
	@Column(name = "CCE_EMAIL_ID_4")
	private String cceEmailId4;
	
	@Column(name = "CCE_EMAIL_ID_5")
	private String cceEmailId5;
	
	@Column(name = "IS_GET_GSTR2A_EMAIL")
	private String isGetGstr2AEmail;

	@Column(name = "IS_GET_GSTR2B_EMAIL")
	private String isGetGstr2BEmail;

	@Column(name = "IS_DRC01B_EMAIL")
	private String isDRC01BEmail;
	
	@Column(name = "IS_DRC01C_EMAIL")
	private String isDRC01CEmail;
	
	@Column(name = "ACTION")
	private String isDelete;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "RETURN_COMPLIANCE_STATUS_EMAIL")
	private String isRetCompStatusEmail;

}
