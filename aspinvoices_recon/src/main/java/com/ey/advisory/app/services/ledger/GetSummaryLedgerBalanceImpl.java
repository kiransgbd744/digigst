package com.ey.advisory.app.services.ledger;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.ey.advisory.gstr2.userdetails.EntityService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 */
@Slf4j
@Component("GetSummaryLedgerBalanceImpl")
public class GetSummaryLedgerBalanceImpl implements GetSummaryLedgerBalance {

	@Autowired
	@Qualifier("GetSummaryLedgerBalanceDaoImpl")
	GetSummaryLedgerBalanceDao getSummaryLedgerBalance;

	@Autowired
	@Qualifier("EntityServiceImpl")
	EntityService entityService;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@Override
	public List<GetSummaryLedgerBalanceDto> getSummaryLedgerBalanceDetails(
			Gstr1GetInvoicesReqDto req) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"About to Fetch Ledger Summary for req '%s' ",
					req);
			LOGGER.debug(msg);
		}

		List<String> gstins = entityService.getGSTINsForEntity(req.getEntityId());

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Gstin list for Ledger Summary : %s",
					gstins);
			LOGGER.debug(msg);
		}

		Map<String, String> stateNameMap = entityService.getStateNames(gstins);

		Map<String, String> authTokenStatusMap = authTokenService
				.getAuthTokenStatusForGstins(gstins);

		List<GetSummaryLedgerBalanceDto> resp = getSummaryLedgerBalance
				.getSummaryLedgerDetails(gstins);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("DB response for Ledger Summary : %s",
					resp.toString());
			LOGGER.debug(msg);
		}

		resp.stream().forEach(obj -> {
			obj.setStatus(authTokenStatusMap.get(obj.getGstin()));
			obj.setState(stateNameMap.get(obj.getGstin()));
		});

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Response after setting authToken and "
							+ "stateName for Ledger Summary : %s",
					resp.toString());
			LOGGER.debug(msg);
		}

		return resp;
	}

}
