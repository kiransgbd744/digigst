package com.ey.advisory.app.services.search.anx2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.daos.client.anx2.Anx2GetAnx2SummaryDetailsDao;
import com.ey.advisory.app.docs.dto.anx2.Anx2GetAnx2SummaryDetailsReqDto;
import com.ey.advisory.app.docs.dto.anx2.Anx2GetFinalSummaryDetailsResDto;

/**
 * 
 * @author Anand3.M
 *
 */

@Service("Anx2GetAnx2SummaryDetailsService")
public class Anx2GetAnx2SummaryDetailsServiceImpl
		implements Anx2GetAnx2SummaryDetailsService {

@Autowired
@Qualifier("Anx2GetAnx2SummaryDetailsDao")
private Anx2GetAnx2SummaryDetailsDao anx2GetAnx2SummaryDetailsDao;

public Anx2GetFinalSummaryDetailsResDto loadSummaryDetails(
		Anx2GetAnx2SummaryDetailsReqDto anx2GetAnx2SummaryDetailsRequest) {

	// List<String> entityId = anx2GetAnx2SummaryDetailsRequest.getEntityId();

	String taxPeriod = anx2GetAnx2SummaryDetailsRequest.getTaxPeriod();
	List<String> selectedSgtins = anx2GetAnx2SummaryDetailsRequest.getGstins();
	List<String> data = anx2GetAnx2SummaryDetailsRequest.getData();
	return anx2GetAnx2SummaryDetailsDao
			.loadSummaryDetails(anx2GetAnx2SummaryDetailsRequest);
}

}
