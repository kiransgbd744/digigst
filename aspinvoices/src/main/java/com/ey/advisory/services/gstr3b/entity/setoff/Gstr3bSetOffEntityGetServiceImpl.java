package com.ey.advisory.services.gstr3b.entity.setoff;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.GstnUserRequestRepository;
import com.ey.advisory.app.gstr3b.Gstr3BGetLedgerDetails;
import com.ey.advisory.app.gstr3b.Gstr3BGstinDashboardService;
import com.ey.advisory.app.gstr3b.Gstr3BGstinsDto;
import com.ey.advisory.app.gstr3b.Gstr3bGstnSaveToAspService;
import com.ey.advisory.app.gstr3b.Gstr3bTaxPaymentDto;
import com.ey.advisory.app.gstr3b.Gstr3bUpdateGstnService;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.api.impl.APIError;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr3bSetOffEntityGetServiceImpl")
public class Gstr3bSetOffEntityGetServiceImpl
		implements Gstr3bSetOffEntityGetService {

	@Autowired
	@Qualifier("Gstr3bGstnSaveToAspServiceImpl")
	private Gstr3bGstnSaveToAspService aspService;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	private Gstr3BGstinDashboardService dashBoardService;

	@Autowired
	@Qualifier("Gstr3bUpdateGstnServiceImpl")
	Gstr3bUpdateGstnService gstr3bUpdateGstnService;

	@Autowired
	GstnUserRequestRepository gstnUserRequestRepo;

	@Autowired
	@Qualifier("Gstr3BGetLedgerDetailImpl")
	Gstr3BGetLedgerDetails gstr3bGetLedgerDetails;

	@Override
	public String getStatusData(List<String> gstins, String taxPeriod) {

		for (String gstin : gstins) {

			APIResponse apiResponse = gstr3bUpdateGstnService.getGstnCall(gstin,
					taxPeriod);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("After invoking getGSTR3BSummaryFromGSTN"
						+ ".getGSTR3BSummary() method " + "json response : "
						+ apiResponse);
			}

			if (!apiResponse.isSuccess()) {
				gstnUserRequestRepo.updateGstnResponse(null, 0, gstin,
						taxPeriod, APIConstants.GSTR3B, LocalDateTime.now());
				APIError error = apiResponse.getError();
				LOGGER.error("GSTR3B GET failed {}, {}, {} ", gstin, taxPeriod,
						error);
			} else {
				List<Gstr3BGstinsDto> resultList = dashBoardService
						.getGstrDtoList(apiResponse);
				List<Gstr3bTaxPaymentDto> taxPaymentResult = dashBoardService
						.getTaxPayemntList(apiResponse);

				String getGstnData = apiResponse.getResponse();

				if (LOGGER.isDebugEnabled()) {
					LOGGER.info("Before invoking saveGstnResponse() method "
							+ "resultList :" + resultList);
				}
				aspService.saveGstnResponse(gstin, taxPeriod, resultList,
						taxPaymentResult, getGstnData);

			}

			gstr3bGetLedgerDetails.getLedgerdetails(gstin, taxPeriod);

		}
		return "SUCCESS";
	}

}
