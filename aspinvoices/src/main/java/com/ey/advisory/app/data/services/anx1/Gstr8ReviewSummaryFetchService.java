package com.ey.advisory.app.data.services.anx1;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.daos.client.Gstr8ReviewSummaryFetchDaoImpl;
import com.ey.advisory.app.data.views.client.Gstr8ReviewSummaryRespDto;
import com.ey.advisory.app.docs.dto.gstr1.Gstr8ReviewSummaryReqDto;

@Component("Gstr8ReviewSummaryFetchService")
public class Gstr8ReviewSummaryFetchService {

	@Autowired
	@Qualifier("Gstr8ReviewSummaryFetchDaoImpl")
	private Gstr8ReviewSummaryFetchDaoImpl gstr8ReviewSummaryFetchDaoImpl;

	@Transactional(value = "clientTransactionManager")
	public List<Gstr8ReviewSummaryRespDto> getReviewSummary(
			Gstr8ReviewSummaryReqDto req) {

		List<Gstr8ReviewSummaryRespDto> respDtos = gstr8ReviewSummaryFetchDaoImpl
				.loadGstr8ReviewSummary(req);

		return respDtos;

	}
}
