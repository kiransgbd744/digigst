package com.ey.advisory.app.data.entities.client.asprecon;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "VENDOR_REPORT_DOWNLOAD")
@Getter
@Setter
@ToString
public class VendorReportDownloadEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Expose
	@SerializedName("requestID")
	@Column(name = "REQUEST_ID")
	private Long requestID;

	@Expose
	@SerializedName("recipientGstins")
	@Column(name = "RECIPIENT_GSTINS")
	private String recipientGstins;

	@Expose
	@SerializedName("vendorGstins")
	@Column(name = "VENDOR_GSTINS")
	private String vendorGstins;

	@Expose
	@SerializedName("reportType")
	@Column(name = "REPORT_TYPE")
	private String reportType;

	@Expose
	@SerializedName("reportStatus")
	@Column(name = "REPORT_STATUS")
	private String reportStatus;

	@Expose
	@SerializedName("fromTaxPeriod")
	@Column(name = "FROM_TAX_PERIOD")
	private LocalDateTime fromTaxPeriod;

	@Expose
	@SerializedName("toTaxPeriod")
	@Column(name = "TO_TAX_PERIOD")
	private LocalDateTime toTaxPeriod;


	@Expose
	@SerializedName("email")
	@Column(name = "EMAIL")
	private String email;

	@Expose
	@SerializedName("emailStatus")
	@Column(name = "EMAIL_STATUS")
	private String emailStatus;

	@Expose
	@SerializedName("filePath")
	@Column(name = "FILEPATH")
	private String filePath;

	@Expose
	@SerializedName("errorDesc")
	@Column(name = "ERROR_DESC")
	private String errorDesc;

	@Expose
	@SerializedName("createdOn")
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Expose
	@SerializedName("createdBy")
	@Column(name = "CREATED_BY")
	private String createdBy;

}
