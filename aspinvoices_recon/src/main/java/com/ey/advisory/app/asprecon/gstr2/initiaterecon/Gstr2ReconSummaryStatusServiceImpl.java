/**
 * 
 */
package com.ey.advisory.app.asprecon.gstr2.initiaterecon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.core.dto.Gstr2ReconSummaryStatusDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Service("Gstr2ReconSummaryStatusServiceImpl")
public class Gstr2ReconSummaryStatusServiceImpl
		implements Gstr2ReconSummaryStatusService {

	@Qualifier("Gstr2ReconSummaryStatusDaoImpl")
	@Autowired
	private Gstr2ReconSummaryStatusDao dao;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;

	private static final String NON_AP_M_2APR = "NON_AP_M_2APR";
	private static final String AP_M_2APR = "AP_M_2APR";

	@Autowired
	private GSTNDetailRepository gstinDetailRepo;

	private static final String REGULAR = "REGULAR";
	private static final String SEZ = "SEZ";
	private static final String SEZU = "SEZU";
	private static final String SEZD = "SEZD";
	private static final String ISD = "ISD";

	@Override
	public List<Gstr2ReconSummaryStatusDto> getReconDetailSummaryStatus(
			Long entityId, String toTaxPeriod2A, String fromTaxPeriod2A,
			String toTaxPeriodPR, String fromTaxPeriodPR, String fromDocDate,
			String toDocDate, String reconType) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Inside "
					+ "Gstr2ReconSummaryStatusServiceImpl with toTaxPeriod2A %s,  "
					+ "fromTaxPeriod2A %s,toTaxPeriodPR %s, fromTaxPeriodPR %s,"
					+ " toDocDate %s, fromDocDate %s entityId %d and reconType %s",
					toTaxPeriod2A, fromTaxPeriod2A, toTaxPeriodPR,
					fromTaxPeriodPR, toDocDate, fromDocDate, entityId,
					reconType);
			LOGGER.debug(msg);
		}

		List<String> regTypeList = new ArrayList<>(Arrays
				.asList(REGULAR, SEZ, SEZU, SEZD));

		Map<String, List<String>> dataSecurityAttrMap = new HashMap<>();
		Map<String, String> inwardSecurityAttributeMap = DataSecurityAttributeUtil
				.getInwardSecurityAttributeMap();
		dataSecurityAttrMap = DataSecurityAttributeUtil
				.dataSecurityAttrMapForQuery(Arrays.asList(entityId),
						inwardSecurityAttributeMap);
		List<String> dataSecList = dataSecurityAttrMap.get("GSTIN");
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Gstr2ReconSummaryStatusServiceImpl - dataSecurityAttrMap {} ",
					dataSecurityAttrMap.toString());
		}
		// sgstinsList = Arrays.asList("33GSPTN0482G1Z9", "33GSPTN0481G1ZA");
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr2ReconSummaryStatusServiceImpl - dataSecList {} ",
					dataSecList);
		}

		List<String> sgstinsList = dataSecList;
		
		if("2BPR".equalsIgnoreCase(reconType)) {
			sgstinsList = gstinDetailRepo
				.filterGstinBasedByRegType(dataSecList, regTypeList);
		}else {
			 
			regTypeList.add(ISD);
			
			sgstinsList = gstinDetailRepo
					.filterGstinBasedByRegType(dataSecList, regTypeList);
		}

		if (CollectionUtils.isEmpty(sgstinsList))
			throw new AppException("User Does not have any gstin");

		List<Long> entityIds = new ArrayList<Long>();
		try {

			entityIds.add(entityId);
			List<Long> optedEntities = entityConfigPemtRepo
					.getAllEntitiesOpted2B(entityIds, "I27");

			//check to see if entty has opted inward einvoice or not

			if (optedEntities == null || optedEntities.isEmpty()) {
				reconType = reconType.equalsIgnoreCase("2APR") ? NON_AP_M_2APR
						: reconType;
			} else {
				reconType = reconType.equalsIgnoreCase("2APR") ? AP_M_2APR
						: reconType;
			}

		} catch (Exception e) {
			throw new AppException("Gstr2ReconSummaryStatusServiceImpl : "
					+ "Exception occured during AP check");
		}

		List<Gstr2ReconSummaryStatusDto> gstr2ReconStatus = dao
				.findReconSummStatus(entityId, toTaxPeriod2A, fromTaxPeriod2A,
						toTaxPeriodPR, fromTaxPeriodPR, fromDocDate, toDocDate,
						reconType);

		List<Gstr2ReconSummaryStatusDto> finalGstr2ReconStatus = new ArrayList<>();

		for (Gstr2ReconSummaryStatusDto obj : gstr2ReconStatus) {
			String gstin = obj.getGstin();

			if (!CollectionUtils.isEmpty(sgstinsList)) {
				if (sgstinsList.contains(gstin)) {
					finalGstr2ReconStatus.add(obj);
				}
			}
		}
		return finalGstr2ReconStatus;
	}

}
