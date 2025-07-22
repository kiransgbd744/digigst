/**
 * 
 */
package com.ey.advisory.app.recon3way;

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
import com.ey.advisory.app.asprecon.gstr2.initiaterecon.Gstr2InitiateReconEmailDto;
import com.ey.advisory.app.asprecon.gstr2.initiaterecon.Gstr2InitiateReconUsernameDto;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconRequestIdsDto;
import com.ey.advisory.gstr2.initiaterecon.EWB3WayInitiateReconReportRequestStatusDao;
import com.ey.advisory.gstr2.initiaterecon.EWB3WayInitiateReconReportRequestStatusDto;
import com.ey.advisory.gstr2.userdetails.UserCreateDetailService;
import com.ey.advisory.gstr2.userdetails.UserCreationReqDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Service("EWB3WayReportRequestStatusServiceImpl")
public class EWB3WayReportRequestStatusServiceImpl
		implements EWB3WayInitiateReconReportRequestStatusService {

	@Autowired
	@Qualifier("EWB3WayInitiateReconReportRequestStatusDaoImpl")
	private EWB3WayInitiateReconReportRequestStatusDao requestStatusDao;
	
	@Autowired
	private UserCreationRepository userRepo;
	
	@Autowired
	@Qualifier("UserCreationRepository")
	UserCreationRepository repo;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;

	@Autowired
	@Qualifier("UserCreateDetailServiceImpl")
	private UserCreateDetailService userCreateService;


	private EWB3WayInitiateReconReportRequestStatusDto mergeDtos(
			EWB3WayInitiateReconReportRequestStatusDto irr1,
			EWB3WayInitiateReconReportRequestStatusDto irr2) {

		EWB3WayInitiateReconReportRequestStatusDto irrs = new EWB3WayInitiateReconReportRequestStatusDto();

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
		irrs.setToDocDate(irr2.getToDocDate());
		irrs.setFromDocDate(irr2.getFromDocDate());
		irrs.setGstinCount(irr1.getGstinCount() + 1);
		irrs.setReqType(irr2.getReqType());
		irrs.setReconType(irr2.getReconType());
		return irrs;
	}

	@Override
	public List<Gstr2InitiateReconRequestIdsDto> getRequestIds(String userName,
			Long entityId) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin EWB3WayReportRequestStatusServiceImpl"
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
	public List<EWB3WayInitiateReconReportRequestStatusDto> getReportRequestData(
			Gstr2InitiateReconReqDto reqDto, String userName) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin EWB3WayReportRequestStatusServiceImpl"
					+ ".getReportRequestStatus ,"
					+ "inside the getReportRequestStatus() method";
			LOGGER.debug(msg);
		}

		List<EWB3WayInitiateReconReportRequestStatusDto> response = requestStatusDao
				.getReportRequestData(reqDto, userName);

		Collector<EWB3WayInitiateReconReportRequestStatusDto, ?, EWB3WayInitiateReconReportRequestStatusDto> collector2 = Collectors
				.reducing(new EWB3WayInitiateReconReportRequestStatusDto(),
						(Irr1, Irr2) -> mergeDtos(Irr1, Irr2));

		Map<Object, EWB3WayInitiateReconReportRequestStatusDto> responseList = response
				.stream().collect(Collectors.groupingBy(o -> o.getRequestId(),
						collector2));

		List<EWB3WayInitiateReconReportRequestStatusDto> res = new ArrayList<>(
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
			List<EWB3WayInitiateReconReportRequestStatusDto> res1 = res.stream()
					.map(o -> checkToEmail(o, initiationByUserEmailId))
					.collect(Collectors.toCollection(ArrayList::new));
			List<EWB3WayInitiateReconReportRequestStatusDto> emailList = new ArrayList<>();

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
			String msg = "Begin EWB3WayReportRequestStatusServiceImpl"
					+ ".getReportRequestStatus ,"
					+ "before returning getReportRequestData() method";
			LOGGER.debug(msg);
		}
		return res;
	}

	private EWB3WayInitiateReconReportRequestStatusDto checkToEmail(
			EWB3WayInitiateReconReportRequestStatusDto dto,
			List<String> initiationByUserEmailId) {

		if (!initiationByUserEmailId.contains(dto.getEmailId())) {
			return null;
		}
		return dto;
	}

	@Override
	public List<Gstr2InitiateReconUsernameDto> getEWB3WayUserNames(
			Long entityId, String userName) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin EWB3WayReportRequestStatusServiceImpl"
					+ ".getEWB3WayUserNames ,"
					+ "inside the getEWB3WayUserNames() method";
			LOGGER.debug(msg);
		}

		String groupCode = TenantContext.getTenantId();
		UserCreationReqDto dto = new UserCreationReqDto();
		dto.setGroupCode(groupCode);

		List<String> response = new ArrayList<>();
		//repo.getUserNames();List<Object[]> userRepo1 = userRepo.getUserInfoByEntityId(entityId);
		
		//repo.getEmailIds();
		List<Object[]> userRepo1 = userRepo.getUserInfoByEntityId(entityId);

		userRepo1.forEach(user -> {
			response.add(user[0].toString());
		});


		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin EWB3WayReportRequestStatusServiceImpl"
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
	public List<Gstr2InitiateReconEmailDto> getgstr2EmailIds(Long entityId,
			String userName) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin Gstr2InitiateReconReportRequestStatusServiceImpl"
					+ ".getgstr2UserNames ,"
					+ "inside the getgstr2UserNames() method";
			LOGGER.debug(msg);
		}

		String groupCode = TenantContext.getTenantId();
		UserCreationReqDto dto = new UserCreationReqDto();
		dto.setGroupCode(groupCode);

		List<String> response = new ArrayList<>(); 
				//repo.getEmailIds();
		List<Object[]> userRepo1 = userRepo.getUserInfoByEntityId(entityId);

		userRepo1.forEach(user -> {
			response.add(user[1].toString());
		});

		Comparator<String> emailCmp = new Comparator<String>() {
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		};
		Collections.sort(response, emailCmp);

		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin EWB3WayReportRequestStatusServiceImpl"
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