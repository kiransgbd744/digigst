package com.ey.advisory.app.gstr1.einv;

import lombok.Data;

/**
 * @author Hema Gonur
 *
 */

@Data
public class Gstr1GSTINDeleteDataReportDto {

	private String sGstin;

	private String cGstin;

	private String returnPeriod;

	private String docType;

	private String docNum;

	private String docDate;

	private String pos;

	private String tableType;

	private String errorCode;

	private String errorDesc;

}
