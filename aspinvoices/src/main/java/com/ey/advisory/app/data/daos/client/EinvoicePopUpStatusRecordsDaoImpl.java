package com.ey.advisory.app.data.daos.client;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.gstr1.Gstr1PopScreenRecordsFinalResponseDto;
import com.ey.advisory.app.services.gstr1.Gstr1PopScreenRecordsRequestDto;
import com.ey.advisory.app.services.gstr1.Gstr1PopScreenRecordsResponseDto;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Slf4j
@Component("EinvoicePopUpStatusRecordsDaoImpl")
public class EinvoicePopUpStatusRecordsDaoImpl implements Gstr1PopUpRecordsDao {


	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public Gstr1PopScreenRecordsFinalResponseDto fetchProcessStatusData(
			Gstr1PopScreenRecordsRequestDto reqDto, List<String> dbGstins) {

		String taxPeriod1 = reqDto.getTaxPeriodFrom();
		String taxPeriod2 = reqDto.getTaxPeriodTo();
		
		int taxPeriodFrom = GenUtil.convertTaxPeriodToInt(taxPeriod1);
		int taxPeriodTo = GenUtil.convertTaxPeriodToInt(taxPeriod2);
		
		List<String> gstinList = Lists.newArrayList();
		List<String> reqGstins = reqDto.getGstins();
		if (CollectionUtils.isNotEmpty(reqGstins)) {
			gstinList.addAll(reqGstins);
		} else {
			gstinList.addAll(dbGstins);
		}

		Set<String> gstinSet = Sets.newHashSet(gstinList);

		StringBuffer lastCallQueryStr = createLastCallQueryString(gstinSet,
				taxPeriodFrom,taxPeriodTo);
		StringBuffer lastSuccessQueryStr = createLastSuccessQueryString(
				gstinSet, taxPeriodFrom,taxPeriodTo);

		Query lastCallQuery = entityManager
				.createNativeQuery(lastCallQueryStr.toString());
		Query lastSuccessQuery = entityManager
				.createNativeQuery(lastSuccessQueryStr.toString());
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug("lastCallQuery -->" + lastCallQuery);
		LOGGER.debug("lastSuccessQuery -->" + lastSuccessQuery);
		}

		if (taxPeriodFrom != 0 && taxPeriodTo !=  0) {
			lastCallQuery.setParameter("taxPeriodfrom", taxPeriodFrom);
			lastCallQuery.setParameter("taxPeriodTo", taxPeriodTo);
			
			lastSuccessQuery.setParameter("taxPeriodfrom", taxPeriodFrom);
			lastSuccessQuery.setParameter("taxPeriodTo", taxPeriodTo);
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
		List<Gstr1PopScreenRecordsResponseDto> listResponse = new ArrayList<>();
		
		if (CollectionUtils.isNotEmpty(lastCallList)) {
			Map<String, List<Gstr1PopScreenRecordsResponseDto>> gstinMap = lastCallList
					.stream().collect(Collectors.groupingBy(
							Gstr1PopScreenRecordsResponseDto::getGstin));

			
			gstinMap.keySet().forEach(gstin -> {
				List<Gstr1PopScreenRecordsResponseDto> list = gstinMap
						.get(gstin);
				
				Map<String, List<Gstr1PopScreenRecordsResponseDto>> returnPeriodMap = list
						.stream().collect(Collectors.groupingBy(
								Gstr1PopScreenRecordsResponseDto::getTaxPeriod));
				
				
				returnPeriodMap.keySet().forEach(returnPeriod -> {
					List<Gstr1PopScreenRecordsResponseDto> returnPeriodList = returnPeriodMap
							.get(returnPeriod);
					Gstr1PopScreenRecordsResponseDto filterDto = new Gstr1PopScreenRecordsResponseDto();
					
				String b2bTimeStamp = "";
				String b2bStatus = "";
				String cdnrTimeStamp = "";
				String cdnrStatus = "";
				String cdnurTimeStamp = "";
				String cdnurStatus = "";
				String exportsTimestamp = "";
				String exportStatus = "";
				String taxPeriod = "";
				
				
				for (Gstr1PopScreenRecordsResponseDto dto : returnPeriodList) {
					
					
					taxPeriod = StringUtils.isEmpty(taxPeriod)
							? dto.getTaxPeriod() : taxPeriod;
					b2bStatus = StringUtils.isEmpty(b2bStatus)
							? dto.getB2bStatus() : b2bStatus;
					b2bTimeStamp = StringUtils.isEmpty(b2bTimeStamp)
							? dto.getB2bTimeStamp() : b2bTimeStamp;
					cdnrStatus = StringUtils.isEmpty(cdnrStatus)
							? dto.getCdnrStatus() : cdnrStatus;
					cdnrTimeStamp = StringUtils.isEmpty(cdnrTimeStamp)
							? dto.getCdnrTimeStamp() : cdnrTimeStamp;

					cdnurStatus = StringUtils.isEmpty(cdnurStatus)
							? dto.getCdnurStatus() : cdnurStatus;
					cdnurTimeStamp = StringUtils.isEmpty(cdnurTimeStamp)
							? dto.getCdnurTimeStamp() : cdnurTimeStamp;

					exportStatus = StringUtils.isEmpty(exportStatus)
							? dto.getExportStatus() : exportStatus;
					exportsTimestamp = StringUtils.isEmpty(exportsTimestamp)
							? dto.getExportsTimestamp() : exportsTimestamp;

				
							filterDto.setTaxPeriod(taxPeriod);
							filterDto.setGstin(gstin);
							filterDto.setB2bStatus(b2bStatus);
							filterDto.setB2bTimeStamp(b2bTimeStamp);
							filterDto.setCdnrStatus(cdnrStatus);
							filterDto.setCdnrTimeStamp(cdnrTimeStamp);
							filterDto.setCdnurStatus(cdnurStatus);
							filterDto.setCdnurTimeStamp(cdnurTimeStamp);
							filterDto.setExportStatus(exportStatus);
							filterDto.setExportsTimestamp(exportsTimestamp);

							filterList.add(filterDto);
							
				}
				listResponse.add(filterList.get(filterList.size()-1));
			
			});
				
			
			});
		
		}
		return listResponse ;
	}

	private List<Gstr1PopScreenRecordsResponseDto> convertLastCallObjectArrayToList(
			List<Object[]> queryList) {
		List<Gstr1PopScreenRecordsResponseDto> dtos = Lists.newArrayList();
		queryList.forEach(obj -> {
			Gstr1PopScreenRecordsResponseDto dto = new Gstr1PopScreenRecordsResponseDto();
			dto.setGstin((String) obj[0]);
			dto.setTaxPeriod((String) obj[1]);
			String getType = (String) obj[3];
			if (StringUtils.isNotBlank(getType)) {
				switch (getType) {
				case "B2B": {
					dto.setB2bStatus((String) obj[5]);

					if (obj[4] != null) {
						/*
						 * Timestamp timeStamp = (Timestamp) obj[4];
						 * LocalDateTime localDT = timeStamp.toLocalDateTime();
						 * LocalDateTime convertref = EYDateUtil
						 * .toISTDateTimeFromUTC(localDT);
						 * dto.setB2bTimeStamp(convertref.toString());
						 */

						Timestamp date = (Timestamp) obj[4];
						LocalDateTime dt = date.toLocalDateTime();
						LocalDateTime dateTimeFormatter = EYDateUtil
								.toISTDateTimeFromUTC(dt);
						DateTimeFormatter FOMATTER = DateTimeFormatter
								.ofPattern("dd-MM-yyyy : HH:mm:ss");
						String newdate = FOMATTER.format(dateTimeFormatter);

						dto.setB2bTimeStamp(newdate);
					} else {
						dto.setB2bTimeStamp(null);
					}
					break;
				}
				case "CDNR": {
					dto.setCdnrStatus((String) obj[5]);

					if (obj[4] != null) {
						Timestamp date = (Timestamp) obj[4];
						LocalDateTime dt = date.toLocalDateTime();
						LocalDateTime dateTimeFormatter = EYDateUtil
								.toISTDateTimeFromUTC(dt);
						DateTimeFormatter FOMATTER = DateTimeFormatter
								.ofPattern("dd-MM-yyyy : HH:mm:ss");
						String newdate = FOMATTER.format(dateTimeFormatter);

						dto.setCdnrTimeStamp(newdate);
					} else {
						dto.setCdnrTimeStamp(null);
					}
					break;
				}
				case "CDNUR": {
					dto.setCdnurStatus((String) obj[5]);

					if (obj[4] != null) {
						Timestamp date = (Timestamp) obj[4];
						LocalDateTime dt = date.toLocalDateTime();
						LocalDateTime dateTimeFormatter = EYDateUtil
								.toISTDateTimeFromUTC(dt);
						DateTimeFormatter FOMATTER = DateTimeFormatter
								.ofPattern("dd-MM-yyyy : HH:mm:ss");
						String newdate = FOMATTER.format(dateTimeFormatter);

						dto.setCdnurTimeStamp(newdate);
					} else {
						dto.setCdnurTimeStamp(null);
					}
					break;
				}
				case "EXP": {
					dto.setExportStatus((String) obj[5]);

					if (obj[4] != null) {
						Timestamp date = (Timestamp) obj[4];
						LocalDateTime dt = date.toLocalDateTime();
						LocalDateTime dateTimeFormatter = EYDateUtil
								.toISTDateTimeFromUTC(dt);
						DateTimeFormatter FOMATTER = DateTimeFormatter
								.ofPattern("dd-MM-yyyy : HH:mm:ss");
						String newdate = FOMATTER.format(dateTimeFormatter);

						dto.setExportsTimestamp(newdate);
					} else {
						dto.setExportsTimestamp(null);
					}
					break;
				}
				}
			}
			dtos.add(dto);
		});

		return dtos;
	}

	private StringBuffer createLastCallQueryString(Set<String> gstinSet,
			int taxPeriodFrom,int taxPeriodTo) {

		StringBuilder queryBuilder = new StringBuilder();

		if (taxPeriodFrom != 0 && taxPeriodTo != 0) {
			queryBuilder.append(" AND DERIVED_RET_PERIOD  BETWEEN :taxPeriodfrom AND :taxPeriodTo ");

		}
		if (CollectionUtils.isNotEmpty(gstinSet)) {
			queryBuilder.append(" AND GSTIN IN (:gstinsList) ");
		}

		String condition = queryBuilder.toString();
		StringBuffer bufferString = new StringBuffer();
		bufferString
				.append("SELECT GSTIN, RETURN_PERIOD, NUM, GET_TYPE, START_TIME, STATUS "
						+ "FROM (SELECT GSTIN,RETURN_PERIOD,DENSE_RANK() OVER(PARTITION BY GSTIN,GET_TYPE,RETURN_PERIOD ORDER BY START_TIME DESC) AS NUM, "
						+ "GET_TYPE, START_TIME, STATUS FROM GETANX1_BATCH_TABLE GBT WHERE "
						+ "GET_TYPE IN ('B2B','CDNR','CDNUR','EXP') AND API_SECTION = 'GSTR1_EINV' ");
		if (StringUtils.isNotBlank(condition)) {
			bufferString.append(condition);
		}
		bufferString.append(
				") A WHERE NUM = 1 ORDER BY GET_TYPE, START_TIME DESC ");

		return bufferString;

	}

	private StringBuffer createLastSuccessQueryString(Set<String> gstinSet,
			int taxPeriodFrom,int taxPeriodTo) {

		StringBuilder queryBuilder = new StringBuilder();

		if (taxPeriodFrom != 0 && taxPeriodTo != 0){
			queryBuilder.append(" AND DERIVED_RET_PERIOD BETWEEN  :taxPeriodfrom AND :taxPeriodTo ");

		}
		if (CollectionUtils.isNotEmpty(gstinSet)) {
			queryBuilder.append(" AND GSTIN IN (:gstinsList) ");
		}

		String condition = queryBuilder.toString();
		StringBuffer bufferString = new StringBuffer();

		bufferString
				.append("SELECT GSTIN, RETURN_PERIOD, NUM, GET_TYPE, START_TIME, STATUS "
						+ "FROM ( SELECT GSTIN,RETURN_PERIOD, DENSE_RANK() OVER(PARTITION BY GSTIN,GET_TYPE,RETURN_PERIOD ORDER BY START_TIME DESC) AS NUM, "
						+ "GET_TYPE,START_TIME, STATUS FROM GETANX1_BATCH_TABLE GBT WHERE STATUS = 'SUCCESS' "
						+ "AND GET_TYPE IN ('B2B','CDNR','CDNUR','EXP') AND API_SECTION = 'GSTR1_EINV' ");
		if (StringUtils.isNotBlank(condition)) {
			bufferString.append(condition);
		}
		bufferString
				.append(") A WHERE NUM = 1 ORDER BY GET_TYPE, START_TIME DESC");

		return bufferString;

	}

}
