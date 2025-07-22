package com.ey.advisory.app.reconewbvsitc04;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.aspose.cells.Cells;
import com.aspose.cells.Workbook;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.core.dto.Gstr2ReconSummaryStatusDto;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Ravindra V S
 *
 */
@Slf4j
@Service("EwbvsItc04SummaryInitiateReconService")
public class EwbvsItc04SummaryInitiateReconService {

	@Autowired
	@Qualifier("EwbVsItc04SummaryInitiateReconDaoImpl")
	EwbVsItc04SummaryInitiateReconDao initiateReconDao;
	
	@Autowired
	private GSTNDetailRepository gstinDetailRepo;

	@Autowired
	CommonUtility commonUtility;
	
	private static final String REGULAR = "REGULAR";
	private static final String SEZU = "SEZU";
	private static final String SEZD = "SEZD";

	public List<EwbVsItc04SummaryInitiateReconLineItemDto> find(
			EwbVsItc04SummaryInitiateReconDto dto) {

		EwbVsItc04SummaryInitiateReconDto req = (EwbVsItc04SummaryInitiateReconDto) dto;

		List<EwbVsItc04SummaryInitiateReconLineItemDto> entityResponse = initiateReconDao
				.ewb3WayInitiateRecon(req);

		return entityResponse;

	}

	public Workbook getReport(EwbVsItc04SummaryInitiateReconDto criteria) {

		EwbVsItc04SummaryInitiateReconDto req = (EwbVsItc04SummaryInitiateReconDto) criteria;

		List<EwbVsItc04SummaryInitiateReconLineItemDto> entityResponse = initiateReconDao
				.ewb3WayInitiateRecon(req);

		return writeToExcel(entityResponse);
	}

	private Workbook writeToExcel(
			List<EwbVsItc04SummaryInitiateReconLineItemDto> entityResponse) {

		Workbook workbook = null;

		if (entityResponse != null && !entityResponse.isEmpty()) {

			String[] invoiceHeadersRecon = commonUtility
					.getProp("ewb.itc04.summary.report.header").split(",");

			if (LOGGER.isDebugEnabled()) {
				String msg = "EwbvsItc04SummaryInitiateReconService.writeToExcel "
						+ "workbook created writing data to the workbook";
				LOGGER.debug(msg);
			}

			workbook = commonUtility.createWorkbookWithExcelTemplate(
					"ReportTemplates", "EWBVsITC04Summary.xlsx");

			Cells reportCells = workbook.getWorksheets().get(0).getCells();

			reportCells.importCustomObjects(entityResponse, invoiceHeadersRecon,
					false, 2, 0, entityResponse.size(), true, "yyyy-mm-dd",
					false);
		}
		return workbook;
	}

	public List<Gstr2ReconSummaryStatusDto> getReconEwbVsItc04DetailSummaryStatus(
			EwbVsItc04SummaryInitiateReconDto criteria) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Inside "
							+ "getReconEwbVsItc04DetailSummaryStatus with"
							+ " entityId %d and criteria %s",
							criteria.getEntityId().get(0), criteria.getCriteria());
			LOGGER.debug(msg);
		}
		
		List<String> regTypeList = Arrays.asList(REGULAR,SEZU,SEZD);
		
		Long entityId = criteria.getEntityId().get(0);

		Map<String, List<String>> dataSecurityAttrMap = new HashMap<>();
		Map<String, String> inwardSecurityAttributeMap = DataSecurityAttributeUtil
				.getInwardSecurityAttributeMap();
		dataSecurityAttrMap = DataSecurityAttributeUtil
				.dataSecurityAttrMapForQuery(Arrays.asList(entityId),
						inwardSecurityAttributeMap);
	   List<String> sgstinsList = dataSecurityAttrMap.get("GSTIN");
	   if (CollectionUtils.isEmpty(sgstinsList))
			throw new AppException("User Does not have any gstin");


	   List<Gstr2ReconSummaryStatusDto> reconStatus = initiateReconDao
				.findEwbVsItc04SummStatus(entityId, criteria);
		
	   List <String> gstnsList = gstinDetailRepo
				.filterGstinBasedByRegType(sgstinsList, regTypeList);
	
		List<Gstr2ReconSummaryStatusDto> finalReconStatus = new ArrayList<>();

		for (Gstr2ReconSummaryStatusDto obj : reconStatus) {
			String gstin = obj.getGstin();

			if (!CollectionUtils.isEmpty(gstnsList) ) {
				if (gstnsList.contains(gstin)) {
					finalReconStatus.add(obj);
				}
			}
		}
		return finalReconStatus;
	}

}
