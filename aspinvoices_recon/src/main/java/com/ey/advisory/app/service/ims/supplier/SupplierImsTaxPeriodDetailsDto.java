package com.ey.advisory.app.service.ims.supplier;

import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SupplierImsTaxPeriodDetailsDto {

	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;

	@Expose
	@SerializedName("status")
	private String apiStatus;

	@Expose
	@SerializedName("failedSections")
	private String failedSections;

	@Expose
	@SerializedName("successSections")
	private String successSections;

	@Expose
	@SerializedName("inProgressSections")
	private String inProgressSections;
	
	@Expose
	@SerializedName("successWithNoDataSections")
	private String successWithNoDataSections;

	@Expose
	@SerializedName("initiatedOn")
	private LocalDateTime initiatedOn;

	@Expose
	@SerializedName("reportStatus")
	private String reportStatus;
	

	public SupplierImsTaxPeriodDetailsDto(String taxPeriod, LocalDateTime initiatedOn,
			String successSections, String failedSections,
			String inprogressSection, String successWithNoDataSections) {

		this.taxPeriod = taxPeriod;
		this.initiatedOn = initiatedOn;
		this.successSections = successSections;
		this.failedSections = failedSections;
		this.inProgressSections = inprogressSection;
		this.successWithNoDataSections = successWithNoDataSections;
	}
	
	public SupplierImsTaxPeriodDetailsDto(String taxPeriod, String apiStatus) {

		this.taxPeriod = taxPeriod;
		this.apiStatus = apiStatus;
	}

}