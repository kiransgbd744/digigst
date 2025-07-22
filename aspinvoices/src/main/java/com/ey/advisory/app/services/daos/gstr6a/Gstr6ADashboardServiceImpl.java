package com.ey.advisory.app.services.daos.gstr6a;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6ADashBoardRespDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6aDashBoardDataDto;
import com.ey.advisory.app.docs.dto.gstr6a.InnerDetailDto;
import com.ey.advisory.common.EYDateUtil;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;
/**
*
* @author Ravindra V S
*
*/
@Slf4j
@Component("Gstr6ADashboardServiceImpl")
public class Gstr6ADashboardServiceImpl implements Gstr6ADashboardService {

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository getAnx1BatchRepository;

	@Override
	public List<Gstr6ADashBoardRespDto> getStatusData(List<String> gstins, int derivedStartPeriod, int derivedEndPeriod,
			Map<String, String> stateNames, Map<String, String> authTokenStatus) {
		List<Gstr6ADashBoardRespDto> dtoList = new ArrayList<>();

		try {

			List<Object[]> statusDetail = getAnx1BatchRepository.getGstr6AStatusAndReturnPeriod(gstins,
					derivedStartPeriod, derivedEndPeriod);
			
			List<Gstr6aDashBoardDataDto> sectionWiseDetails = new ArrayList<Gstr6aDashBoardDataDto>();
			
			statusDetail.forEach(batch -> {
				Gstr6aDashBoardDataDto sectionDto = new Gstr6aDashBoardDataDto();
				sectionDto.setGstin(
						batch[0] != null ? String.valueOf(batch[0]) : null);
				sectionDto.setTaxPeriod(
						batch[1] != null ? String.valueOf(batch[1]) : null);
				sectionDto.setSection(
						batch[2] != null ? String.valueOf(batch[2]) : null);
				sectionDto.setStatus(
						batch[3] != null ? String.valueOf(batch[3]) : null);
				if (batch[4] != null) {
					LocalDateTime dt = (LocalDateTime) batch[4];
					LocalDateTime dateTimeFormatter = EYDateUtil
							.toISTDateTimeFromUTC(dt);
					DateTimeFormatter FOMATTER = DateTimeFormatter
							.ofPattern("dd-MM-yyyy : HH:mm:ss");
					String newdate = FOMATTER.format(dateTimeFormatter);
					sectionDto.setEndtime(newdate);
				}
				if(sectionDto.getEndtime() == null){
					if (batch[5] != null) {
						LocalDateTime dt = (LocalDateTime) batch[5];
						LocalDateTime dateTimeFormatter = EYDateUtil
								.toISTDateTimeFromUTC(dt);
						DateTimeFormatter FOMATTER = DateTimeFormatter
								.ofPattern("dd-MM-yyyy : HH:mm:ss");
						String newdate = FOMATTER.format(dateTimeFormatter);
						sectionDto.setEndtime(newdate);
					}
				}
				
				sectionWiseDetails.add(sectionDto);
			});

			Map<Object, List<Gstr6aDashBoardDataDto>> dataMap = sectionWiseDetails.stream()
					.collect(Collectors.groupingBy(e -> e.getGstin()));

			dtoList = gstins.stream().distinct().map(o -> convertToDto(o, stateNames, authTokenStatus, dataMap))
					.distinct().collect(Collectors.toList());

			dtoList.sort(Comparator.comparing(Gstr6ADashBoardRespDto::getGstin));

		} catch (Exception ex) {
			String msg = String.format(
					"Error occured in " + "Gstr2BDashboardServiceImpl.getStatusData() method " + "%s gstins", gstins);
			LOGGER.error(msg, ex);
			ex.printStackTrace();

		}
		return dtoList;
	}

	private Gstr6ADashBoardRespDto convertToDto(String o, Map<String, String> stateNames,
			Map<String, String> authTokenStatus, Map<Object, List<Gstr6aDashBoardDataDto>> errorMap) {

		Gstr6ADashBoardRespDto dto = new Gstr6ADashBoardRespDto();

		dto.setGstin(o);
		dto.setStateName(stateNames.get(o));
		dto.setAuthStatus(authTokenStatus.get(o));
		dto.setRegType("ISD");
		

		List<Gstr6aDashBoardDataDto> errorList = errorMap.get(o) != null ? errorMap.get(o) : new ArrayList<>();
		
		if (errorList != null && !errorList.isEmpty()) {
			
			Map<Object, List<Gstr6aDashBoardDataDto>> dataMap = errorList.stream()
					.collect(Collectors.groupingBy(e -> e.getTaxPeriod()));

			
			Set<String> taxperiodList = errorList.stream()
					.map(Gstr6aDashBoardDataDto::getTaxPeriod)
					.collect(Collectors.toSet());
			
			List<InnerDetailDto> collect = new ArrayList< InnerDetailDto>();
			
			for(String taxperiod : taxperiodList){
				List<Gstr6aDashBoardDataDto> eachTaxperiodList = dataMap.get(taxperiod);
				List<String> StatusList = eachTaxperiodList.stream()
						.map(Gstr6aDashBoardDataDto::getStatus)
						.collect(Collectors.toList());
				String status = filterDataByStatus(StatusList);
				InnerDetailDto txDto = new InnerDetailDto();
				
				txDto.setInitiatedOn(eachTaxperiodList.get(0).getEndtime());

				txDto.setStatus(status);

				txDto.setTaxPeriod(taxperiod);
				
				collect.add(txDto);
				
			}
			dto.setTaxPeriodDetails(collect);
		}
		return dto;
	}

	private String filterDataByStatus(List<String> statusList) {
		String status = "";
		Set<String> statusSet = Sets.newHashSet(statusList);
		if (statusSet.contains("INITIATED")) {
			status = "INITIATED";
		} else if (statusSet.contains("INPROGRESS")) {
			status = "INPROGRESS";
		} else if ((statusSet.contains("SUCCESS") && statusSet.contains("FAILED")
				&& statusSet.contains("SUCCESS_WITH_NO_DATA"))) {
			status = "PARTIALLY_SUCCESS";
		} else if (statusSet.contains("FAILED") && statusSet.contains("SUCCESS_WITH_NO_DATA")) {
			status = "PARTIALLY_SUCCESS";
		} else if (statusSet.contains("FAILED") && statusSet.contains("SUCCESS")) {
			status = "PARTIALLY_SUCCESS";
		} else if ((statusSet.contains("SUCCESS") && statusSet.contains("SUCCESS_WITH_NO_DATA"))) {
			status = "SUCCESS";
		} else if (statusSet.contains("SUCCESS")) {
			status = "SUCCESS";
		} else if (statusSet.contains("SUCCESS_WITH_NO_DATA")) {
			status = "SUCCESS";
		} else if (statusSet.contains("FAILED")) {
			status = "FAILED";

		}
		return status;
	}

}
