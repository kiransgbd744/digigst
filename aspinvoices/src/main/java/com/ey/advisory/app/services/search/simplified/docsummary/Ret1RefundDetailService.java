package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.daos.client.simplified.Ret1PaymentTaxDetailSectionDaoImpl;
import com.ey.advisory.app.docs.dto.simplified.Ret1LateFeeDetailSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1RefundDetailSummaryDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Service("Ret1RefundDetailService")
public class Ret1RefundDetailService implements SearchService{

	@Autowired
	@Qualifier("Ret1PaymentTaxDetailSectionDaoImpl")
	Ret1PaymentTaxDetailSectionDaoImpl refund;
	
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {
		Annexure1SummaryReqDto req = (Annexure1SummaryReqDto) criteria;
		List<Ret1RefundDetailSummaryDto> loadRefund = refund.loadRefundSummarySection(req);
		return (SearchResult<R>) new SearchResult<Ret1RefundDetailSummaryDto>(
				loadRefund);
	}

	@Override
	public <R> Stream<R> find(SearchCriteria criteria,
			Class<? extends R> retType) {
		// TODO Auto-generated method stub
		return null;
	}

}
