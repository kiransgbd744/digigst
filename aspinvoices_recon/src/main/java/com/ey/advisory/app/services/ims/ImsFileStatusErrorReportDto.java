package com.ey.advisory.app.services.ims;

import lombok.Data;

@Data
public class ImsFileStatusErrorReportDto extends ImsFileStatusReportDto{
	
	private String digiGstErrorDescription;
	private String errorId;
	private String errorDescription;
	

}
