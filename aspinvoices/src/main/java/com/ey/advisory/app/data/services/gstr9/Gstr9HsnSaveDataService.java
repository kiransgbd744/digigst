package com.ey.advisory.app.data.services.gstr9;

import java.util.List;

import com.ey.advisory.app.docs.dto.gstr9.Gstr9HsnDetailsSummaryDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9ItemsResponseDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table17ItemsReqDto;

/**
 * 
 * @author Rajesh
 *
 */
public interface Gstr9HsnSaveDataService {

	String saveHsnOutwardInwardData(List<Gstr9Table17ItemsReqDto> req, String type, 
			String gstin, String fy);
	
	String deleteHsnOutwardInwardData(List<Gstr9Table17ItemsReqDto> req, String type, 
			String gstin, String fy);

	List<Gstr9ItemsResponseDto> getHsnProcessedData(String gstin,
			String fyYear, String type);

	List<Gstr9HsnDetailsSummaryDto> getHsnSummaryDetails(String gstin,
			String fyYear);

}
