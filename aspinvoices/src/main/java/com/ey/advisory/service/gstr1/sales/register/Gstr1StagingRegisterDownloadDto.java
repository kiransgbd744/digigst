package com.ey.advisory.service.gstr1.sales.register;

import lombok.Data;

/**
 * @author Shashikant.Shukla
 *
 */

@Data
public class Gstr1StagingRegisterDownloadDto {
	
private String returnPeriod;
private String businessPlace;
private String customerGstin;
private String documentType;
private String supplyType;
private String documentNumber;
private String documentDate;
private String itemSerialNumber;
private String hsnSac;
private String taxRate;
private String itemAssessableValue;
private String igst;
private String cgst;
private String sgst;
private String advaloremCess;
private String specificCess;
private String invoiceValue;
private String pos;
private String transactionType;
private String fileName;
private String dateTime;
private String errorDescription;

}
