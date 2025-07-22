/**
 * 
 */
package com.ey.advisory.app.docs.dto.erp;

import java.time.LocalDate;

import lombok.Data;

/**
 * @author Hemasundar.J
 *
 */
@Data
public class AnxDataStatusResultDto {

	private LocalDate receivedDate;
	private Integer sapTotal = 0;
	private Integer totalRecords = 0;
	private Integer diff = 0;
	private Integer processedActive = 0;
	private Integer processedInactive = 0;
	private Integer errorActive = 0;
	private Integer errorInactive = 0;
	private Integer infoActive = 0;
	private Integer infoInactive = 0;

	private String gstin;
	private String retPeriod;
	private String division;
	private String profitCentre;
	private String location;
	private String plantcode;
	private String salesOrg;
	private String purchaseOrg;
	private String distributionChannel;
	private String userAccess1;
	private String userAccess2;
	private String userAccess3;
	private String userAccess4;
	private String userAccess5;
	private String userAccess6;

	private String entity;
	private String entityName;
	private String companyCode;

}
