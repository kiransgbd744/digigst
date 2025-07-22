/**
 * 
 */
package com.ey.advisory.app.glrecon;

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
import com.ey.advisory.app.anx2.initiaterecon.Gstr2InitiateReconReportRequestStatusDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconRequestIdsDto;
import com.ey.advisory.gstr2.userdetails.UserCreateDetailService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */

@Slf4j
@Service("GlReconInitiateReconReportRequestStatusServiceImpl")
public class GlReconInitiateReconReportRequestStatusServiceImpl
		implements GLReconInitiateReconReportRequestStatusService {

	@Autowired
	@Qualifier("GLReconInitiateReconReportRequestStatusDaoImpl")
	private GLReconInitiateReconReportRequestStatusDao requestStatusDao;

	@Autowired
	@Qualifier("UserCreationRepository")
	UserCreationRepository repo;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;

	@Autowired
	@Qualifier("UserCreateDetailServiceImpl")
	private UserCreateDetailService userCreateService;

	@Autowired
	private UserCreationRepository userRepo;

	private Gstr2InitiateReconReportRequestStatusDto mergeDtos(
			Gstr2InitiateReconReportRequestStatusDto irr1,
			Gstr2InitiateReconReportRequestStatusDto irr2) {

		Gstr2InitiateReconReportRequestStatusDto irrs = new Gstr2InitiateReconReportRequestStatusDto();

		if (irr1.getGstins() == null || irr1.getGstins().isEmpty()) {
			irrs.setGstins(irr2.getGstins());

		} else {
			irr1.getGstins().addAll(irr2.getGstins());
			irrs.setGstins(irr1.getGstins());

		}
		irrs.setRequestId(irr2.getRequestId());
		irrs.setCompletionOn(irr2.getCompletionOn());
		irrs.setInitiatedBy(irr2.getInitiatedBy());
		irrs.setInitiatedOn(irr2.getInitiatedOn());
		irrs.setStatus(irr2.getStatus());
		irrs.setPath(irr2.getPath());
		irrs.setToTaxPeriod(irr2.getToTaxPeriod());
		irrs.setFromTaxPeriod(irr2.getFromTaxPeriod());
		irrs.setGstinCount(irr1.getGstinCount() + 1);
		irrs.setReconType(irr2.getReconType());
		return irrs;
	}

	@Override
	public List<Gstr2InitiateReconRequestIdsDto> getRequestIds(String userName,
			Long entityId, Gstr2InitiateReconReqDto reqDto) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin GlReconInitiateReconReportRequestStatusServiceImpl"
					+ ".getRequestIds ," + "inside the getRequestIds() method";
			LOGGER.debug(msg);
		}

		List<BigInteger> response = requestStatusDao.getRequestIds(userName,
				entityId,  reqDto);

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
	public List<Gstr2InitiateReconReportRequestStatusDto> getReportRequestData(
			Gstr2InitiateReconReqDto reqDto, String userName) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin GlReconInitiateReconReportRequestStatusServiceImpl"
					+ ".getReportRequestStatus ,"
					+ "inside the getReportRequestStatus() method";
			LOGGER.debug(msg);
		}

		List<Gstr2InitiateReconReportRequestStatusDto> response = requestStatusDao
				.getReportRequestData(reqDto, userName);

		Collector<Gstr2InitiateReconReportRequestStatusDto, ?, Gstr2InitiateReconReportRequestStatusDto> collector2 = Collectors
				.reducing(new Gstr2InitiateReconReportRequestStatusDto(),
						(Irr1, Irr2) -> mergeDtos(Irr1, Irr2));

		Map<Object, Gstr2InitiateReconReportRequestStatusDto> responseList = response
				.stream().collect(Collectors.groupingBy(o -> o.getRequestId(),
						collector2));

		List<Gstr2InitiateReconReportRequestStatusDto> res = new ArrayList<>(
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
			List<Gstr2InitiateReconReportRequestStatusDto> res1 = res.stream()
					.map(o -> checkToEmail(o, initiationByUserEmailId))
					.collect(Collectors.toCollection(ArrayList::new));
			List<Gstr2InitiateReconReportRequestStatusDto> emailList = new ArrayList<>();

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
			String msg = "Begin GlReconInitiateReconReportRequestStatusServiceImpl"
					+ ".getReportRequestStatus ,"
					+ "before returning getReportRequestData() method";
			LOGGER.debug(msg);
		}
		return res;
	}

	private Gstr2InitiateReconReportRequestStatusDto checkToEmail(
			Gstr2InitiateReconReportRequestStatusDto dto,
			List<String> initiationByUserEmailId) {

		if (!initiationByUserEmailId.contains(dto.getEmailId())) {
			return null;
		}
		return dto;
	}

	}