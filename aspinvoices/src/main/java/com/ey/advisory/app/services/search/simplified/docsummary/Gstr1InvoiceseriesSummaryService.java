package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.daos.client.simplified.Gstr1InvSeriesSectionDao;
import com.ey.advisory.app.docs.dto.Gstr1VerticalDocSeriesRespDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Service("Gstr1InvoiceseriesSummaryService")
public class Gstr1InvoiceseriesSummaryService {

	@Autowired
	@Qualifier("Gstr1InvSeriesSectionDaoImpl")
	private Gstr1InvSeriesSectionDao loadData;

	public List<Gstr1VerticalDocSeriesRespDto> findgstnDetails(
			Annexure1SummaryReqDto req) {
		List<Gstr1VerticalDocSeriesRespDto> gstinView = loadData
				.gstinViewSection(req);
		return gstinView;
	}
}
