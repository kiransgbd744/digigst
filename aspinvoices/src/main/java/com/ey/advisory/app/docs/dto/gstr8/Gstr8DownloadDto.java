package com.ey.advisory.app.docs.dto.gstr8;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.Data;

@Data
public class Gstr8DownloadDto {

	private BigInteger fileId;

	private String ecomGstin;

	private String returnPeriod;

	private String identifier;

	private String originalReturnPeriod;

	private BigDecimal originalNetSupplies;

	private String docType;

	private String supplyType;

	private String supplierGstinOrEnrolmentId;

	private String originalSupplierGstinOrEnrolmentId;

	private BigDecimal suppliesToRegistered;

	private BigDecimal returnsFromRegistered;

	private BigDecimal suppliesToUnregistered;

	private BigDecimal returnsFromUnregistered;

	private BigDecimal netSupplies;

	private BigDecimal integratedTaxAmount;

	private BigDecimal centralTaxAmount;

	private BigDecimal stateUTTaxAmount;

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

	private String recordType;
	
	private String pos;

	private String originalPos;


}
