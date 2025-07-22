/**
 * 
 */
package com.ey.advisory.app.reconewbvsitc04;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.UserCreationEntity;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.UserCreationRepository;
import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconRequestIdsDto;
import com.ey.advisory.gstr2.userdetails.UserCreateDetailService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Ravindra V S
 *
 */

@Slf4j
@Service("EwbVsItc04ReportRequestStatusServiceImpl")
public class EwbVsItc04ReportRequestStatusServiceImpl
		implements EwbVsItc04InitiateReconReportRequestStatusService {

	@Autowired
	@Qualifier("EwbVsItc04InitiateReconReportRequestStatusDaoImpl")
	private EwbVsItc04InitiateReconReportRequestStatusDao requestStatusDao;

	@Autowired
	@Qualifier("UserCreationRepository")
	UserCreationRepository repo;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;

	@Autowired
	@Qualifier("UserCreateDetailServiceImpl")
	private UserCreateDetailService userCreateService;


	private EwbVsItc04InitiateReconReportRequestStatusDto mergeDtos(
			EwbVsItc04InitiateReconReportRequestStatusDto irr1,
			EwbVsItc04InitiateReconReportRequestStatusDto irr2) {

		EwbVsItc04InitiateReconReportRequestStatusDto irrs = new EwbVsItc04InitiateReconReportRequestStatusDto();

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
		irrs.setFromTaxPeriod(irr2.getFromTaxPeriod());
		irrs.setToTaxPeriod(irr2.getToTaxPeriod());
		irrs.setFinancialYear(irr2.getFinancialYear());
		irrs.setGstinCount(irr2.getGstinCount());//.add(BigInteger.ONE)
		return irrs;
	}

	@Override
	public List<Gstr2InitiateReconRequestIdsDto> getRequestIds(String userName,
			Long entityId) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin EwbVsItc04ReportRequestStatusServiceImpl"
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
	public List<EwbVsItc04InitiateReconReportRequestStatusDto> getReportRequestData(
			Gstr2InitiateReconReqDto reqDto, String userName) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin EwbVsItc04ReportRequestStatusServiceImpl"
					+ ".getReportRequestStatus ,"
					+ "inside the getReportRequestData() method";
			LOGGER.debug(msg);
		}

		List<EwbVsItc04InitiateReconReportRequestStatusDto> response = requestStatusDao
				.getReportRequestData(reqDto, userName);

		Collector<EwbVsItc04InitiateReconReportRequestStatusDto, ?, EwbVsItc04InitiateReconReportRequestStatusDto> collector2 = Collectors
				.reducing(new EwbVsItc04InitiateReconReportRequestStatusDto(),
						(Irr1, Irr2) -> mergeDtos(Irr1, Irr2));

		Map<Object, EwbVsItc04InitiateReconReportRequestStatusDto> responseList = response
				.stream().collect(Collectors.groupingBy(o -> o.getRequestId(),
						collector2));

		List<EwbVsItc04InitiateReconReportRequestStatusDto> res = new ArrayList<>(
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

		List<String> initiationByUserEmailId = reqDto
				.getInitiationByUserEmailId();

		if (!CollectionUtils.isEmpty(initiationByUserEmailId)) {
			List<EwbVsItc04InitiateReconReportRequestStatusDto> res1 = res.stream()
					.map(o -> checkToEmail(o, initiationByUserEmailId))
					.collect(Collectors.toCollection(ArrayList::new));
			List<EwbVsItc04InitiateReconReportRequestStatusDto> emailList = new ArrayList<>();

			res1.forEach(dto -> {
				if (dto != null) {
					emailList.add(dto);
				}
			});
			if (emailList != null && !emailList.isEmpty()) {
				return emailList;
			}
			return new ArrayList<>();
		}

		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin EwbVsItc04ReportRequestStatusServiceImpl"
					+ ".getReportRequestStatus ,"
					+ "before returning getReportRequestData() method";
			LOGGER.debug(msg);
		}
		return res;
	}

	private EwbVsItc04InitiateReconReportRequestStatusDto checkToEmail(
			EwbVsItc04InitiateReconReportRequestStatusDto dto,
			List<String> initiationByUserEmailId) {

		if (!initiationByUserEmailId.contains(dto.getEmailId())) {
			return null;
		}
		return dto;
	}

}