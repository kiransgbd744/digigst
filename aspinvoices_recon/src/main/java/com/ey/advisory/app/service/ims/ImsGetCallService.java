package com.ey.advisory.app.service.ims;

import java.util.List;

/**
 * 
 * @author ashutosh.kar
 *
 */

public interface ImsGetCallService {

	List<ImsGetCallResponseDto> getImsCallSummary(ImsEntitySummaryReqDto criteria);

	List<ImsGetCallPopUpResponseDto> getImsDetailCallPopupData(String gstin);

}
