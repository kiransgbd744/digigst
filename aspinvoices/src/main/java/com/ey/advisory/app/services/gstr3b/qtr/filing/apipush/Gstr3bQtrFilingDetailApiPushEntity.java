package com.ey.advisory.app.services.gstr3b.qtr.filing.apipush;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TBL_GET_GSTR3B_QTR_FILING_API_PUSH")
public class Gstr3bQtrFilingDetailApiPushEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "META_ID")
	private Long metaId;
	
	@Column(name = "PAYLOAD_ID")
	private String payloadId;
	
	@Column(name = "GSTIN")
	private String gstin;
	
	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;
	
	@Column(name = "Quarter")
	private String quarter;
	
	@Column(name = "IS_FILED")
	private String isFiled;
	
	@Column(name = "ERROR_CODE")
	private String errorCode;	
	
	@Column(name = "ERROR_DESCRIPTION")
	private String errorDiscription;
	
	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;	
	
}