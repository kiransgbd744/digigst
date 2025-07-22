package com.ey.advisory.app.services.daos.get2a;

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

import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.docs.dto.GetAnx2DetailStatusReqDto;
import com.ey.advisory.app.docs.dto.GetGstr2aDetailStatusRespDto;
import com.ey.advisory.common.EYDateUtil;

@Component("GetGstr2aFailedStatusService")
public class GetGstr2aFailedStatusService {

	@Autowired
	@Qualifier("GetGstr2aFailedStatusFetchDaoImpl")
	private GetGstr2aFailedStatusFetchDao getGstr2aFailedStatusFetchDao;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository anx1BatchRepository;

	public List<GetGstr2aDetailStatusRespDto> findByCriteria(
			GetAnx2DetailStatusReqDto criteria) throws Exception {
		List<String> gstnList = criteria.getGstin();
		String taxPeriod = criteria.getTaxPeriod();
		List<GetGstr2aDetailStatusRespDto> summaryList = new ArrayList<GetGstr2aDetailStatusRespDto>();
		List<Object[]> dataStatusList = getGstr2aFailedStatusFetchDao
				.findDataByCriteria(criteria);

		if (CollectionUtils.isNotEmpty(dataStatusList)) {
			summaryList.addAll(dataStatusList.parallelStream()
					.map(obj -> convert(obj)).collect(Collectors.toList()));

		}
		fillTheDefaultData(summaryList, gstnList, taxPeriod);
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
				String isdTimeStamp = " - ";
				String isdStatus = " - ";
				String isdaTimeStamp = " - ";
				String isdaStatus = " - ";
				String amdhistTimeStamp = " - ";
				String amdhistStatus = " - ";
				String impgTimeStamp = " - ";
				String impgStatus = " - ";
				String impgsezTimeStamp = " - ";
				String impgsezStatus = " - ";

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
					if (!StringUtils.isEmpty(dto.getIsdStatus())) {

						isdStatus = dto.getIsdStatus();
					}

					if (!StringUtils.isEmpty(dto.getIsdTimeStamp())) {
						isdTimeStamp = dto.getIsdTimeStamp();
					}
					if (!StringUtils.isEmpty(dto.getIsdaStatus())) {

						isdaStatus = dto.getIsdaStatus();
					}

					if (!StringUtils.isEmpty(dto.getIsdaTimeStamp())) {
						isdaTimeStamp = dto.getIsdaTimeStamp();
					}

					if (!StringUtils.isEmpty(dto.getAmdhistStatus())) {

						isdaStatus = dto.getAmdhistStatus();
					}

					if (!StringUtils.isEmpty(dto.getAmdhistTimeStamp())) {
						isdaTimeStamp = dto.getAmdhistTimeStamp();
					}
					if (!StringUtils.isEmpty(dto.getImpgStatus())) {

						isdaStatus = dto.getImpgStatus();
					}

					if (!StringUtils.isEmpty(dto.getImpgTimeStamp())) {
						isdaTimeStamp = dto.getImpgTimeStamp();
					}
					if (!StringUtils.isEmpty(dto.getImpgSezStatus())) {

						isdaStatus = dto.getImpgSezStatus();
					}

					if (!StringUtils.isEmpty(dto.getImpgSezTimeStamp())) {
						isdaTimeStamp = dto.getImpgSezTimeStamp();
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
				respDto.setIsdStatus(isdStatus.toUpperCase());
				respDto.setIsdTimeStamp(isdTimeStamp);
				respDto.setIsdaStatus(isdaStatus.toUpperCase());
				respDto.setIsdaTimeStamp(isdaTimeStamp);
				respDto.setAmdhistStatus(amdhistStatus.toUpperCase());
				respDto.setAmdhistTimeStamp(amdhistTimeStamp);
				respDto.setImpgStatus(impgStatus.toUpperCase());
				respDto.setImpgTimeStamp(impgTimeStamp);
				respDto.setImpgSezStatus(impgsezStatus.toUpperCase());
				respDto.setImpgSezTimeStamp(impgsezTimeStamp);
				finalList.add(respDto);
			});
		}
		return finalList;
	}

	private void fillTheDefaultData(
			List<GetGstr2aDetailStatusRespDto> summaryList,
			List<String> gstnList, String taxPeriod) {
		List<String> dataGstinList = new ArrayList<>();
		summaryList.forEach(dto -> dataGstinList.add(dto.getGstin()));
		for (String gstin : gstnList) {
			int count = anx1BatchRepository.gstinCount(gstin, taxPeriod);
			if (count == 0 || !dataGstinList.contains(gstin)) {
				GetGstr2aDetailStatusRespDto dto = new GetGstr2aDetailStatusRespDto();
				dto.setGstin(gstin);
				dto.setB2bStatus("-");
				dto.setB2bTimeStamp("-");
				dto.setB2baStatus("-");
				dto.setB2baTimeStamp("-");
				dto.setCdnStatus("-");
				dto.setCdnTimeStamp("-");
				dto.setCdnaStatus("-");
				dto.setCdnaTimeStamp("-");
				dto.setIsdStatus("-");
				dto.setIsdTimeStamp("-");
				dto.setIsdaStatus("-");
				dto.setIsdaTimeStamp("-");
				dto.setAmdhistStatus("-");
				dto.setAmdhistTimeStamp("-");
				dto.setImpgStatus("-");
				dto.setImpgTimeStamp("-");
				dto.setImpgSezStatus("-");
				dto.setImpgSezTimeStamp("-");
				summaryList.add(dto);
			}

		}
	}

	private GetGstr2aDetailStatusRespDto convert(Object[] obj) {
		GetGstr2aDetailStatusRespDto dto = new GetGstr2aDetailStatusRespDto();
		dto.setGstin((String) obj[0]);
		dto.setB2bStatus((String) obj[2]);
		dto.setB2baStatus((String) obj[4]);
		dto.setCdnStatus((String) obj[6]);
		dto.setCdnaStatus((String) obj[8]);
		dto.setIsdStatus((String) obj[10]);
		dto.setIsdaStatus((String) obj[12]);
		dto.setAmdhistStatus((String) obj[14]);
		dto.setImpgStatus((String) obj[16]);
		dto.setImpgSezStatus((String) obj[18]);

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
		if (obj[11] == null) {
			dto.setIsdTimeStamp(null);
		} else {
			Timestamp date = (Timestamp) obj[11];
			LocalDateTime dt = date.toLocalDateTime();
			LocalDateTime dateTimeFormatter = EYDateUtil
					.toISTDateTimeFromUTC(dt);
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("dd-MM-yyyy : HH:mm:ss");
			String newdate = FOMATTER.format(dateTimeFormatter);
			dto.setIsdTimeStamp(newdate);
		}
		if (obj[13] == null) {
			dto.setIsdaTimeStamp(null);
		} else {
			Timestamp date = (Timestamp) obj[13];
			LocalDateTime dt = date.toLocalDateTime();
			LocalDateTime dateTimeFormatter = EYDateUtil
					.toISTDateTimeFromUTC(dt);
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("dd-MM-yyyy : HH:mm:ss");
			String newdate = FOMATTER.format(dateTimeFormatter);
			dto.setIsdaTimeStamp(newdate);
		}
		if (obj[15] == null) {
			dto.setAmdhistTimeStamp(null);
		} else {
			Timestamp date = (Timestamp) obj[15];
			LocalDateTime dt = date.toLocalDateTime();
			LocalDateTime dateTimeFormatter = EYDateUtil
					.toISTDateTimeFromUTC(dt);
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("dd-MM-yyyy : HH:mm:ss");
			String newdate = FOMATTER.format(dateTimeFormatter);
			dto.setAmdhistTimeStamp(newdate);
		}
		if (obj[17] == null) {
			dto.setImpgTimeStamp(null);
		} else {
			Timestamp date = (Timestamp) obj[17];
			LocalDateTime dt = date.toLocalDateTime();
			LocalDateTime dateTimeFormatter = EYDateUtil
					.toISTDateTimeFromUTC(dt);
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("dd-MM-yyyy : HH:mm:ss");
			String newdate = FOMATTER.format(dateTimeFormatter);
			dto.setImpgTimeStamp(newdate);
		}
		if (obj[19] == null) {
			dto.setImpgSezTimeStamp(null);
		} else {
			Timestamp date = (Timestamp) obj[19];
			LocalDateTime dt = date.toLocalDateTime();
			LocalDateTime dateTimeFormatter = EYDateUtil
					.toISTDateTimeFromUTC(dt);
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("dd-MM-yyyy : HH:mm:ss");
			String newdate = FOMATTER.format(dateTimeFormatter);
			dto.setImpgSezTimeStamp(newdate);
		}

		return dto;
	}

}
