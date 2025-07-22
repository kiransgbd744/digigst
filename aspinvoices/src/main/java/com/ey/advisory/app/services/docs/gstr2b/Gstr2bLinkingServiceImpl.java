/**
 * 
 */
package com.ey.advisory.app.services.docs.gstr2b;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.Gstr2BLinkingConfigEntity;
import com.ey.advisory.app.data.repositories.client.Gstr2bLinkingConfigRepository;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.docs.dto.Gstr2BLinkingDto;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;
import com.google.common.collect.Lists;

/**
 * @author Shashikant.Shukla
 *
 */
@Service("Gstr2bLinkingServiceImpl")
public class Gstr2bLinkingServiceImpl implements Gstr2bLinkingService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstnDetailRepository;

	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository statecodeRepository;

	@Autowired
	@Qualifier("Gstr2bLinkingConfigRepository")
	private Gstr2bLinkingConfigRepository linkingRepo;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;

	@Override
	public List<Gstr2bLinkingResponseDto> getGstr2bLinkingRecords(
			Gstr2BLinkingDto dto) {
		List<Object[]> objs = getGstr2BLinkingProcRecords(dto);
		return convertProcessedData(dto, objs);

	}

	private List<Gstr2bLinkingResponseDto> convertProcessedData(
			Gstr2BLinkingDto dto, List<Object[]> arrs) {

		List<String> gstinList = gstnDetailRepository
				.findgstinByEntityIdWithRegTypeForGstr1(dto.getEntityId());

		List<Gstr2bLinkingResponseDto> linkingResponseList = new ArrayList<>();

		if (arrs != null && !arrs.isEmpty()) {
			List<Gstr2bLinkingDto> gstr2bLinkingData = convertGstr2bLinkingData(
					dto, arrs, gstinList);

			Map<String, List<Gstr2bLinkingRespDto>> mapGstr2bLinkingResp = mapGstr2bLinkingResp(
					gstr2bLinkingData);
			gstinList.forEach(gstin -> {
				Gstr2bLinkingResponseDto linkingResponseDto = new Gstr2bLinkingResponseDto();
				linkingResponseDto.setGstin(gstin);
				String gstintoken = defaultGSTNAuthTokenService
						.getAuthTokenStatusForGstin(gstin);
				if (gstintoken != null) {
					if ("A".equalsIgnoreCase(gstintoken)) {
						linkingResponseDto.setAuthToken("A");
					} else {
						linkingResponseDto.setAuthToken("I");
					}
				} else {
					linkingResponseDto.setAuthToken("I");
				}
				linkingResponseDto.setStateName(statecodeRepository
						.findStateNameByCode(gstin.substring(0, 2)));

				List<Gstr2bLinkingRespDto> gstr2blinkingRespDtos = mapGstr2bLinkingResp
						.get(gstin);
				linkingResponseDto.setItem(gstr2blinkingRespDtos);

				linkingResponseList.add(linkingResponseDto);
			});
		}
		return linkingResponseList;
	}

	private Map<String, List<Gstr2bLinkingRespDto>> mapGstr2bLinkingResp(
			List<Gstr2bLinkingDto> gstr2blinkingDtos) {

		Map<String, List<Gstr2bLinkingRespDto>> mapGstr2bLinkingResp = new HashMap<>();
		gstr2blinkingDtos.forEach(linkingDto -> {
			String key = linkingDto.getGstin();
			Gstr2bLinkingRespDto dto = new Gstr2bLinkingRespDto();
			dto.setTaxPeriod(linkingDto.getTaxPeriod());
			dto.setLinked(linkingDto.getLinked());
			dto.setNotLinked(linkingDto.getNotLinked());
			dto.setLastUpdatedOn(linkingDto.getLastUpdatedOn());
			dto.setLinkingStatus(linkingDto.getLinkingStatus());
			mapGstr2bLinkingResp.computeIfAbsent(key, k -> new ArrayList<>())
					.add(dto);
		});

		return mapGstr2bLinkingResp;

	}

	public List<Object[]> getGstr2BLinkingProcRecords(Gstr2BLinkingDto dto) {

		List<Object[]> list = new ArrayList<>();

		List<String> gstinList = gstnDetailRepository
				.findgstinByEntityIdWithRegTypeForGstr1(dto.getEntityId());
		List<String> gstins = new ArrayList<>();

		List<String> taxPeriods = GenUtil
				.extractTaxPeriodsFromFY(dto.getFinancialYear(), "Gstr1");
		String taxPeriod = String.join(",", taxPeriods);

		if (gstinList != null && !gstinList.isEmpty()) {

			List<List<String>> chunks = Lists.partition(gstinList, 200);
			for (List<String> chunk : chunks) {
				gstins.add(String.join(",", chunk));
			}

			for (String gstin : gstins) {
				StoredProcedureQuery storedProc = procCall(gstin, taxPeriod);
				list.addAll(storedProc.getResultList());
			}
		}

		return list;
	}

	private StoredProcedureQuery procCall(String gstin, String taxPeriod) {

		StoredProcedureQuery storedProc = entityManager
				.createStoredProcedureQuery("USP_GTR2B_LINKING_SCREEN_UI");

		storedProc.registerStoredProcedureParameter("GSTIN", String.class,
				ParameterMode.IN);

		storedProc.setParameter("GSTIN", gstin);

		storedProc.registerStoredProcedureParameter("TAX_PERIOD", String.class,
				ParameterMode.IN);

		storedProc.setParameter("TAX_PERIOD", taxPeriod);

		return storedProc;
	}

	/*
	 * private List<String> getTaxPeriods(String fy) { List<String>
	 * allTaxPeriods = getAllTaxPeriods(fy); List<String> taxPeriods = new
	 * ArrayList<>(); Integer derivedTaxPeriod = GenUtil
	 * .getDerivedTaxPeriod(GenUtil.getCurrentTaxPeriod().toString()); for
	 * (String taxPeriod : allTaxPeriods) { if (derivedTaxPeriod >
	 * GenUtil.getDerivedTaxPeriod(taxPeriod)) taxPeriods.add(taxPeriod); }
	 * return taxPeriods; }
	 * 
	 * public static List<String> getAllTaxPeriods(String fy) { List<String>
	 * monthsList = new ArrayList<>();
	 * 
	 * String[] splitVal = fy.split("-"); String financialYear = splitVal[0] +
	 * "-20" + splitVal[1];
	 * 
	 * // Parse the input financial year string int startYear =
	 * Integer.parseInt(financialYear.substring(0, 4)); int endYear =
	 * Integer.parseInt(financialYear.substring(5));
	 * 
	 * if (!GenUtil.getCurrentFinancialYear().equalsIgnoreCase(fy)) {
	 * 
	 * // Iterate over each month from April to March for each year for (int
	 * year = startYear; year <= endYear; year++) { for (Month month :
	 * Month.values()) { // Check if it's April to March cycle if (month ==
	 * Month.APRIL || monthsList.size() > 0) { // Format the month and year to
	 * "MMyyyy" format String monthYear = String.format("%02d%04d",
	 * month.getValue(), year); monthsList.add(monthYear); } // Break loop when
	 * reached March of the end year if (year == endYear && month ==
	 * Month.MARCH) { break; } } } } else { String currentTaxPeriod =
	 * GenUtil.getCurrentTaxPeriod(); int currentDerivedTaxPeriod = GenUtil
	 * .getDerivedTaxPeriod(currentTaxPeriod); for (int year = startYear; year
	 * <= endYear; year++) { for (Month month : Month.values()) { // Check if
	 * it's April to March cycle if (month == Month.APRIL || monthsList.size() >
	 * 0) { // Format the month and year to "MMyyyy" format String monthYear =
	 * String.format("%02d%04d", month.getValue(), year); int derivedMonthYear =
	 * GenUtil .getDerivedTaxPeriod(monthYear); if (derivedMonthYear <=
	 * currentDerivedTaxPeriod) monthsList.add(monthYear); } // Break loop when
	 * reached March of the end year if (year == endYear && month ==
	 * Month.MARCH) { break; } } } } return monthsList; }
	 */

	public static void main(String[] args) {
		System.out.println(GenUtil.extractTaxPeriodsFromFY("2024-25", "Gstr1"));
	}

	private List<Gstr2bLinkingDto> convertGstr2bLinkingData(
			Gstr2BLinkingDto dto, List<Object[]> arrs, List<String> gstinList) {
		List<String> taxPeriods = GenUtil
				.extractTaxPeriodsFromFY(dto.getFinancialYear(), "Gstr1");
		List<Gstr2BLinkingConfigEntity> configEntities = linkingRepo
				.findByGstinTaxPeriod(gstinList, taxPeriods);
		Map<String, Gstr2BLinkingConfigEntity> configmap = new HashMap<>();
		for (Gstr2BLinkingConfigEntity config : configEntities) {
			configmap.put(config.getGstin() + "|" + config.getTaxPeriod(),
					config);
		}
		List<Gstr2bLinkingDto> objs = new ArrayList<>();
		if (arrs != null && !arrs.isEmpty()) {
			for (Object[] arr : arrs) {
				Gstr2bLinkingDto obj = new Gstr2bLinkingDto();
				String gstin = (String) arr[0];
				obj.setGstin(gstin);
				String taxPeriod = arr[1] != null ? String.valueOf(arr[1])
						: null;
				obj.setTaxPeriod(taxPeriod);
				String linked = arr[2] != null ? String.valueOf(arr[2]) : null;
				obj.setLinked(linked);
				String notLinked = arr[3] != null ? String.valueOf(arr[3])
						: null;
				obj.setNotLinked(notLinked);
				String key = gstin + "|" + taxPeriod;
				Gstr2BLinkingConfigEntity configEntity = configmap.get(key);
				if (configEntity != null) {
					obj.setLastUpdatedOn(configEntity.getCompletedOn() != null
							? String.valueOf(configEntity.getCompletedOn())
							: null);
					obj.setLinkingStatus(configEntity.getStatus() != null
							? String.valueOf(configEntity.getStatus())
							: "Linking Not Initiated");
				} else {
					obj.setLinkingStatus("Linking Not Initiated");
				}
				objs.add(obj);
			}

		}
		return objs;
	}
}
