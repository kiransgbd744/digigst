package com.ey.advisory.app.data.entities.client.asprecon;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Sakshi.jain
 *
 */

@Entity
@Table(name = "TBL_RECON_2BPR_RECON_RESULT_UI")
@Setter
@Getter
@ToString

public class ReconResultsUIGstr2BprEntity {

	//private static final long serialVersionUID = 1L;

	@Expose
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "RECON_RESULT_2BPR_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;
		
	@Expose
	@Column(name = "BATCHID")
	private Long batchId;
		
	@Expose
	@Column(name = "RECON_LINK_ID")
	private Long reconLinkId;
	
	@Expose
	@Column(name = "USER_RESPONSE")
	private String userResp;
	
	@Expose
	@Column(name = "RESPONSE_REMARKS")
	private String respRemarks;
	
	@Expose
	@Column(name = "FMRESPONSE")
	private String fmResponse;
	
	@Expose
	@Column(name = "RSPTAXPERIOD3B")
	private String rspTaxPeriod3B;
	
	@Expose
	@Column(name = "AVAILABLE_IGST")
	private BigDecimal avaiIgst;
	
	@Expose
	@Column(name = "AVAILABLE_CGST")
	private BigDecimal avaiCgst;
	
	@Expose
	@Column(name = "AVAILABLE_SGST")
	private BigDecimal avaiSgst;
	
	@Expose
	@Column(name = "AVAILABLE_CESS")
	private BigDecimal avaiCess;
	
	@Expose
	@Column(name = "IS_DELETE")
	private boolean isDelete;
	
	@Expose
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;
	
	@Expose
	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;
	
	@Expose
	@Column(name = "FILE_ID")
	private Long fileId;
	
	@Expose
	@Column(name = "SRC_TYP")
	private String source;
	
	@Expose
	@Column(name = "ITC_REVERSAL_IDENTIFIER")
	private String itcReversal;
	
	@Expose
	@Column(name = "LOCK_TYPE")
	private String identifier;
	
	@Expose
	@Column(name = "IMS_USER_RESPONSE")
	private String imsUserResponse;
	
	@Expose
	@Column(name = "IMS_RESPONSE_REMARKS")
	private String imsResponseRemarks;
	
	@Expose
	@Column(name = "IMS_UNIQUE_ID")
	private String imsUniqueId;
	
}
