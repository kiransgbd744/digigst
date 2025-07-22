package com.ey.advisory.app.controllers.ims;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ImsErrorReportResponseDownloadDto {
	 private String tableType;
	    private String supplierGstin;
	    private String returnPeriod;
	    private String documentNumber;
	    private String errorCode;
	    private String errorMessage;
}
