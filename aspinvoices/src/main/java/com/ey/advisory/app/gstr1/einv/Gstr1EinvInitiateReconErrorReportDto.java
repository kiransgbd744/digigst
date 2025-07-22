package com.ey.advisory.app.gstr1.einv;

import lombok.Data;

@Data
public class Gstr1EinvInitiateReconErrorReportDto extends Gstr1EinvInitiateReconReportDto{
	
	private String errorId;
	private String errorDescription;

}
