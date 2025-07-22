package com.ey.advisory.app.services.daos.gstr6a;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ey.advisory.app.docs.dto.GetAnx2DetailStatusReqDto;
import com.ey.advisory.app.docs.dto.GetGstr2aDetailStatusRespDto;
import com.ey.advisory.common.EYDateUtil;
/**
 * 
 * @author SriBhavya
 *
 */

@Component("GetGstr6aDetailStatusService")
public class GetGstr6aDetailStatusService {

	@Autowired
	@Qualifier("GetGstr6aDetailStatusFetchDaoImpl")
	private GetGstr6aDetailStatusFetchDao getGstr6aDetailStatusFetchDao;

	public List<GetGstr2aDetailStatusRespDto> findByCriteria(
			GetAnx2DetailStatusReqDto criteria) throws Exception {
		List<GetGstr2aDetailStatusRespDto> summaryList = new ArrayList<GetGstr2aDetailStatusRespDto>();
		List<Object[]> dataStatusList = getGstr6aDetailStatusFetchDao
				.findDataByCriteria(criteria);

		if (CollectionUtils.isNotEmpty(dataStatusList)) {
			summaryList.addAll(dataStatusList.parallelStream()
					.map(obj -> convert(obj)).collect(Collectors.toList()));

		}

		List<GetGstr2aDetailStatusRespDto> finalList = segregateTheGstinByStatusList(
				summaryList);
		return finalList;
	}

	private List<GetGstr2aDetailStatusRespDto> segregateTheGstinByStatusList(
			List<GetGstr2aDetailStatusRespDto> summaryList) {
		List<GetGstr2aDetailStatusRespDto> finalList = new ArrayList<GetGstr2aDetailStatusRespDto>();
		if (CollectionUtils.isNotEmpty(summaryList)) {
			Map<String, List<GetGstr2aDetailStatusRespDto>> gstinMap = summaryList
					.stream().collect(Collectors.groupingBy(e -> e.getGstin()));

			gstinMap.keySet().forEach(gstin -> {

				String b2bTimeStamp = " - ";
				String b2bStatus = " - ";
				String b2baTimeStamp = " - ";
				String b2baStatus = " - ";
				String cdnTimeStamp = " - ";
				String cdnStatus = " - ";
				String cdnaTimeStamp = " - ";
				String cdnaStatus = " - ";
				
				List<GetGstr2aDetailStatusRespDto> dtos = gstinMap.get(gstin);
				for (GetGstr2aDetailStatusRespDto dto : dtos) {
					if (!StringUtils.isEmpty(dto.getB2bStatus())) {
						b2bStatus = dto.getB2bStatus();
					}
					if (!StringUtils.isEmpty(dto.getB2bTimeStamp())) {
						b2bTimeStamp = dto.getB2bTimeStamp();
					}
					if (!StringUtils.isEmpty(dto.getB2baStatus())) {
						b2baStatus = dto.getB2baStatus();
					}
					if (!StringUtils.isEmpty(dto.getB2baTimeStamp())) {
						b2baTimeStamp = dto.getB2baTimeStamp();
					}
					if (!StringUtils.isEmpty(dto.getCdnStatus())) {

						cdnStatus = dto.getCdnStatus();
					}
					if (!StringUtils.isEmpty(dto.getCdnTimeStamp())) {
						cdnTimeStamp = dto.getCdnTimeStamp();
					}
					if (!StringUtils.isEmpty(dto.getCdnaStatus())) {

						cdnaStatus = dto.getCdnaStatus();
					}
					if (!StringUtils.isEmpty(dto.getCdnaTimeStamp())) {
						cdnaTimeStamp = dto.getCdnaTimeStamp();
					}				
				}
				GetGstr2aDetailStatusRespDto respDto = new GetGstr2aDetailStatusRespDto();
				respDto.setGstin(gstin);
				respDto.setB2bStatus(b2bStatus.toUpperCase());
				respDto.setB2bTimeStamp(b2bTimeStamp);
				respDto.setB2baStatus(b2baStatus.toUpperCase());
				respDto.setB2baTimeStamp(b2baTimeStamp);
				respDto.setCdnStatus(cdnStatus.toUpperCase());
				respDto.setCdnTimeStamp(cdnTimeStamp);
				respDto.setCdnaStatus(cdnaStatus.toUpperCase());
				respDto.setCdnaTimeStamp(cdnaTimeStamp);				
				finalList.add(respDto);
			});
		}
		return finalList;
	}

	private GetGstr2aDetailStatusRespDto convert(Object[] obj) {
		GetGstr2aDetailStatusRespDto dto = new GetGstr2aDetailStatusRespDto();
		dto.setGstin((String) obj[0]);
		dto.setB2bStatus((String) obj[2]);
		dto.setB2baStatus((String) obj[4]);
		dto.setCdnStatus((String) obj[6]);
		dto.setCdnaStatus((String) obj[8]);	

		if (obj[3] == null) {
			dto.setB2bTimeStamp(null);
		} else {
			Timestamp date = (Timestamp) obj[3];
			LocalDateTime dt = date.toLocalDateTime();
			LocalDateTime dateTimeFormatter = EYDateUtil
					.toISTDateTimeFromUTC(dt);
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("dd-MM-yyyy : HH:mm:ss");
			String newdate = FOMATTER.format(dateTimeFormatter);

			dto.setB2bTimeStamp(newdate);
		}
		if (obj[5] == null) {
			dto.setB2baTimeStamp(null);
		} else {
			Timestamp date = (Timestamp) obj[5];
			LocalDateTime dt = date.toLocalDateTime();
			LocalDateTime dateTimeFormatter = EYDateUtil
					.toISTDateTimeFromUTC(dt);
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("dd-MM-yyyy : HH:mm:ss");
			String newdate = FOMATTER.format(dateTimeFormatter);

			dto.setB2baTimeStamp(newdate);
		}
		if (obj[7] == null) {
			dto.setCdnTimeStamp(null);
		} else {
			Timestamp date = (Timestamp) obj[7];
			LocalDateTime dt = date.toLocalDateTime();
			LocalDateTime dateTimeFormatter = EYDateUtil
					.toISTDateTimeFromUTC(dt);
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("dd-MM-yyyy : HH:mm:ss");
			String newdate = FOMATTER.format(dateTimeFormatter);

			dto.setCdnTimeStamp(newdate);
		}
		if (obj[9] == null) {
			dto.setCdnaTimeStamp(null);
		} else {
			Timestamp date = (Timestamp) obj[9];
			LocalDateTime dt = date.toLocalDateTime();
			LocalDateTime dateTimeFormatter = EYDateUtil
					.toISTDateTimeFromUTC(dt);
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("dd-MM-yyyy : HH:mm:ss");
			String newdate = FOMATTER.format(dateTimeFormatter);

			dto.setCdnaTimeStamp(newdate);
		}	

		return dto;
	}
}
