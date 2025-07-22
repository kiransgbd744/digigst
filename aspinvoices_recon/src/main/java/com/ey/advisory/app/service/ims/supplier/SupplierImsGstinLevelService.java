package com.ey.advisory.app.service.ims.supplier;

import org.springframework.stereotype.Component;

/**
 * 
 *  @author ashutosh.kar
 *
 */

@Component("SupplierImsGstinLevelService")
public interface SupplierImsGstinLevelService {

	SupplierImsGstinSummaryResponseDto getSupplierImsSummaryGstinLvlData(SupplierImsEntityReqDto criteria);

}
