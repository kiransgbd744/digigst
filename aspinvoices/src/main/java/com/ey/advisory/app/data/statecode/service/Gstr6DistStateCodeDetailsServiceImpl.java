package com.ey.advisory.app.data.statecode.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.statecode.dto.Gstr6DistributionStateCodeDetailsDto;
import com.ey.advisory.app.docs.services.gstr6.Gstr6DistributedStateCodeSummaryDao;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Dibyakanta.S
 *
 */

@Slf4j
@Service("Gstr6DistStateCodeDetailsServiceImpl")
public class Gstr6DistStateCodeDetailsServiceImpl
		implements Gstr6DistStateCodeDetailsService {

	@Autowired
	@Qualifier("Gstr6DistributedStateCodeSummaryDaoImpl")
	Gstr6DistributedStateCodeSummaryDao gstr6DistributedStateCodeSummaryDao;

	@Override
	public List<Gstr6DistributionStateCodeDetailsDto> findStates() {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside Gstr6DistributionSummaryStateCodeDetailsServiceImpl"
					+ ".getAllState, calling Gstr6DistributedStateCodeSummaryDao.";
			LOGGER.debug(msg);
		}
		
		List<Gstr6DistributionStateCodeDetailsDto> stateList = gstr6DistributedStateCodeSummaryDao.findGetState();
		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside Gstr6DistributionSummaryStateCodeDetailsServiceImpl"
					+ ".getAllState, Created Response.";
			LOGGER.debug(msg + " " + stateList);
		}
		return stateList;
	}

}
