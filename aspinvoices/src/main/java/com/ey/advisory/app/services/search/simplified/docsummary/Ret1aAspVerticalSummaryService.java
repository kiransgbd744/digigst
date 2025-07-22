package com.ey.advisory.app.services.search.simplified.docsummary;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.daos.client.simplified.Ret1AspVerticalSectionDaoImpl;
import com.ey.advisory.app.data.daos.client.simplified.Ret1GstnDetailsDaoImpl;
import com.ey.advisory.app.data.daos.client.simplified.Ret1VerticalSectionDaoImpl;
import com.ey.advisory.app.docs.dto.simplified.Ret1AspVerticalSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1SummaryReqDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1VerticalSummaryRespDto;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Service("Ret1aAspVerticalSummaryService")
public class Ret1aAspVerticalSummaryService {

	
	@Autowired
	@Qualifier("Ret1AspVerticalSectionDaoImpl")
	Ret1AspVerticalSectionDaoImpl loadData;

	@Autowired
	@Qualifier("Ret1GstnDetailsDaoImpl")
	Ret1GstnDetailsDaoImpl loadGstnData;

	@Autowired
	@Qualifier("Ret1VerticalSectionDaoImpl")
	Ret1VerticalSectionDaoImpl loadVertical;

	// For ASP Data
	@SuppressWarnings("null")
	public List<Ret1AspVerticalSummaryDto> find(Ret1SummaryReqDto req) {
		// TODO Auto-generated method stub
		List<Ret1AspVerticalSummaryDto> aspvertical = loadData
				.lateBasicSummarySectionRet1A(req);
		return aspvertical;
	}

	// For Gstn Data
	@SuppressWarnings("null")
	public List<Ret1AspVerticalSummaryDto> findgstnDetails(
			Ret1SummaryReqDto req) {
		// TODO Auto-generated method stub
		List<Ret1AspVerticalSummaryDto> gstnvertical = loadGstnData
				.lateBasicSummarySectionRet1A(req);

		if (gstnvertical == null) {
			Ret1AspVerticalSummaryDto defaulVertList = new Ret1AspVerticalSummaryDto();
			defaulVertList.setTaxableValue(BigDecimal.ZERO);
			defaulVertList.setIgst(BigDecimal.ZERO);
			defaulVertList.setCgst(BigDecimal.ZERO);
			defaulVertList.setSgst(BigDecimal.ZERO);
			defaulVertList.setCess(BigDecimal.ZERO);
			gstnvertical.add(defaulVertList);
		}
		return gstnvertical;
	}

	// Vertical Data
	@SuppressWarnings("null")
	public List<Ret1VerticalSummaryRespDto> findVerticalDetails(
			Ret1SummaryReqDto req) {
		// TODO Auto-generated method stub

		List<Ret1VerticalSummaryRespDto> verticalData = loadVertical
				.lateBasicSummarySectionRet1A(req);
		List<Ret1VerticalSummaryRespDto> verticalList = new ArrayList<>();
		if (verticalData.size() > 0) {
			verticalList.addAll(verticalData);
		} else {
			Ret1VerticalSummaryRespDto vertical = new Ret1VerticalSummaryRespDto();
			vertical.setReturnTable(req.getTable());
			vertical.setIgstAmt(BigDecimal.ZERO);
			vertical.setSgstAmt(BigDecimal.ZERO);
			vertical.setCgstAmt(BigDecimal.ZERO);
			vertical.setCessAmt(BigDecimal.ZERO);
			vertical.setValue(BigDecimal.ZERO);
			verticalList.add(vertical);
		}
		return verticalList;
	}
	
}
