package com.ey.advisory.app.gstr2b.summary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.ey.advisory.gstr2.userdetails.EntityService;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */
@Slf4j
@Component("Gstr2BSummaryServiceImpl")
public class Gstr2BSummaryServiceImpl implements Gstr2BSummaryService {

	@Autowired
	@Qualifier("Gstr2BSummaryDaoImpl")
	private Gstr2BSummaryDao dao;

	@Autowired
	@Qualifier("EntityServiceImpl")
	private EntityService entityService;
	
	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;


	@Override
	public List<Gstr2BSummaryDto> getGstr2bSummary(List<String> gstins,
			String toTaxPeriod, String fromTaxPeriod) {

		List<Gstr2BSummaryDto> summaryResp = null;
		
		Map<String, String> authTokenStatus = authTokenService
				.getAuthTokenStatusForGstins(gstins);
		Map<String, String> stateNames = entityService.getStateNames(gstins);

		try {
			List<Gstr2BSummaryDto> dbResp = dao.getSummaryResp(gstins,
					toTaxPeriod, fromTaxPeriod);

			summaryResp = dbResp.stream().map(
					o -> setStateNameValue(o, stateNames.get(o.getGstin()), 
							authTokenStatus.get(o.getGstin())))
					.collect(Collectors.toCollection(ArrayList::new));

		} catch (Exception ex) {
			String msg = String.format(
					"Error occured in " + "Gstr2BSummaryServiceImpl ", ex);
			LOGGER.error(msg);
		}
		return summaryResp;
	}

	private Gstr2BSummaryDto setStateNameValue(Gstr2BSummaryDto dto,
			String stateName, String authToken ) {
		dto.setStateName(stateName);
		dto.setAuthToken(authToken);
		return dto;
	}

}
