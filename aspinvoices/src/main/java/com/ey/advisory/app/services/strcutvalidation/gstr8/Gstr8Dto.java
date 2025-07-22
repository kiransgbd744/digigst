package com.ey.advisory.app.services.strcutvalidation.gstr8;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * @author Shashikant.Shukla
 *
 */

@Data
public class Gstr8Dto {

	private String ecomGstin;
	private String returnPeriod;
	private String identifier;
	private String originalReturnPeriod;
	private String originalNetSupplies;
	private String documentType;
	private String supplyType;
	private String supplierGSTINorEnrolmentID;
	private String originalSupplierGSTINorEnrolmentID;
	private String suppliesToRegistered;
	private String returnsFromRegistered;
	private String suppliesToUnRegistered;
	private String returnsFromUnRegistered;
	private String netSupplies;
	private String integratedTaxAmount;
	private String centralTaxAmount;
	private String stateUTTaxAmount;
	private String userDefinedField1;
	private String userDefinedField2;
	private String userDefinedField3;
	private String userDefinedField4;
	private String userDefinedField5;
	private String userDefinedField6;
	private String userDefinedField7;
	private String userDefinedField8;
	private String userDefinedField9;
	private String userDefinedField10;
	private String userDefinedField11;
	private String userDefinedField12;
	private String userDefinedField13;
	private String userDefinedField14;
	private String userDefinedField15;
	private String pos;
	private String originalPos;
	private String docKey;
	private Long fileId;
	private String fileName;
	private boolean isPsd;
	private String errorCode;
	private String errorDesc;
	private boolean isActive;
	private LocalDateTime createdDate;

}
