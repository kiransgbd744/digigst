package com.ey.advisory.app.services.reports.gstr7trans;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Gstr7TransDto {
	
	private String sourceIdentifier;
	private String sourcFilename;
	private String glAccountCode;//
	private String division;
	private String subDivision;
	private String profitCentre1;
	private String profitCentre2;
	private String plantCode;
	private String returnPeriod;
	private String docType;
	private String supplyType;
	private String deductorGstin;//
	private String docNum;//
	private String docDate;//
	private String originalDocNum;//
	private String originalDocDate;//
	private String deducteeGstin; //
	private String originalDeducteeGstin;
	private String originalReturnPeriod;
	private String originalTaxableValue;
	private String OriginalInvoiceValue;// new
	private String lineItemNumber;  // check leter
	private String taxableValue;
	private String igstAmt;
	private String cgstAmt;
	private String sgstAmt;
	private String invoiceValue;
	private String contractNumber;
	private String contractDate;
	private String contractValue;
	private String paymentAdviceNumber;
	private String paymentAdviceDate;
	
	private String userdefinedField1;
	private String userdefinedField2;
	private String userdefinedField3;
	private String userdefinedField4;
	private String userdefinedField5;
	private String userdefinedField6;
	private String userdefinedField7;
	private String userdefinedField8;
	private String userdefinedField9;
	private String userdefinedField10;
	private String userdefinedField11;
	private String userdefinedField12;
	private String userdefinedField13;
	private String userdefinedField14;
	private String userdefinedField15;
	
	//Error Records
	private String errorCode;
	private String errorDescription;
	
	//Processed Records
	private String fileid;
	private String section;
	
	//Conolidated GSTN error Report
	private String gstnStatus;
	private String gstnRefid;
	private String gstnRefIdDateTime;
	private String gstnErrorCode;
	private String gstnErrorDescription;
	private String tableNumber;
	
	//Conolidated DigiGST error Report
	private String aspError;
	
	//Conolidated GSTN & DigiGST error Report
	private String source;
	private String userId;
	private String fileID;
	private String fileName;
	private String uploadDataDateTime;
	
	// API extra parameter
	private String fiYear;
	private String dataOriginTypeCode;

}