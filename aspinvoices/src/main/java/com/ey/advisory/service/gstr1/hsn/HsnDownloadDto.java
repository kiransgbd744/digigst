package com.ey.advisory.service.gstr1.hsn;

import lombok.Data;

/**
 * @author Shashikant.Shukla
 *
 */

@Data
public class HsnDownloadDto {

	private String serialNo;
	private String supplierGstin;
	private String returnPeriod;
	private String hsnSac;
	private String productDescription;
	private String unitOfMeasurement;
	private String quantity;
	private String rate;
	private String taxableValue;
	private String integratedTaxAmount;
	private String centralTaxAmount;
	private String stateUTTaxAmount;
	private String cessAmount;
	private String totalValue;
	private String fileName;
	private String uploadedBy;
	private String uploadedDateAndTime;
	private String errorCode;
	private String errorDescription;
	private String recordType;

}
