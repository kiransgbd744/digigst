/**
 * 
 */
package com.ey.advisory.app.services.common;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;

import lombok.Data;

/**
 * @author Arun.KA
 *
 */

@Data
public class Gstr8AReportConvertor {
	
	private String cgstin;
	private String returnPeriod;
	private String sgstin;
	private String tableType;
	private String supplyType;
	private String docType;
	private String docNum;
	private String docDate;//converted to string
	private String originalNoteType;
	private String originalDocNum;
	private String originalDocDate;//converted to string
	private String originalInvNum;
	private String originalInvDate;//converted to string
	private String pos;
	private String reverseCharge;
	private BigDecimal invValue;
	private BigDecimal taxPayable;
	private BigDecimal igst;
	private BigDecimal cgst;
	private BigDecimal sgst;
	private BigDecimal cess;
	private String eligibleItc;
	private String reason;

}
