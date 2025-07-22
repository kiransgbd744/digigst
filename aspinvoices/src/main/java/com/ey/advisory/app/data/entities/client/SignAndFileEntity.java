/**
 * 
 */
package com.ey.advisory.app.data.entities.client;

import java.sql.Clob;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @author Hemasundar.J
 *
 */
@Entity
@Table(name = "SIGN_FILE")
@Data
public class SignAndFileEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "TAX_PERIOD")
	private String taxPeriod;

	@Column(name = "RETURN_TYPE")
	private String returnType;

	@Column(name = "SESSION_KEY")
	private String sessionKey;

	@Column(name = "ACK_NUM")
	private String ackNum;

	@Column(name = "SUMMARY_PAYLOAD")
	private Clob payload;

	@Column(name = "HASH_PAYLOAD")
	private String hashPayload;

	@Column(name = "SIGNED_PAYLOAD")
	private Clob signedData;

	@Column(name = "ENCRYPTED_PAYLOAD")
	private Clob base64Signed;

	@Column(name = "USER_NAME")
	private String gstinUserName;

	@Column(name = "PAN")
	private String pan;

	@Column(name = "PROCESSING_KEY")
	private String processingKey;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;	

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "ERROR_MSG")
	private String errorMsg;

	@Column(name = "FILING_TYPE")
	private String filingType;
	
	@Column(name = "FILING_MODE")
	private String filingMode;
	
	@Column(name = "OTP_STATUS")
	private String otpStatus;

}
