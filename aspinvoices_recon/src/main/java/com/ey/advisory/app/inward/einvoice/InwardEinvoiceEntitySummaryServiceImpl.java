package com.ey.advisory.app.inward.einvoice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.entities.client.UserCreationEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.UserCreationRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.ey.advisory.gstr2.userdetails.EntityService;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */
@Slf4j
@Component("InwardEinvoiceEntitySummaryServiceImpl")
public class InwardEinvoiceEntitySummaryServiceImpl
		implements InwardEinvoiceEntitySummaryService {

	@Autowired
	@Qualifier("InwardEinvoiceEntitySummaryDaoImpl")
	private InwardEinvoiceEntitySummaryDao dao;

	@Autowired
	@Qualifier("EntityServiceImpl")
	private EntityService entityService;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstNDetailRepository;
	
	@Autowired
	@Qualifier("UserCreationRepository")
	UserCreationRepository repo;

	@Override
	public List<InwardEinvoiceEntitySummaryResponseDto> findTableData(
			InwardEinvoiceEntitySummaryReqDto criteria) {

		List<InwardEinvoiceEntitySummaryResponseDto> list = new ArrayList<>();

		try {
			
			List<String> reqGstinList = criteria.getGstins();

			List<GSTNDetailEntity> regList = gstNDetailRepository
					.findRegTypeByGstinList(criteria.getGstins());

			Map<String, String> regMap = regList.stream()
					.collect(Collectors.toMap(o -> o.getGstin(),
							o -> o.getRegistrationType(), (o1, o2) -> o2));

			Map<String, String> stateNames = entityService
					.getStateNames(criteria.getGstins());
			Map<String, String> authTokenStatus = authTokenService
					.getAuthTokenStatusForGstins(criteria.getGstins());
			
			list = dao.findTableData(criteria, regMap, stateNames, 
					authTokenStatus);
			
			List<String> respGstinList = new ArrayList<>();
			if (list != null && !list.isEmpty()) {
				
				for (InwardEinvoiceEntitySummaryResponseDto listData : list) {
					String respGstin = listData.getGstin();
					respGstinList.add(respGstin);
				}
				
				//creating default entries
				reqGstinList.removeAll(respGstinList);
			}
			
			List<InwardEinvoiceEntitySummaryResponseDto> DefaultEntities = 
					reqGstinList.stream().map(o -> setDefaultEntityValue(o,
							regMap, stateNames, authTokenStatus))
					.collect(Collectors.toList());
			
			list.addAll(DefaultEntities);
			
			list.sort(
					Comparator.comparing(InwardEinvoiceEntitySummaryResponseDto
							::getGstin));

		} catch (Exception e) {
			LOGGER.error("InwardEinvoiceEntitySummaryServiceImpl.findTableData:"
					+ " Error occuerd {} ",e);
			throw new AppException(e);
		}

		return list;
	}

	private InwardEinvoiceEntitySummaryResponseDto setDefaultEntityValue(String o, 
			Map<String, String> regMap, Map<String, String> stateNames,
			Map<String, String> authTokenStatus) {
		
		InwardEinvoiceEntitySummaryResponseDto dto = 
				new InwardEinvoiceEntitySummaryResponseDto();
		dto.setActiveEinv(0);
		dto.setAuthToken(authTokenStatus.get(o));
		dto.setCanclEinv(0);
		dto.setCountEinv(0);
		dto.setCountSuppGstn(0);
		dto.setGetCallTimestamp(null);
		dto.setGstin(o);
		dto.setRegType(regMap.get(o));
		dto.setState(stateNames.get(o));
		dto.setStatus("Not Initiated");
		dto.setTotlInvAmt(BigDecimal.ZERO);
		return dto;
	}

	@Override
	public List<InwardEinvoiceDetailedInfoResponseDto> findTableDetailedData(
			InwardEinvoiceEntitySummaryReqDto criteria) {

		List<InwardEinvoiceDetailedInfoResponseDto> list = new ArrayList<>();

		try {

			List<String> reqGstinList = criteria.getGstins();
			
			List<GSTNDetailEntity> regList = gstNDetailRepository
					.findRegTypeByGstinList(criteria.getGstins());

			Map<String, String> regMap = regList.stream()
					.collect(Collectors.toMap(o -> o.getGstin(),
							o -> o.getRegistrationType(), (o1, o2) -> o2));

			Map<String, String> stateNames = entityService
					.getStateNames(criteria.getGstins());
			Map<String, String> authTokenStatus = authTokenService
					.getAuthTokenStatusForGstins(criteria.getGstins());
			
			List<UserCreationEntity> userdata = repo.findAll();

			Map<String, String> emailMap = new HashMap<>();

			userdata.forEach(data -> {
				emailMap.put(data.getUserName(), data.getEmail());
			});

			list = dao.findTableDetailedData(criteria, regMap, stateNames, 
					authTokenStatus, emailMap);
			
			List<String> respGstinList = new ArrayList<>();
			if (list != null && !list.isEmpty()) {
				
				for (InwardEinvoiceDetailedInfoResponseDto listData : list) {
					String respGstin = listData.getGstin();
					respGstinList.add(respGstin);
				}
				
				//creating default entries
				reqGstinList.removeAll(respGstinList);
			}
			
			List<InwardEinvoiceDetailedInfoResponseDto> DefaultEntities = 
					reqGstinList.stream().map(o -> setDefaultGstinValue(o,
							regMap, stateNames, authTokenStatus, emailMap))
					.collect(Collectors.toList());
			
			list.addAll(DefaultEntities);
			
			list.sort(
					Comparator.comparing(InwardEinvoiceDetailedInfoResponseDto
							::getGstin));

		} catch (Exception e) {
			LOGGER.error("InwardEinvoiceEntitySummaryServiceImpl"
					+ ".findTableDetailedData:"
					+ " Error occuerd {} ",e);
			throw new AppException(e);
		}

		return list;
	}

	private InwardEinvoiceDetailedInfoResponseDto setDefaultGstinValue(String o, 
			Map<String, String> regMap, Map<String, String> stateNames, 
			Map<String, String> authTokenStatus,
			Map<String, String> emailMap) {
		
		InwardEinvoiceDetailedInfoResponseDto dto = new 
				InwardEinvoiceDetailedInfoResponseDto();
		
		dto.setAuthToken(authTokenStatus.get(o));
		dto.setGstin(o);
		dto.setRegType(regMap.get(o));
		dto.setState(stateNames.get(o));
		dto.setStatus("Not Initiated");
		return dto;
	}

	@Override
	public InwardEinvoiceStatusScreenResponseDto findStatusData(
			InwardEinvoiceEntitySummaryReqDto criteria) {

		List<InwardEinvoiceStatusScreenResponseDto> list = new ArrayList<>();

		try {

			List<GSTNDetailEntity> regList = gstNDetailRepository
					.findRegTypeByGstinList(criteria.getGstins());

			Map<String, String> regMap = regList.stream()
					.collect(Collectors.toMap(o -> o.getGstin(),
							o -> o.getRegistrationType(), (o1, o2) -> o2));

			Map<String, String> stateNames = entityService
					.getStateNames(criteria.getGstins());
			Map<String, String> authTokenStatus = authTokenService
					.getAuthTokenStatusForGstins(criteria.getGstins());
			
			list = dao.findStatusData(criteria, regMap, stateNames, 
					authTokenStatus);
			
			if (list == null || list.isEmpty()) {

				InwardEinvoiceStatusScreenResponseDto DefaultEntities = 
						setDefaultStatusScreenValue(
						criteria.getGstins(), regMap, stateNames,
						authTokenStatus);
				
				return DefaultEntities;

			}

		} catch (Exception e) {
			LOGGER.error("InwardEinvoiceEntitySummaryServiceImpl.findStatusData:"
					+ " Error occuerd {} ",e);
			throw new AppException(e);
		}
		
		InwardEinvoiceStatusScreenResponseDto respDto = list.get(0);

		return respDto;
	}

	private InwardEinvoiceStatusScreenResponseDto setDefaultStatusScreenValue(
			List<String> gstins, Map<String, String> regMap,
			Map<String, String> stateNames,
			Map<String, String> authTokenStatus) {

		InwardEinvoiceStatusScreenResponseDto dto = 
				new InwardEinvoiceStatusScreenResponseDto();

		dto.setGstin(gstins.get(0));

		dto.setStatus("Not Initiated");

		dto.setB2bStatus("Not Initiated");

		dto.setDexpStatus("Not Initiated");

		dto.setExpwopStatus("Not Initiated");

		dto.setExpwpStatus("Not Initiated");

		dto.setSezwopStatus("Not Initiated");

		dto.setSezwpStatus("Not Initiated");

		dto.setAuthToken(authTokenStatus.get(dto.getGstin()).toString());
		dto.setState(stateNames.get(dto.getGstin()).toString());
		dto.setRegType(regMap.get(dto.getGstin()).toString());

		return dto;
	}

}
