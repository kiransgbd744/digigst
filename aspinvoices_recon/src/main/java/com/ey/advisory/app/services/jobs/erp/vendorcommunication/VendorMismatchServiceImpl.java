package com.ey.advisory.app.services.jobs.erp.vendorcommunication;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */

@Service("VendorMismatchServiceImpl")
@Slf4j
public class VendorMismatchServiceImpl implements VendorMismatchService {

	@Autowired
	@Qualifier("VendorMismatchDaoImpl")
	VendorMismatchDao venMismatchDao;
	

	@Override
	public List<VendorMismatchDto> getVendorMismatchRecords(
			RevIntegrationScenarioTriggerDto req) {
		
		if (LOGGER.isDebugEnabled()) {
			String msg = "Calling Dao Layer From getVendorMismatchRecords from "
					+ "Service Layer ";
			LOGGER.debug(msg);
		}
		
		return venMismatchDao.findVendorMismatchRecords(req);
	}

	/*@Override
	public List<Pair<VendorMismatchRevRecordsDto, List<Long>>>
	 getDocsAsDtosByChunking(
			List<Object[]> objs) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Calling Dao Layer to Convert Docs to Dto "
					+ "From getVendorMismatchRecords from " + "Service Layer";
			LOGGER.debug(msg);
		}
		return venMismatchDao.convertDocsAsDtosByChunking(objs);
	}*/

	
}
