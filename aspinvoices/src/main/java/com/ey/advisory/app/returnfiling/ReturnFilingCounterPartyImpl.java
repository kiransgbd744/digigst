package com.ey.advisory.app.returnfiling;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author Arun.KA
 *
 */
@Component("ReturnFilingCounterPartyImpl")
public class ReturnFilingCounterPartyImpl implements ReturnFilingCounterParty {

	@Autowired
	@Qualifier("ReturnFilingCounterPartyDaoImpl")
	ReturnFilingCounterPartyDao returnFilingCounterPartyDao;

	@Override
	public List<ReturnFilingCounterPartyStatusDto> getCounterPartyStatus(
			String userName) {

		List<ReturnFilingCounterPartyStatusDto> counterPartyDetails = 
				returnFilingCounterPartyDao.getCounterPartyDetailsDB(userName);

		return counterPartyDetails;

	}

}
