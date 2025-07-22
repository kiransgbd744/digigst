package com.ey.advisory.app.service.ims;

import java.util.List;

import org.springframework.stereotype.Component;

/**
 * 
 *  @author ashutosh.kar
 *
 */

@Component("ImsGstinLevelSummaryService")
public interface ImsGstinLevelSummaryService {

	ImsGstinSummaryResponseWrapperDto getImsSummaryGstinLvlData(ImsEntitySummaryReqDto criteria);
}
