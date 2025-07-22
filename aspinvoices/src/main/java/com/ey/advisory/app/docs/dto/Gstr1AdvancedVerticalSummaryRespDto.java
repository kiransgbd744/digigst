package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import com.ey.advisory.app.services.validation.b2cs.ErrorDescriptionDto;

import lombok.Data;
/**
 * 
 * @author Sri BHavya
 *
 */
@Data
public class Gstr1AdvancedVerticalSummaryRespDto {
	
	private Long sNo;
	private BigInteger id;
	private String section;
	private String sgstn;
	private String taxPeriod; 
	private String transType;
	private String month;
    private String orgPos;
	private String orgHsnOrSac;
	private String orgUom;
	private BigDecimal orgQunty;
	private BigDecimal orgRate;
	private BigDecimal orgTaxableValue;
	private String orgEcomGstin;
	private BigDecimal orgEcomSupplValue;
	private String newPos;
	private String newHsnOrSac;
	private String newUom;
	private BigDecimal newQunty;
	private BigDecimal newRate;
	private BigDecimal newTaxableValue;
	private String newEcomGstin;
	private BigDecimal newEcomSupplValue;
	private BigDecimal igst;
	private BigDecimal cgst;
	private BigDecimal sgst;
	private BigDecimal cess;
	private BigDecimal totalValue;
	private String profitCntr;
	private String plant;
	private String division;
	private String location;
	private String salesOrg;
	private String distrChannel;
	private String usrAccess1;
	private String usrAccess2;
	private String usrAccess3;
	private String usrAccess4;
	private String usrAccess5;
	private String usrAccess6;
	private String usrDefined1;
	private String usrDefined2;
	private String usrDefined3;
	private List<ErrorDescriptionDto> errorList;
	private String errorField;
	private String newStateName;
	private String orgStateName;

}
