package com.ey.advisory.app.gstr2b;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.Gstr2bGet2bGstinDetailsRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2bGet2bRequestStatusRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Component("Gstr2BRequestStatusServiceImpl")
public class Gstr2BRequestStatusServiceImpl
		implements Gstr2BRequestStatusService {

	@Autowired
	@Qualifier("Gstr2bGet2bRequestStatusRepository")
	Gstr2bGet2bRequestStatusRepository configRepo;

	@Autowired
	@Qualifier("Gstr2bGet2bGstinDetailsRepository")
	Gstr2bGet2bGstinDetailsRepository gstinDetailsRepo;

	@Override
	public List<Gstr2bGet2bRequestStatusDto> getRequestWiseStatus(
			String userName) {

		//userName = "SYSTEM";
		
		try{
		List<Gstr2bGet2bRequestStatusEntity> respList = configRepo
				.findByCreatedBy(userName);

		List<Gstr2bGet2bRequestStatusDto> result = respList.stream()
				.map(o -> convertDto(o)).collect(Collectors.toList());

		return result;
		} catch(Exception ex){
			ex.printStackTrace();
			String msg = "Gstr2BRequestStatusServiceImpl :: Error while "
					+ "fetching data for request status";
			LOGGER.error(msg,ex);
			throw new AppException(ex);
		}
		
	}

	private Gstr2bGet2bRequestStatusDto convertDto(
			Gstr2bGet2bRequestStatusEntity o) {
		
		try{

		Gstr2bGet2bRequestStatusDto dto = new Gstr2bGet2bRequestStatusDto();

		List<Gstr2bGet2bGstinDetailsEntity> gstinDetails = gstinDetailsRepo
				.FindByReqId(o.getReqId());

		List<Gstr2BReqStatusDetailsDto> gstinList = gstinDetails.stream()
				.map(g -> convertGstin(g))
				.distinct()
				.collect(Collectors.toList());

		List<Gstr2BReqStatusDetailsDto> taxPeriodList = gstinDetails.stream()
				.map(t -> convertTaxPeriod(t))
				.distinct()
				.collect(Collectors.toList());

		if (o.getCompletedOn() != null) {
			LocalDateTime completedOn = EYDateUtil
					.toISTDateTimeFromUTC(o.getCompletedOn());
			dto.setCompletedOn(completedOn.toString());
		}

		if (o.getCreatedDate() != null) {
			LocalDateTime createdOn = EYDateUtil
					.toISTDateTimeFromUTC(o.getCreatedDate());
			dto.setCreatedOn(createdOn.toString());
		}

		dto.setCreatedBy(o.getCreatedBy());
		dto.setFilePath(o.getFilePath());
		dto.setGstinCount(o.getGstinCount());
		dto.setReportType(o.getReportType());
		dto.setReqId(o.getReqId());
		dto.setStatus(o.getStatus());
		dto.setTaxPeriodCount(o.getTaxPeriodCount());
		dto.setGstinList(gstinList);
		dto.setTaxPeriodList(taxPeriodList);

		return dto;
		
		} catch(Exception ex){
			LOGGER.error("Gstr2BRequestStatusServiceImpl :: "
					+ "Error while converting dtos",ex);
			throw new AppException(ex);
			
		}
		
		
	}

	private Gstr2BReqStatusDetailsDto convertGstin(
			Gstr2bGet2bGstinDetailsEntity o) {

		Gstr2BReqStatusDetailsDto dto = new Gstr2BReqStatusDetailsDto();

		dto.setGstin(o.getGstin());
		return dto;
	}

	private Gstr2BReqStatusDetailsDto convertTaxPeriod(
			Gstr2bGet2bGstinDetailsEntity o) {

		Gstr2BReqStatusDetailsDto dto = new Gstr2BReqStatusDetailsDto();

		dto.setTaxPeriod(o.getRetPeriod());

		return dto;
	}

}
