package com.ey.advisory.app.data.entities.client;

import java.sql.Clob;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;

import lombok.Data;

@Entity
@Data
@Table(name = "TBL_GET_SEARCH_IRN_PAYLOADS")
public class GetIrnSearchDetailsPayloadEntity {
	
	@Expose
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Expose
	@Column(name = "IRN")
	private String irn;

	@Expose
	@Column(name = "IRN_STATUS")
	private String irnSts;
	
	@Expose
	@Column(name = "ACK_NO")
	private Long ackNo;

	@Expose
	@Column(name = "ACK_DATE")
	private String ackDate;
	
	@Expose
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;
	
	@Expose
	@Lob
	@Column(name = "PAYLOAD")
	private Clob payload;
	
	@Expose
	@Column(name = "CNL_DATE")
	private String cnlDate;
	
	@Expose
	@Column(name = "IRP_NAME")
	private String iss;
	
	@Expose
	@Column(name = "ERROR_CODE")
	private String errCode;
	
	@Expose
	@Column(name = "ERROR_MSG")
	private String errMsg;

}
