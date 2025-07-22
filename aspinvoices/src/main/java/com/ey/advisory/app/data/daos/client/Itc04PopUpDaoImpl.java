package com.ey.advisory.app.data.daos.client;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.gstr1.Gstr1PopScreenRecordsFinalResponseDto;
import com.ey.advisory.app.services.gstr1.Gstr1PopScreenRecordsRequestDto;
import com.ey.advisory.app.services.gstr1.Gstr1PopScreenRecordsResponseDto;
import com.ey.advisory.common.EYDateUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * 
 * @author Anand3.M
 *
 */
@Component("Itc04PopUpDaoImpl")
public class Itc04PopUpDaoImpl implements Gstr1PopUpRecordsDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Itc04PopUpDaoImpl.class);
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public Gstr1PopScreenRecordsFinalResponseDto fetchProcessStatusData(
			Gstr1PopScreenRecordsRequestDto reqDto, List<String> dbGstins) {

		String taxPeriod = reqDto.getTaxPeriod();
		List<String> gstinList = Lists.newArrayList();
		List<String> reqGstins = reqDto.getGstins();
		if (CollectionUtils.isNotEmpty(reqGstins)) {
			gstinList.addAll(reqGstins);
		} else {
			gstinList.addAll(dbGstins);
		}

		Set<String> gstinSet = Sets.newHashSet(gstinList);

		StringBuffer lastCallQueryStr = createLastCallQueryString(gstinSet,
				taxPeriod);
		StringBuffer lastSuccessQueryStr = createLastSuccessQueryString(
				gstinSet, taxPeriod);

		Query lastCallQuery = entityManager
				.createNativeQuery(lastCallQueryStr.toString());
		Query lastSuccessQuery = entityManager
				.createNativeQuery(lastSuccessQueryStr.toString());
		LOGGER.debug("lastCallQuery -->" + lastCallQuery);
		LOGGER.debug("lastSuccessQuery -->" + lastSuccessQuery);

		if (taxPeriod != null) {
			lastCallQuery.setParameter("taxPeriod", taxPeriod);
			lastSuccessQuery.setParameter("taxPeriod", taxPeriod);
		}

		if (CollectionUtils.isNotEmpty(gstinSet)) {
			lastCallQuery.setParameter("gstinsList", gstinSet);
			lastSuccessQuery.setParameter("gstinsList", gstinSet);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> lastCallQueryList = lastCallQuery.getResultList();
		@SuppressWarnings("unchecked")
		List<Object[]> lastSuccessQueryList = lastSuccessQuery.getResultList();

		List<Gstr1PopScreenRecordsResponseDto> lastCallList = convertLastCallObjectArrayToList(
				lastCallQueryList);
		List<Gstr1PopScreenRecordsResponseDto> lastSuccessList = convertLastCallObjectArrayToList(
				lastSuccessQueryList);
		List<Gstr1PopScreenRecordsResponseDto> lastCallFilterList = filterDataByGstin(
				lastCallList);
		List<Gstr1PopScreenRecordsResponseDto> lastCallSuccessList = filterDataByGstin(
				lastSuccessList);
		Gstr1PopScreenRecordsFinalResponseDto finalResponseDto = new Gstr1PopScreenRecordsFinalResponseDto();
		finalResponseDto.setLastCall(lastCallFilterList);
		finalResponseDto.setLastSuccess(lastCallSuccessList);

		return finalResponseDto;

	}

	private List<Gstr1PopScreenRecordsResponseDto> filterDataByGstin(
			List<Gstr1PopScreenRecordsResponseDto> lastCallList) {
		List<Gstr1PopScreenRecordsResponseDto> filterList = Lists
				.newArrayList();
		if (CollectionUtils.isNotEmpty(lastCallList)) {
			Map<String, List<Gstr1PopScreenRecordsResponseDto>> gstinMap = lastCallList
					.stream().collect(Collectors.groupingBy(
							Gstr1PopScreenRecordsResponseDto::getGstin));

			gstinMap.keySet().forEach(gstin -> {
				List<Gstr1PopScreenRecordsResponseDto> list = gstinMap
						.get(gstin);
				Gstr1PopScreenRecordsResponseDto filterDto = new Gstr1PopScreenRecordsResponseDto();
				String table4TimeStamp = "";
				String table4Status = "";
				String table5ATimeStamp = "";
				String table5AStatus = "";
				String table5BTimeStamp = "";
				String table5BStatus = "";
				String table5CTimeStamp = "";
				String table5CStatus = "";
				for (Gstr1PopScreenRecordsResponseDto dto : list) {
					table4Status = StringUtils.isEmpty(table4Status)
							? dto.getTable4Status() : table4Status;
					table4TimeStamp = StringUtils.isEmpty(table4TimeStamp)
							? dto.getTable4TimeStamp() : table4TimeStamp;
					table5AStatus = StringUtils.isEmpty(table5AStatus)
							? dto.getTable5AStatus() : table5AStatus;
					table5ATimeStamp = StringUtils.isEmpty(table5ATimeStamp)
							? dto.getTable5ATimeStamp() : table5ATimeStamp;

					table5BStatus = StringUtils.isEmpty(table5BStatus)
							? dto.getTable5BStatus() : table5BStatus;
					table5BTimeStamp = StringUtils.isEmpty(table5BTimeStamp)
							? dto.getTable5BTimeStamp() : table5BTimeStamp;

					table5CStatus = StringUtils.isEmpty(table5CStatus)
							? dto.getTable5CStatus() : table5CStatus;
					table5CTimeStamp = StringUtils.isEmpty(table5CTimeStamp)
							? dto.getTable5CTimeStamp() : table5CTimeStamp;

				}

				filterDto.setGstin(gstin);
				filterDto.setTable4Status(table4Status);
				filterDto.setTable4TimeStamp(table4TimeStamp);
				filterDto.setTable5AStatus(table5AStatus);
				filterDto.setTable5ATimeStamp(table5ATimeStamp);
				filterDto.setTable5BStatus(table5BStatus);
				filterDto.setTable5BTimeStamp(table5BTimeStamp);
				filterDto.setTable5CStatus(table5CStatus);
				filterDto.setTable5CTimeStamp(table5CTimeStamp);

				filterList.add(filterDto);
			});

		}
		return filterList;
	}

	private List<Gstr1PopScreenRecordsResponseDto> convertLastCallObjectArrayToList(
			List<Object[]> queryList) {
		List<Gstr1PopScreenRecordsResponseDto> dtos = Lists.newArrayList();
		queryList.forEach(obj -> {
			Gstr1PopScreenRecordsResponseDto dto = new Gstr1PopScreenRecordsResponseDto();
			dto.setGstin((String) obj[0]);
			dto.setTable4Status((String) obj[5]);
			if (obj[4] != null) {
				Timestamp date = (Timestamp) obj[4];
				LocalDateTime dt = date.toLocalDateTime();
				LocalDateTime dateTimeFormatter = EYDateUtil
						.toISTDateTimeFromUTC(dt);
				DateTimeFormatter FOMATTER = DateTimeFormatter
						.ofPattern("dd-MM-yyyy : HH:mm:ss");
				String newdate = FOMATTER.format(dateTimeFormatter);

				dto.setTable4TimeStamp(newdate);
			} else {
				dto.setTable4TimeStamp(null);
			}
			dtos.add(dto);
		});

		queryList.forEach(obj -> {
			Gstr1PopScreenRecordsResponseDto dto = new Gstr1PopScreenRecordsResponseDto();
			dto.setGstin((String) obj[0]);
			dto.setTable5AStatus((String) obj[5]);
			if (obj[4] != null) {
				Timestamp date = (Timestamp) obj[4];
				LocalDateTime dt = date.toLocalDateTime();
				LocalDateTime dateTimeFormatter = EYDateUtil
						.toISTDateTimeFromUTC(dt);
				DateTimeFormatter FOMATTER = DateTimeFormatter
						.ofPattern("dd-MM-yyyy : HH:mm:ss");
				String newdate = FOMATTER.format(dateTimeFormatter);

				dto.setTable5ATimeStamp(newdate);
			} else {
				dto.setTable5ATimeStamp(null);
			}
			dtos.add(dto);
		});

		queryList.forEach(obj -> {
			Gstr1PopScreenRecordsResponseDto dto = new Gstr1PopScreenRecordsResponseDto();
			dto.setGstin((String) obj[0]);
			dto.setTable5BStatus((String) obj[5]);
			if (obj[4] != null) {
				Timestamp date = (Timestamp) obj[4];
				LocalDateTime dt = date.toLocalDateTime();
				LocalDateTime dateTimeFormatter = EYDateUtil
						.toISTDateTimeFromUTC(dt);
				DateTimeFormatter FOMATTER = DateTimeFormatter
						.ofPattern("dd-MM-yyyy : HH:mm:ss");
				String newdate = FOMATTER.format(dateTimeFormatter);

				dto.setTable5BTimeStamp(newdate);
			} else {
				dto.setTable5BTimeStamp(null);
			}
			dtos.add(dto);
		});

		queryList.forEach(obj -> {
			Gstr1PopScreenRecordsResponseDto dto = new Gstr1PopScreenRecordsResponseDto();
			dto.setGstin((String) obj[0]);
			dto.setTable5CStatus((String) obj[5]);
			if (obj[4] != null) {
				Timestamp date = (Timestamp) obj[4];
				LocalDateTime dt = date.toLocalDateTime();
				LocalDateTime dateTimeFormatter = EYDateUtil
						.toISTDateTimeFromUTC(dt);
				DateTimeFormatter FOMATTER = DateTimeFormatter
						.ofPattern("dd-MM-yyyy : HH:mm:ss");
				String newdate = FOMATTER.format(dateTimeFormatter);

				dto.setTable5CTimeStamp(newdate);
			} else {
				dto.setTable5CTimeStamp(null);
			}
			dtos.add(dto);
		});

		return dtos;
	}

	private StringBuffer createLastCallQueryString(Set<String> gstinSet,
			String taxPeriod) {

		StringBuilder queryBuilder = new StringBuilder();

		if (StringUtils.isNotBlank(taxPeriod)) {
			queryBuilder.append(" AND RETURN_PERIOD = :taxPeriod ");

		}
		if (CollectionUtils.isNotEmpty(gstinSet)) {
			queryBuilder.append(" AND GSTIN IN (:gstinsList) ");
		}

		String condition = queryBuilder.toString();
		StringBuffer bufferString = new StringBuffer();
		bufferString
				.append("SELECT GSTIN, RETURN_PERIOD, NUM, GET_TYPE, START_TIME, STATUS "
						+ "FROM (SELECT GSTIN,RETURN_PERIOD,DENSE_RANK() OVER(PARTITION BY GSTIN,GET_TYPE ORDER BY START_TIME DESC) AS NUM, "
						+ "GET_TYPE, START_TIME, STATUS FROM GETANX1_BATCH_TABLE GBT WHERE API_SECTION='ITC04' "
						+ "AND GET_TYPE IN ('GET') ");
		if (StringUtils.isNotBlank(condition)) {
			bufferString.append(condition);
		}
		bufferString.append(
				") A WHERE NUM = 1 ORDER BY GET_TYPE, START_TIME DESC ");

		return bufferString;

	}

	private StringBuffer createLastSuccessQueryString(Set<String> gstinSet,
			String taxPeriod) {

		StringBuilder queryBuilder = new StringBuilder();

		if (StringUtils.isNotBlank(taxPeriod)) {
			queryBuilder.append(" AND RETURN_PERIOD = :taxPeriod ");

		}
		if (CollectionUtils.isNotEmpty(gstinSet)) {
			queryBuilder.append(" AND GSTIN IN (:gstinsList) ");
		}

		String condition = queryBuilder.toString();
		StringBuffer bufferString = new StringBuffer();

		bufferString
				.append("SELECT GSTIN, RETURN_PERIOD, NUM, GET_TYPE, START_TIME, STATUS "
						+ "FROM ( SELECT GSTIN,RETURN_PERIOD, DENSE_RANK() OVER(PARTITION BY GSTIN,GET_TYPE ORDER BY START_TIME DESC) AS NUM, "
						+ "GET_TYPE,START_TIME, STATUS FROM GETANX1_BATCH_TABLE GBT WHERE STATUS = 'SUCCESS' AND API_SECTION='ITC04' "
						+ "AND GET_TYPE IN ('GET') ");
		if (StringUtils.isNotBlank(condition)) {
			bufferString.append(condition);
		}
		bufferString
				.append(") A WHERE NUM = 1 ORDER BY GET_TYPE, START_TIME DESC");

		return bufferString;

	}

}
