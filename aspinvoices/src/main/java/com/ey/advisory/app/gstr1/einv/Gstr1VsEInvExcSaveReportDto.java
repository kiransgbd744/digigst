/**
 * 
 */
package com.ey.advisory.app.gstr1.einv;

import java.time.LocalDate;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
public class Gstr1VsEInvExcSaveReportDto {

	private String sGstin;

	private String taxPeriod;

	private String docType;

	private String docNum;

	private LocalDate docDate;

	private String resp;

	private String errorCode;

	private String errorDesc;

}
