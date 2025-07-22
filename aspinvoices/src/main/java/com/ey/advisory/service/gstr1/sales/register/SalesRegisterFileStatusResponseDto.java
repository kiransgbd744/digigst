package com.ey.advisory.service.gstr1.sales.register;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class SalesRegisterFileStatusResponseDto {

	private Long id;
	private String uploadedOn;
	private String uploadedBy;
	private String fileName;
	private String totalRecords;
	private String processedRecords;
	private String errorRecords;
	private String status;
}