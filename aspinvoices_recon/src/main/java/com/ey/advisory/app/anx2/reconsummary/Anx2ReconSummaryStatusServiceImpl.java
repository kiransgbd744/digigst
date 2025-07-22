package com.ey.advisory.app.anx2.reconsummary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.common.AppException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("anx2ReconSummaryStatusServiceImpl")
public class Anx2ReconSummaryStatusServiceImpl
		implements Anx2ReconSummaryStatusService {

	@Autowired
	@Qualifier("Anx2ReconSummaryStatusDaoImpl")
	Anx2ReconSummaryStatusDao anxStatusDao;

	@Override
	public List<Anx2ReconSummaryStatusDto> getReconDetailSummaryStatus(
			int taxPeriod, Long entityId) {
		// TODO Auto-generated method stub
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"In Anx2ReconSummaryStatusServiceImpl with"
							+ "taxPeriod %d",
					taxPeriod);
			LOGGER.debug(msg);
		}
		Map<String, List<String>> dataSecurityAttrMap = new HashMap<>();
		Map<String, String> inwardSecurityAttributeMap 
		= DataSecurityAttributeUtil
				.getInwardSecurityAttributeMap();
		dataSecurityAttrMap = DataSecurityAttributeUtil
				.dataSecurityAttrMapForQuery(
						Arrays.asList(entityId),
						inwardSecurityAttributeMap);
		List<String> sgstinsList = dataSecurityAttrMap.get("GSTIN");
	 	if(CollectionUtils.isEmpty(sgstinsList))
			throw new AppException("User Does not have any gstin");
		
		List<Anx2ReconSummaryStatusDto> anx2ReconStatus = 
				 anxStatusDao.findReconSummStatus(entityId, taxPeriod);
		
		List<Anx2ReconSummaryStatusDto> finalAnx2ReconStatus =
				new ArrayList<>();
		
		for(Anx2ReconSummaryStatusDto obj : anx2ReconStatus) {
			String gstin = obj.getGstin();
			
			if(!CollectionUtils.isEmpty(sgstinsList)) {
				if(sgstinsList.contains(gstin)) {
					finalAnx2ReconStatus.add(obj);
				}
			}
		}
		return finalAnx2ReconStatus;
	}

}
