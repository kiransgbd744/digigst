package com.ey.advisory.app.services.credit.reversal;

import lombok.Data;

@Data
public class ExceptionalTaggingDetailsDto {
	
private String customerGSTIN;
private String documentType;
private String documentNumber;
private String documentDate;
private String supplierGSTIN;
private String itemSerialNumber;
private String commonSupplyIndicator;
private String returnPeriod;
private String fileID;
private String fileName;
private String errorCode;
private String errorDescription;

}
