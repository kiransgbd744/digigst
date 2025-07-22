package com.ey.advisory.app.service.ims.supplier;

import java.util.List;

import lombok.Data;

/**
 * 
 * @author ashutosh.kar
 *
 */

@Data
public class SupplierImsGstinSummaryResponseDto {
	
	private List<SupplierImsGstinSectionSummaryDto> imsData;
	private List<SupplierImsGstinSectionSummaryDto> gstr1And1AData;
	
}
