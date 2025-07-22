package com.ey.advisory.app.service.ims.supplier;

import lombok.Data;

/**
 * 
 * @author ashutosh.kar
 *
 */

@Data
public class SupplierImsEntityDetailsResponseDto {

	private String gstin;
	private String authTokenStatus;
	private String state;
	private String regType;
	
	private String getCountStatus;
	private String getCountStatusDateTime;

	private SupplierEntityRecordDetail totalRecords;
	private SupplierEntityRecordDetail acceptedRecords;
	private SupplierEntityRecordDetail pendingRecords;
	private SupplierEntityRecordDetail rejectedRecords;
	private SupplierEntityRecordDetail noActionRecords;

	private boolean differenceWithGstr1And1A;

	private GstrSummaryStatus gstr1Summary;
	private GstrSummaryStatus gstr1aSummary;

}
