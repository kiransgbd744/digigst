package com.ey.advisory.app.data.entities.client.asprecon;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ravindra
 *
 */
@Entity
@Table(name = "VENDOR_GSTIN_DETAILS")
@Data
@NoArgsConstructor
public class VendorGstinDetailsEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "GSTIN")
	private String gstin;
	
	@Column(name = "LEGAL_NAME_BS")
	private String legalNameBusiness;
	
	@Column(name = "TAXPAYER_TYPE")
	private String TaxpayerType;
	
	@Column(name = "GSTIN_STATUS")
	private String GstinStatus;

	@Column(name = "DATE_OF_CANCELLATION")
	private LocalDate cancelDate;
	
	@Column(name = "LAST_UPDATED_DATE")
	private LocalDateTime UpdatedDate;
	
	@Column(name = "TRADE_NAME")
	private String TradeName;
	
	@Column(name = "ERROR_CODE")
	private String errorCode;

	@Column(name = "ERROR_DESCRIPTION")
	private String errorDescription;
	 
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;

	public VendorGstinDetailsEntity(String gstin, String legalNameBusiness,
			String taxpayerType, String gstinStatus,
			LocalDate cancelDate, String tradeName) {
		this.gstin = gstin;
		this.legalNameBusiness = legalNameBusiness;
		TaxpayerType = taxpayerType;
		GstinStatus = gstinStatus;
		this.cancelDate = cancelDate;
		TradeName = tradeName;
	}
	
	

}
