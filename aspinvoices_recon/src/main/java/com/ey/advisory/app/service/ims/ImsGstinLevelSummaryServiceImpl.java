package com.ey.advisory.app.service.ims;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author ashutosh.kar
 *
 */

@Component("ImsGstinLevelSummaryServiceImpl")
@Slf4j
public class ImsGstinLevelSummaryServiceImpl implements ImsGstinLevelSummaryService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	private final List<String> GET_TYPES = Arrays.asList("B2B", "B2B - Amendment", "CDN", "CDN - Amendment", "DNA",
			"ISD", "ISD - Amendment", "ECOM", "ECOM - Amendment", "IMPG", "IMPGSEZ");

	private static final Map<String, String> TABLE_NAME_TRANSFORM_MAP = new HashMap<>();

	static {
		TABLE_NAME_TRANSFORM_MAP.put("Credit Note(ISD)", "Credit Note");
		TABLE_NAME_TRANSFORM_MAP.put("Credit Note Amendment(ISD)", "Credit Note Amendment");
	}

	private static final Map<String, List<String>> PARENT_CHILD_MAP = new LinkedHashMap<>();

	static {
		PARENT_CHILD_MAP.put("All Other ITC",
				Arrays.asList("B2B", "B2B Amendment", "ECOM", "ECOM Amendment", "CDN", "CDN Amendment"));
		PARENT_CHILD_MAP.put("CDN", Arrays.asList("Credit Note", "Debit Note"));
		PARENT_CHILD_MAP.put("CDN Amendment", Arrays.asList("Credit Note Amendment", "Debit Note Amendment"));
		PARENT_CHILD_MAP.put("Inward Supplies from ISD",
				Arrays.asList("Invoice", "Credit Note(ISD)", "Invoice Amendment", "Credit Note Amendment(ISD)"));
		PARENT_CHILD_MAP.put("Import of Goods",
				Arrays.asList("IMPG", "IMPGSEZ", "IMPG Amendment", "IMPGSEZ Amendment"));
	}

	@Override
	public ImsGstinSummaryResponseWrapperDto getImsSummaryGstinLvlData(ImsEntitySummaryReqDto criteria) {
		ImsGstinSummaryResponseWrapperDto responseWrapper = new ImsGstinSummaryResponseWrapperDto();
		List<ImsGstinSummaryResponseDto> responseList = new ArrayList<>();

		try {
			if (criteria.getGstin() == null || criteria.getGstin().isEmpty()) {
				LOGGER.error("GSTIN is null or empty");
				return new ImsGstinSummaryResponseWrapperDto();
			}

			String latestCallTimestamp = getLatestCallTimestamp(criteria.getGstin());

			responseWrapper.setGetCallTimeStamp(latestCallTimestamp != null ? latestCallTimestamp : "");

			// Call stored procedure
			StoredProcedureQuery storedProc = entityManager.createStoredProcedureQuery("USP_GETIMS_SUMMARY");
			storedProc.registerStoredProcedureParameter("IP_RECIPIENT_GSTIN", String.class, ParameterMode.IN);
			storedProc.registerStoredProcedureParameter("IP_TABLE_TYPE", String.class, ParameterMode.IN);

			// Get table types
			List<String> tableTypes = criteria.getTableType();
			List<String> sections = tableTypes.isEmpty() || tableTypes.contains("All") ? new ArrayList<>(GET_TYPES)
					: tableTypes.stream().filter(GET_TYPES::contains).collect(Collectors.toList());

			String tableTypesString = String.join(",", sections);
			storedProc.setParameter("IP_TABLE_TYPE", tableTypesString);
			storedProc.setParameter("IP_RECIPIENT_GSTIN", criteria.getGstin());

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = storedProc.getResultList();
			LOGGER.debug("Result list fetched from stored procedure: {}", resultList);

			if (resultList == null || resultList.isEmpty()) {
				LOGGER.error("No results found for the stored procedure.");
				return responseWrapper;
			}

			// Group results by table
			Map<String, List<Object[]>> groupedResults = resultList.stream()
					.collect(Collectors.groupingBy(row -> (String) row[0]));

			Set<String> processedTables = new HashSet<>();

			for (Map.Entry<String, List<String>> entry : PARENT_CHILD_MAP.entrySet()) {
				String parentTable = entry.getKey();
				List<String> childTables = entry.getValue();

				if ("CDN".equals(parentTable) || "CDN Amendment".equals(parentTable)) {
					continue; // Skip specific tables
				}

				// Check if the parent table has already been processed
				if (processedTables.contains(parentTable)) {
					continue;
				}

				// Map parent row and its child items
				ImsGstinSummaryResponseDto parentDto = mapRowToDto(groupedResults.get(parentTable), parentTable);

				if (parentDto != null) {
					List<ImsGstinSummaryResponseDto> childItems = mapChildItems(parentTable, groupedResults);

					if (childItems != null && !childItems.isEmpty()) {
						parentDto.setItems(childItems);
					}
					parentDto.setRowStyle("Bold");
					if ("CDN".equals(parentTable) || "CDN Amendment".equals(parentTable)) {
						parentDto.setRowStyle("Bold");
					}
					responseList.add(parentDto);
				}
			}

		} catch (Exception ex) {
			throw new AppException("Error occurred while fetching IMS Gstin Level Summary", ex);
		}

		responseWrapper.setTables(responseList);

		return responseWrapper;
	}

	private String getLatestCallTimestamp(String gstin) {
		try {
			String queryStr = "SELECT CREATED_ON FROM GETANX1_BATCH_TABLE  WHERE GSTIN =:gstin"
					+ " AND RETURN_PERIOD ='000000' AND API_SECTION = 'IMS_COUNT'"
					+ " AND GET_TYPE='ALL_OTH' AND IS_DELETE = false";

			Query query = entityManager.createNativeQuery(queryStr);

			query.setParameter("gstin", gstin);

			Object item = query.getSingleResult();

			return item != null ? formatDateTime((Timestamp) item) : null;

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			throw new AppException("Error occured during executing the Query for TimeStamp", e);
		}
	}

	private ImsGstinSummaryResponseDto mapRowToDto(List<Object[]> rows, String tableName) {
		try {
			if (rows == null || rows.isEmpty()) {
				return null;
			}

			Object[] row = rows.get(0);

			ImsGstinSummaryResponseDto dto = new ImsGstinSummaryResponseDto();
			dto.setTable(TABLE_NAME_TRANSFORM_MAP.getOrDefault(tableName, tableName));
			dto.setGstnTotal(GenUtil.getBigInteger(row[1]));
			dto.setGstnNoAction(GenUtil.getBigInteger(row[2]));
			dto.setGstnAccepted(GenUtil.getBigInteger(row[3]));
			dto.setGstnRejected(GenUtil.getBigInteger(row[4]));
			dto.setGstnPendingTotal(GenUtil.getBigInteger(row[5]));
			if (shouldNotIncludeAspFields(tableName)) {
				dto.setAspTotal(GenUtil.getBigInteger(row[6]));
				dto.setAspNoAction((Integer) row[7]);
				dto.setAspAccepted((Integer) row[8]);
				dto.setAspRejected((Integer) row[9]);
				dto.setAspPendingTotal((Integer) row[10]);
			} else {
				dto.setAspTotal(null);
				dto.setAspNoAction(null);
				dto.setAspAccepted(null);
				dto.setAspRejected(null);
				dto.setAspPendingTotal(null);
			}

			if ("CDN".equals(tableName) || "CDN Amendment".equals(tableName)) {
				dto.setRowStyle("Bold");
			}

			return dto;

		} catch (Exception ex) {
			LOGGER.error("Error occurred while mapping row to DTO for table: {}", tableName, ex);
			return null;
		}
	}

	private boolean shouldNotIncludeAspFields(String tableName) {
		List<String> excludedTables = Arrays.asList("Invoice", "Credit Note(ISD)", "Invoice Amendment",
				"Credit Note Amendment(ISD)", "IMPG", "IMPGSEZ", "IMPG Amendment", "IMPGSEZ Amendment",
				"Inward Supplies from ISD","Import of Goods");
		return !excludedTables.contains(tableName);
	}

	private List<ImsGstinSummaryResponseDto> mapChildItems(String parentTable,
			Map<String, List<Object[]>> groupedResults) {
		try {
			// Find child tables for the current parent
			List<String> childTables = PARENT_CHILD_MAP.getOrDefault(parentTable, Collections.emptyList());
			List<ImsGstinSummaryResponseDto> childDtos = new ArrayList<>();

			for (String childTable : childTables) {
				ImsGstinSummaryResponseDto childDto = mapRowToDto(groupedResults.get(childTable), childTable);
				if (childDto != null) {
					List<ImsGstinSummaryResponseDto> nestedChildren = mapChildItems(childTable, groupedResults);
					if (nestedChildren != null && !nestedChildren.isEmpty()) {
						childDto.setItems(nestedChildren);
					} else {
						childDto.setItems(null);
					}
					childDtos.add(childDto);
				}
			}

			return childDtos.isEmpty() ? null : childDtos;

		} catch (Exception ex) {
			LOGGER.error("Error occurred while mapping child items for parent table: {}", parentTable, ex);
			return Collections.emptyList();
		}
	}

	private String formatDateTime(Timestamp timestamp) {
		if (timestamp == null)
			return null;
		LocalDateTime dateTimeInIst = EYDateUtil.toISTDateTimeFromUTC(timestamp);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		return dateTimeInIst.format(formatter);
	}
}
