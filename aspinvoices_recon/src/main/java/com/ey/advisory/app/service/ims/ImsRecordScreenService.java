package com.ey.advisory.app.service.ims;

import java.util.List;

import org.springframework.stereotype.Component;

/**
 * 
 *  @author ashutosh.kar
 *
 */

@Component("ImsRecordScreenService")
public interface ImsRecordScreenService {

	List<ImsDigiGstTrailPopUpResponseDto> getImsDigiGstTrailPopupData(ImsEntitySummaryReqDto criteria);

	List<ImsGstnTrailPopUpResponseDto> getImsGstTrailPopupData(ImsEntitySummaryReqDto criteria);
	
}
