/**
 * 
 */
package com.ey.advisory.app.recon3way;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
import com.ey.advisory.gstr2.initiaterecon.EWBSummaryDataRequestStatusDao;
import com.ey.advisory.gstr2.initiaterecon.EWBSummaryDataRequestStatusDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Service("EWBSummaryDataRequestServiceImpl")
public class EWBSummaryDataRequestServiceImpl
		implements EWBSummaryDataRequestStatusService {

	@Autowired
	@Qualifier("EWBSummaryDataRequestStatusDaoImpl")
	private EWBSummaryDataRequestStatusDao requestStatusDao;
	
	@Autowired
	private GSTNDetailRepository gstinDetailRepo;
	
	private static final String REGULAR = "REGULAR";
	private static final String SEZU = "SEZU";
	private static final String SEZD = "SEZD";
	
	@Override
	public List<EWBSummaryDataRequestStatusDto> getRequestDataStatus(
			List<String> gstinlist, String criteria, String fromDate,
			String toDate,Long entityId) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin EWB3WayReportRequestStatusServiceImpl"
					+ ".getReportRequestStatus ,"
					+ "inside the getReportRequestStatus() method";
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

		List<EWBSummaryDataRequestStatusDto> response = requestStatusDao
				.getRequestDataSummaryStatus(gstinlist, criteria, fromDate, toDate);
		
		List <String> gstnsList = gstinDetailRepo
				.filterGstinBasedByRegType(sgstinsList, regTypeList);
		
	
		List<EWBSummaryDataRequestStatusDto> finalRecon3WayStatus = new ArrayList<>();

		for (EWBSummaryDataRequestStatusDto obj : response) {
			String gstin = obj.getGstin();

			if (!CollectionUtils.isEmpty(gstnsList)) {
				if (gstnsList.contains(gstin)) {
					obj.setRegType(getRegType(gstin));
					finalRecon3WayStatus.add(obj);
					
				}
			}
		}
		finalRecon3WayStatus.sort(Comparator
				.comparing(EWBSummaryDataRequestStatusDto::getGstin));
		return finalRecon3WayStatus;
	}

	private String getRegType(String gstin) {
		List<String> regName = gstinDetailRepo.findRegTypeByGstin(gstin);
		String s = "";
		if (regName != null && regName.size() > 0) {
				String regTypeName = regName.get(0);
				if (regTypeName == null
				|| regTypeName.equalsIgnoreCase("normal")
				|| regTypeName.equalsIgnoreCase("regular")) {
				return s;
				} else {
				return (regTypeName.toUpperCase());
				}
				} else {
				return s;
				}
	}

}