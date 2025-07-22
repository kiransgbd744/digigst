package com.ey.advisory.app.gstr3b;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.UserCreationEntity;
import com.ey.advisory.admin.data.repositories.client.UserCreationRepository;
import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconRequestIdsDto;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Sakshi.jain
 *
 */

@Slf4j
@Service("Gstr1vs3bRequestStatusServiceImpl")
public class Gstr1vs3bRequestStatusServiceImpl
		implements Gstr1vs3bRequestStatusService {

	@Autowired
	@Qualifier("Gstr1vs3bRequestStatusDaoImpl")
	private Gstr1vs3bRequestStatusDao requestStatusDao;

	@Autowired
	@Qualifier("UserCreationRepository")
	UserCreationRepository repo;

	@Override
	public List<Gstr1Vs3BRequestStatusDto> getRequestIdSummary(
			Gstr2InitiateReconReqDto reqDto, String userName) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin Gstr1vs3bRequestStatusServiceImpl"
					+ ".getRequestIdSummary ,"
					+ "inside the getRequestIdSummary() method";
			LOGGER.debug(msg);
		}
		
		List<Gstr1Vs3BRequestStatusDto> resp = requestStatusDao
				.getRequestIdSummaryData(reqDto, userName);
		
		Collector<Gstr1Vs3BRequestStatusDto, ?, Gstr1Vs3BRequestStatusDto> collector2 = Collectors
				.reducing(new Gstr1Vs3BRequestStatusDto(),
						(Irr1, Irr2) -> mergeDtos(Irr1, Irr2));

		Map<Object, Gstr1Vs3BRequestStatusDto> responseList = resp
				.stream().collect(Collectors.groupingBy(o -> o.getRequestId(),
						collector2));

		List<Gstr1Vs3BRequestStatusDto> res = new ArrayList<>(
				responseList.values());
		
	

		List<UserCreationEntity> userdata = repo.findAll();

		Map<String, String> emailMap = new HashMap<>();

		userdata.forEach(data -> {
			emailMap.put(data.getUserName(), data.getEmail());
		});

		res.forEach(dto -> {
			String username = dto.getInitiatedBy();
			if (dto.getInitiatedBy().equalsIgnoreCase("SYSTEM")) {
				dto.setEmailId("SYSTEM");
			} else {
				dto.setEmailId(emailMap.get(username));
			}
		});

		Collections.sort(res, Comparator
				.comparing(Gstr1Vs3BRequestStatusDto::getRequestId).reversed());

		if (LOGGER.isDebugEnabled()) {
			String msg = "End Gstr1vs3bRequestStatusServiceImpl"
					+ ".getRequestIdSummary ,"
					+ "inside the getRequestIdSummary() method with response -"
					+ resp.toString();
			LOGGER.debug(msg);
		}

		return res;
	}
	
	private Gstr1Vs3BRequestStatusDto mergeDtos(
			Gstr1Vs3BRequestStatusDto irr1,
			Gstr1Vs3BRequestStatusDto irr2) {

		Gstr1Vs3BRequestStatusDto irrs = new Gstr1Vs3BRequestStatusDto();

		if (irr1.getGstins() == null || irr1.getGstins().isEmpty()) {
			irrs.setGstins(irr2.getGstins());

		} else {
			irr1.getGstins().addAll(irr2.getGstins());
			irrs.setGstins(irr1.getGstins());

		}
		irrs.setCompletionOn(irr2.getCompletionOn());
		irrs.setInitiatedBy(irr2.getInitiatedBy());
		irrs.setInitiatedOn(irr2.getInitiatedOn());
		irrs.setRequestId(irr2.getRequestId());
		irrs.setStatus(irr2.getStatus());
		irrs.setToTaxPeriod(irr2.getToTaxPeriod());
		irrs.setFromTaxPeriod(irr2.getFromTaxPeriod());
		irrs.setGstinCount(irr1.getGstinCount() + 1);
		irrs.setRptDownldPath(irr2.getRptDownldPath());
		irrs.setDownld(irr2.isDownld());
		return irrs;
	}
	
	@Override
	public List<Gstr2InitiateReconRequestIdsDto> getRequestIds(String userName,
			Long entityId) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin Gstr1vs3bRequestStatusServiceImpl"
					+ ".getRequestIds ," + "inside the getRequestIds() method";
			LOGGER.debug(msg);
		}

		List<BigInteger> response = requestStatusDao.getRequestIds(userName,
				entityId);
		
		if (LOGGER.isDebugEnabled()) {
			String msg = "getRequestIds " + response ;
			LOGGER.debug(msg);
		}
		return response.stream().map(o -> convertToRequestIdDto(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Gstr2InitiateReconRequestIdsDto convertToRequestIdDto(
			BigInteger requestId) {
		Gstr2InitiateReconRequestIdsDto dto = new Gstr2InitiateReconRequestIdsDto();
		dto.setRequestId(requestId);
		return dto;
	}
}