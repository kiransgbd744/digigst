package com.ey.advisory.service.gstr1.sales.register;

import lombok.Data;

/**
 * @author Shashikant.Shukla
 *
 */

@Data
public class StagingSalesRegisterUploadDto {
	
private Long fileId;
private String returnPeriod;
private String businessPlace;
private String custGstin;
private String docType;
private String supplyType;
private String docNum;
private String docDate;
private String itemSerialNumber;
private String hsnSac;
private String taxRate;
private String itemAssessableValue;
private String igst;
private String cgst;
private String sgst;
private String advaloremAmountCess;
private String specificCess;
private String invoiceValue;
private String pos;
private String transactionType;
private String fileName;
private String dateTime;
private Boolean isError;
private String errorDescription;

}
