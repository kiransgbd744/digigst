package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.daos.client.simplified.Gstr1AdvancedSectionDao;
import com.ey.advisory.app.docs.dto.Gstr1AdvancedVerticalSummaryRespDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1AspVerticalSummaryDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * 
 * @author Sasidhar
 *
 */

@Service("Gstr1AdvancedSummaryService")
public class Gstr1AdvancedSummaryService {

	@Autowired
	@Qualifier("Gstr1AdvancedSectionDaoImpl")
	Gstr1AdvancedSectionDao loadData;

	public List<Ret1AspVerticalSummaryDto> findgstnDetails(
			Annexure1SummaryReqDto req) {
		List<Ret1AspVerticalSummaryDto> gstinView = loadData
				.gstinViewSection(req);
		return gstinView;
	}

	public List<Gstr1AdvancedVerticalSummaryRespDto> findVerticalDetails(
			Annexure1SummaryReqDto req) {
		List<Gstr1AdvancedVerticalSummaryRespDto> verticalData = loadData
				.verticalSummarySection(req);
		return verticalData;
	}

	public List<Ret1AspVerticalSummaryDto> findSummaryDetails(
			Annexure1SummaryReqDto req) {
		List<Ret1AspVerticalSummaryDto> summaryData = loadData
				.SummarySectionData(req);
		return summaryData;
	}
}
