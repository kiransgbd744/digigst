package com.ey.advisory.app.data.services.gstr9;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9HsnProcessedRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9UserInputRepository;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9GstinInOutwardDashBoardDTO;
import com.ey.advisory.common.GSTR9Constants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Slf4j
@Component
public class Gstr9OutwardUtil {

	@Autowired
	private Gstr9UserInputRepository gstr9UserInputRepository;
	
	@Autowired
	private Gstr9HsnProcessedRepository gstr9HsnProcesRepository;
	

	public Map<String, Gstr9GstinInOutwardDashBoardDTO> getGstr9OutwardGstinDashboardMap() {
		Map<String, Gstr9GstinInOutwardDashBoardDTO> map = new LinkedHashMap<>();

		map.put(GSTR9Constants.Table_4, new Gstr9GstinInOutwardDashBoardDTO(
				GSTR9Constants.Table_4, true));
		map.put(GSTR9Constants.Table_4A, new Gstr9GstinInOutwardDashBoardDTO(
				GSTR9Constants.Table_4A, false));
		map.put(GSTR9Constants.Table_4B, new Gstr9GstinInOutwardDashBoardDTO(
				GSTR9Constants.Table_4B, false));
		map.put(GSTR9Constants.Table_4C, new Gstr9GstinInOutwardDashBoardDTO(
				GSTR9Constants.Table_4C, false));
		map.put(GSTR9Constants.Table_4D, new Gstr9GstinInOutwardDashBoardDTO(
				GSTR9Constants.Table_4D, false));
		map.put(GSTR9Constants.Table_4E, new Gstr9GstinInOutwardDashBoardDTO(
				GSTR9Constants.Table_4E, false));
		map.put(GSTR9Constants.Table_4F, new Gstr9GstinInOutwardDashBoardDTO(
				GSTR9Constants.Table_4F, false));
		map.put(GSTR9Constants.Table_4G, new Gstr9GstinInOutwardDashBoardDTO(
				GSTR9Constants.Table_4G, false));
		map.put(GSTR9Constants.Table_4G1, new Gstr9GstinInOutwardDashBoardDTO(
				GSTR9Constants.Table_4G1, false));
		map.put(GSTR9Constants.Table_4H, new Gstr9GstinInOutwardDashBoardDTO(
				GSTR9Constants.Table_4H, false));
		map.put(GSTR9Constants.Table_4I, new Gstr9GstinInOutwardDashBoardDTO(
				GSTR9Constants.Table_4I, false));
		map.put(GSTR9Constants.Table_4J, new Gstr9GstinInOutwardDashBoardDTO(
				GSTR9Constants.Table_4J, false));
		map.put(GSTR9Constants.Table_4K, new Gstr9GstinInOutwardDashBoardDTO(
				GSTR9Constants.Table_4K, false));
		map.put(GSTR9Constants.Table_4L, new Gstr9GstinInOutwardDashBoardDTO(
				GSTR9Constants.Table_4L, false));
		map.put(GSTR9Constants.Table_4M, new Gstr9GstinInOutwardDashBoardDTO(
				GSTR9Constants.Table_4M, false));
		map.put(GSTR9Constants.Table_4N, new Gstr9GstinInOutwardDashBoardDTO(
				GSTR9Constants.Table_4N, false));
		

		map.put(GSTR9Constants.Table_5, new Gstr9GstinInOutwardDashBoardDTO(
				GSTR9Constants.Table_5, true));
		map.put(GSTR9Constants.Table_5A, new Gstr9GstinInOutwardDashBoardDTO(
				GSTR9Constants.Table_5A, false));
		map.put(GSTR9Constants.Table_5B, new Gstr9GstinInOutwardDashBoardDTO(
				GSTR9Constants.Table_5B, false));
		map.put(GSTR9Constants.Table_5C, new Gstr9GstinInOutwardDashBoardDTO(
				GSTR9Constants.Table_5C, false));
		map.put(GSTR9Constants.Table_5C1, new Gstr9GstinInOutwardDashBoardDTO(
				GSTR9Constants.Table_5C1, false));
		map.put(GSTR9Constants.Table_5D, new Gstr9GstinInOutwardDashBoardDTO(
				GSTR9Constants.Table_5D, false));
		map.put(GSTR9Constants.Table_5E, new Gstr9GstinInOutwardDashBoardDTO(
				GSTR9Constants.Table_5E, false));
		map.put(GSTR9Constants.Table_5F, new Gstr9GstinInOutwardDashBoardDTO(
				GSTR9Constants.Table_5F, false));
		map.put(GSTR9Constants.Table_5G, new Gstr9GstinInOutwardDashBoardDTO(
				GSTR9Constants.Table_5G, false));
		map.put(GSTR9Constants.Table_5H, new Gstr9GstinInOutwardDashBoardDTO(
				GSTR9Constants.Table_5H, false));
		map.put(GSTR9Constants.Table_5I, new Gstr9GstinInOutwardDashBoardDTO(
				GSTR9Constants.Table_5I, false));
		map.put(GSTR9Constants.Table_5J, new Gstr9GstinInOutwardDashBoardDTO(
				GSTR9Constants.Table_5J, false));
		map.put(GSTR9Constants.Table_5K, new Gstr9GstinInOutwardDashBoardDTO(
				GSTR9Constants.Table_5K, false));
		map.put(GSTR9Constants.Table_5L, new Gstr9GstinInOutwardDashBoardDTO(
				GSTR9Constants.Table_5L, false));
		map.put(GSTR9Constants.Table_5M, new Gstr9GstinInOutwardDashBoardDTO(
				GSTR9Constants.Table_5M, false));
		map.put(GSTR9Constants.Table_5N, new Gstr9GstinInOutwardDashBoardDTO(
				GSTR9Constants.Table_5N, false));		
		
		
		return map;
	}

	public int softDeleteData(String gstin, String fy,
			List<String> sectionList, String createdBy) {
		int deleteCount = gstr9UserInputRepository.softDeleteBasedOnGstinandFy(
				gstin, fy, sectionList, "U", createdBy);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Gstr9OutwardServiceImpl"
					+ ".savegstr9DashboardUserInputs soft delete count "
					+ deleteCount);
		}
		return deleteCount;
	}

	public int hsnsoftDeleteData(String gstin, String taxPeriod,
			List<String> sectionList, String createdBy) {
		int deleteCount = gstr9HsnProcesRepository.softDeleteActiveEntries(
				gstin, taxPeriod, sectionList, createdBy);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Gstr9OutwardServiceImpl"
					+ ".savegstr9DashboardUserInputs soft delete count "
					+ deleteCount);
		}
		return deleteCount;
	}

}
