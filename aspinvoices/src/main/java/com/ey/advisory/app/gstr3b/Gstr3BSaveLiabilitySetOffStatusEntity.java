package com.ey.advisory.app.gstr3b;

import java.sql.Clob;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
/**
 * 
 * @author vishal.verma
 *
 */

@Setter
@Getter
@Entity
@Table(name = "GSTR3B_SAVE_LIABILITYSETOFF_STATUS")
public class Gstr3BSaveLiabilitySetOffStatusEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR3B_SAVE_LIABILITYSETOFF_STATUS_SEQ", allocationSize = 1)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;
	
	@Expose
	@SerializedName("Gstin")
	@Column(name = "GSTIN")
	protected String gstin;

	@Expose
	@SerializedName("taxPeriod")
	@Column(name = "TAXPERIOD")
	protected String taxPeriod;
	
	@Expose
	@SerializedName("status")
	@Column(name = "STATUS")
	protected String status;
		
	@Expose
	@SerializedName("refid")
	@Column(name = "REF_ID")
	protected String refId;
	
	@Expose
	@SerializedName("errorCount")
	@Column(name = "ERROR_COUNT")
	protected Integer errorCount;
	
	@Expose
	@SerializedName("saveRequestPayload")
	@Column(name = "SAVE_REQUEST_PAYLOAD")
	protected Clob saveRequestPayload;
	
	@Expose
	@SerializedName("saveResponsePayload")
	@Column(name = "SAVE_RESPONSE_PAYLOAD")
	protected String saveResponsePayload;
	
	@Expose
	@SerializedName("pollingResponsePayload")
	@Column(name = "POLLING_RESPONSE_PAYOAD")
	protected Clob pollingResponsePayload;
	
	@Expose
	@SerializedName("filePath")
	@Column(name = "FILE_PATH")
	protected String filePath;
	
	@Expose
	@SerializedName("createdOn")
	@Column(name = "CREATED_ON")
	protected LocalDateTime createdOn;
	
	@Expose
	@SerializedName("updatedOn")
	@Column(name = "UPDATED_ON")
	protected LocalDateTime updatedOn;
	

}