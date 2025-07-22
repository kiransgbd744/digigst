package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.ey.advisory.app.services.validation.b2cs.ErrorDescriptionDto;

import lombok.Data;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Data
public class Gstr1VerticalAtRespDto {

	private Long sNo;
	private Long id;
	private String gstin;
	private String taxPeriod;
	private String transType;
	private String month;
	private String orgPos;
	private String orgRate;
	private String orgAdvance;
	private String newPos;
	private String newRate;
	private String newAdvance;
	private String igst;
	private String cgst;
	private String sgst;
	private String cess;
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
	private String section;
	private String returnType;
}
