/**
 * 
 */
package com.ey.advisory.service.gstr1.sales.register;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.core.dto.Gstr2ReconSummaryStatusDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */

@Slf4j
@Service("Gstr1GstinSummaryStatusServiceImpl")
public class Gstr1GstinSummaryStatusServiceImpl
		implements Gstr1GstinSummaryStatusService {

	@Qualifier("Gstr1SalesRegisterSummaryStatusDaoImpl")
	@Autowired
	private Gstr1SalesRegisterSummaryStatusDao dao;

	@Autowired
	private GSTNDetailRepository gstinDetailRepo;
	
	private static final String REGULAR = "REGULAR";
	private static final String SEZU = "SEZU";
	private static final String SEZD = "SEZD";
	
	@Override
	public List<Gstr2ReconSummaryStatusDto> getSalesRegisterGstinSummaryStatus(
			Long entityId, String fromReturnPeriod, String toReturnPeriod,
			String criteria) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Inside "
							+ "Gstr1GstinSummaryStatusServiceImpl with fromReturnPeriod %s,  "
							+ "toReturnPeriod %s entityId %d and criteria %s",
					fromReturnPeriod, toReturnPeriod, entityId, criteria);
			LOGGER.debug(msg);
		}
		
		List<String> regTypeList = Arrays.asList(REGULAR, SEZU,SEZD);
		
		
		Map<String, List<String>> dataSecurityAttrMap = new HashMap<>();
		Map<String, String> inwardSecurityAttributeMap = DataSecurityAttributeUtil
				.getInwardSecurityAttributeMap();
		dataSecurityAttrMap = DataSecurityAttributeUtil
				.dataSecurityAttrMapForQuery(Arrays.asList(entityId),
						inwardSecurityAttributeMap);
	   List<String> sgstinsList = dataSecurityAttrMap.get("GSTIN");
		if (CollectionUtils.isEmpty(sgstinsList))
			throw new AppException("User Does not have any gstin");

		List<Gstr2ReconSummaryStatusDto> salesRegisterStatus = dao
				.findsalesRegisterGstinStatus(entityId, fromReturnPeriod,
						toReturnPeriod, criteria);
		List <String> gstnsList = gstinDetailRepo
				.filterGstinBasedByRegType(sgstinsList, regTypeList);
	
		List<Gstr2ReconSummaryStatusDto> finalsalesRegisterStatus = new ArrayList<>();

		for (Gstr2ReconSummaryStatusDto obj : salesRegisterStatus) {
			String gstin = obj.getGstin();

			if (!CollectionUtils.isEmpty(gstnsList) ) {
				if (gstnsList.contains(gstin)) {
					finalsalesRegisterStatus.add(obj);
				}
			}
		}
		return finalsalesRegisterStatus;
	}
}
