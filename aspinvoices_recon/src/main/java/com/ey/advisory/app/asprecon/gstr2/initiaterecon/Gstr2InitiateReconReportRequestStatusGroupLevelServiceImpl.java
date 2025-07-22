package com.ey.advisory.app.asprecon.gstr2.initiaterecon;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconRequestIdsDto;
import com.ey.advisory.core.dto.UserCreationReqDto;
import com.ey.advisory.gstr2.userdetails.UserCreateDetailService;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service("Gstr2InitiateReconReportRequestStatusGroupLevelServiceImpl")
public class Gstr2InitiateReconReportRequestStatusGroupLevelServiceImpl
		implements Gstr2InitiateReconReportRequestStatusForGroupService {

	@Autowired
	@Qualifier("Gstr2InitiateReconReportRequestStatusGroupLevelDaoImpl")
	private Gstr2InitiateReconReportRequestStatusForGroupDao requestStatusDao;

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
		irrs.setCompletionOn(irr2.getCompletionOn());
		irrs.setInitiatedBy(irr2.getInitiatedBy());
		irrs.setInitiatedOn(irr2.getInitiatedOn());
		irrs.setRequestId(irr2.getRequestId());
		irrs.setStatus(irr2.getStatus());
		irrs.setPath(irr2.getPath());
		irrs.setToTaxPeriod(irr2.getToTaxPeriod());
		irrs.setFromTaxPeriod(irr2.getFromTaxPeriod());
		irrs.setToTaxPeriod2A(irr2.getToTaxPeriod2A());
		irrs.setFromTaxPeriod2A(irr2.getFromTaxPeriod2A());
		irrs.setToDocDate(irr2.getToDocDate());
		irrs.setFromDocDate(irr2.getFromDocDate());
		irrs.setGstinCount(irr1.getGstinCount() + 1);
		irrs.setReqType(irr2.getReqType());
		irrs.setReconType(irr2.getReconType());
		irrs.setEntityName(irr2.getEntityName());
		return irrs;
	}

	@Override
	public List<Gstr2InitiateReconRequestIdsDto> getRequestIds(String userName,
			List<Long> entityId, Gstr2InitiateReconReqDto reqDto) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin Gstr2InitiateReconReportRequestStatusGroupLevelServiceImpl"
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
			String msg = "Begin Gstr2InitiateReconReportRequestStatusGroupLevelServiceImpl"
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
			String msg = "Begin Gstr2InitiateReconReportRequestStatusGroupLevelServiceImpl"
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

	@Override
	public List<Gstr2InitiateReconUsernameDto> getgstr2UserNames(List<Long> entityId,
			String userName) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin Gstr2InitiateReconReportRequestStatusGroupLevelServiceImpl"
					+ ".getgstr2UserNames ,"
					+ "inside the getgstr2UserNames() method";
			LOGGER.debug(msg);
		}

		String groupCode = TenantContext.getTenantId();
		UserCreationReqDto dto = new UserCreationReqDto();
		dto.setGroupCode(groupCode);

		/*List<Long> entityIds = new ArrayList<Long>();
		entityIds.add(entityId);*/
		List<Long> optedEntities = entityConfigPemtRepo
				.getAllEntitiesOptedUserRestriction(entityId, "I29");

		List<String> response = new ArrayList<>();

		if (optedEntities != null && !optedEntities.isEmpty()) {
			response.add(userName);

		} else {
			List<Object[]> userRepo1 = new ArrayList<>();

			for (Long enttyId : entityId) {
			// response = repo.getUserNames();
		    userRepo1.addAll(userRepo.getUserInfoByEntityId(enttyId));
			}
			userRepo1.forEach(user -> {
				response.add(user[0].toString());
			});
		}

		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin Gstr2InitiateReconReportRequestStatusGroupLevelServiceImpl"
					+ ".getReportRequestStatus ,"
					+ "before returning getReportRequestStatus() method";
			LOGGER.debug(msg);
		}
		return response.stream().map(o -> convertToUserNameDto(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Gstr2InitiateReconUsernameDto convertToUserNameDto(
			String userName) {
		Gstr2InitiateReconUsernameDto dto = new Gstr2InitiateReconUsernameDto();
		dto.setUserName(userName);
		return dto;
	}

	@Override
	public List<Gstr2InitiateReconEmailDto> getgstr2EmailIds(List<Long> entityId,
			String userName) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin Gstr2InitiateReconReportRequestStatusGroupLevelServiceImpl"
					+ ".getgstr2UserNames ,"
					+ "inside the getgstr2UserNames() method";
			LOGGER.debug(msg);
		}

		String groupCode = TenantContext.getTenantId();
		UserCreationReqDto dto = new UserCreationReqDto();
		dto.setGroupCode(groupCode);

		/*List<Long> entityIds = new ArrayList<Long>();
		entityIds.add(entityId);*/
		List<Long> optedEntities = entityConfigPemtRepo
				.getAllEntitiesOptedUserRestriction(entityId, "I29");

		List<String> response = new ArrayList<>();

		if (optedEntities != null && !optedEntities.isEmpty()) {
			List<String> emails = repo.findEmailByUser(userName);
			response.addAll(emails);
		} else {

			// response = repo.getEmailIds();
			/*
			 * List<UserCreationDto> userDataList = userCreateService
			 * .findUserDetail(dto);
			 */
			List<Object[]> userRepo1 = new ArrayList<>();

			for (Long enttyId : entityId) {
		    userRepo1.addAll(userRepo.getUserInfoByEntityId(enttyId));

			}
			userRepo1.forEach(user -> {
				response.add(user[1].toString());
			});
		}

		Comparator<String> emailCmp = new Comparator<String>() {
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		};
		Collections.sort(response, emailCmp);

		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin Gstr2InitiateReconReportRequestStatusGroupLevelServiceImpl"
					+ ".getReportRequestStatus ,"
					+ "before returning getReportRequestStatus() method";
			LOGGER.debug(msg);
		}
		return response.stream().map(o -> convertToEmailDto(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private Gstr2InitiateReconEmailDto convertToEmailDto(String email) {
		Gstr2InitiateReconEmailDto dto = new Gstr2InitiateReconEmailDto();
		dto.setEmail(email);
		return dto;
	}
}