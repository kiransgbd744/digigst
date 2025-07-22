/**
 * 
 */
package com.ey.advisory.service.gstr1.sales.register;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.UserCreationRepository;
import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconRequestIdsDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */

@Slf4j
@Service("Gstr1SalesRegisterRequestStatusServiceImpl")
public class Gstr1SalesRegisterRequestStatusServiceImpl
		implements Gstr1SalesRegisterRequestStatusService {

	@Autowired
	@Qualifier("SalesRegisterRequestStatusDaoImpl")
	private SalesRegisterRequestStatusDao requestStatusDao;

	@Autowired
	@Qualifier("UserCreationRepository")
	UserCreationRepository repo;

	private Gstr1SalesRegisterRequestStatusDto mergeDtos(
			Gstr1SalesRegisterRequestStatusDto irr1,
			Gstr1SalesRegisterRequestStatusDto irr2) {

		Gstr1SalesRegisterRequestStatusDto irrs = new Gstr1SalesRegisterRequestStatusDto();

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
		irrs.setGstinCount(irr1.getGstinCount() + irr2.getGstinCount());
		irrs.setReconType(irr2.getReconType());
		return irrs;
	}

	@Override
	public List<Gstr2InitiateReconRequestIdsDto> getRequestIds(String userName,
			Long entityId) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin Gstr1SalesRegisterRequestStatusServiceImpl"
					+ ".getRequestIds ," + "inside the getRequestIds() method";
			LOGGER.debug(msg);
		}

		List<BigInteger> response = requestStatusDao.getRequestIds(userName,
				entityId);

		return response.stream().map(o -> convertToRequestIdDto(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Gstr2InitiateReconRequestIdsDto convertToRequestIdDto(
			BigInteger requestId) {
		Gstr2InitiateReconRequestIdsDto dto = new Gstr2InitiateReconRequestIdsDto();
		dto.setRequestId(requestId);
		return dto;
	}

	@Override
	public List<Gstr1SalesRegisterRequestStatusDto> getReportRequestData(
			Gstr2InitiateReconReqDto reqDto, String userName) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin Gstr1SalesRegisterRequestStatusServiceImpl"
					+ ".getReportRequestStatus ,"
					+ "inside the getReportRequestStatus() method";
			LOGGER.debug(msg);
		}

		List<Gstr1SalesRegisterRequestStatusDto> response = requestStatusDao
				.getReportRequestData(reqDto, userName);

		Collector<Gstr1SalesRegisterRequestStatusDto, ?, Gstr1SalesRegisterRequestStatusDto> collector2 = Collectors
				.reducing(new Gstr1SalesRegisterRequestStatusDto(),
						(Irr1, Irr2) -> mergeDtos(Irr1, Irr2));

		Map<Object, Gstr1SalesRegisterRequestStatusDto> responseList = response
				.stream().collect(Collectors.groupingBy(o -> o.getRequestId(),
						collector2));

		List<Gstr1SalesRegisterRequestStatusDto> res = new ArrayList<>(
				responseList.values());

			return res;

	}
}