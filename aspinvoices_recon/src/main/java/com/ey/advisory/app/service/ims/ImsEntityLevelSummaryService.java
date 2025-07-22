package com.ey.advisory.app.service.ims;

import java.util.List;

import org.springframework.stereotype.Component;

/**
 * 
 *  @author ashutosh.kar
 *
 */

@Component("ImsEntityLevelSummaryService")
public interface ImsEntityLevelSummaryService {

	List<ImsEntitySummaryResponseDto> getImsSummaryEntityLvlData(ImsEntitySummaryReqDto criteria);

	List<ImsSaveSatusPopUpResponseDto> getImsDetailCallPopupData(String gstin);

	List<ImsSaveQueueStatusResponseDto> getImsDetailQueueData(String gstin);

}
