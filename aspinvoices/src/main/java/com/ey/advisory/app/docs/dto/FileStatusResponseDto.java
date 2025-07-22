package com.ey.advisory.app.docs.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class FileStatusResponseDto {

	private String uploadedOn;
	private String uploadedBy;
	private String dataType;
	private String fileType;
	private String fileName;

	private String fileStatus;
	private Integer total;
	private Integer processedActive;
	private Integer processedInactive;
	private Integer strucError;
	private Integer errorsActive;
	private Integer errorsInactive;
	private Integer totalStrucBusinessError;
	
	private Long id;

	private Integer enivNA;
	private Integer enivAplicable;
	private Integer einvGenerated;
	private Integer einvINRInitiated;
	private Integer einvCancelled;
	private Integer einvErrorGigiGST;
	private Integer einvError;

	private Integer ewbNA;
	private Integer ewbApplicable;
	private Integer eWBGenerated;
	private Integer eWBInitiated;
	private Integer ewbCancelled;
	private Integer eWBErrorGigiGST;
	private Integer eWBError;
	
	
	private Integer ewbId;
	private Integer einvId;
	private Integer aspNA;
	private Integer gstnApplicable;
	private Integer aspProcess;
	private Integer infoActive;
	private Integer aspError;
	
	private Integer errorCount;
	
	private String errDescription;
	
	private String transformationStatus;
}