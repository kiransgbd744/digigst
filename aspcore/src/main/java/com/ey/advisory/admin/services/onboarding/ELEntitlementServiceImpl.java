package com.ey.advisory.admin.services.onboarding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ey.advisory.admin.data.entities.client.ELEntitlementEntity;
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.ELEntitlementRepository;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.GroupInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.MasterFunctionalityRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.dto.ELEntitlementDto;
import com.ey.advisory.core.dto.ElEntitlementHistoryReqDto;
import com.ey.advisory.core.dto.ElEntitlementReqDto;
import com.ey.advisory.core.dto.Messages;

/**
 * @author Umesha.M
 *
 */
@Component("elEntitlementService")
public class ELEntitlementServiceImpl implements ELEntitlementService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ELEntitlementServiceImpl.class);
	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityInfoDetailsRepository;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstNDetailRepository;

	@Autowired
	@Qualifier("elEntitlementRepository")
	private ELEntitlementRepository elEntitlementRepository;

	@Autowired
	@Qualifier("masterFunctionalityRepository")
	private MasterFunctionalityRepository masterFunctionalityRepository;

	@Autowired
	@Qualifier("groupInfoDetailsRepository")
	GroupInfoDetailsRepository groupInfoDetailsRepository;

	private static final String FARMATTER = "yyyy-MM-dd";

	/**
	 * 
	 */
	public List<ELEntitlementDto> getLatestElEntDetails(
			ElEntitlementReqDto dto) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"ELEntitlementServiceImpl getLatestElEntDetails BEGIN");
		}
		List<ELEntitlementDto> elEntitlementDtos = new ArrayList<>();
		List<EntityInfoEntity> entityInfoEntities = entityInfoDetailsRepository
				.findEntityInfoDetails(dto.getGroupCode());
		if (!entityInfoEntities.isEmpty()) {
			entityInfoEntities.forEach(entityInfoEntity -> {
				ELEntitlementDto elEntitlementDto = new ELEntitlementDto();
				elEntitlementDto.setEntityId(entityInfoEntity.getId());
				elEntitlementDto
						.setEntityName(entityInfoEntity.getEntityName());
				elEntitlementDto.setGroupCode(entityInfoEntity.getGroupCode());
				elEntitlementDto.setPan(entityInfoEntity.getPan());
				elEntitlementDto
						.setCompanyCode(entityInfoEntity.getCompanyHq());
				List<ELEntitlementEntity> elEntitlementEntities = elEntitlementRepository
						.findAllEntitlementdetails(dto.getGroupCode(),
								entityInfoEntity.getId());
				elEntitlementEntities.forEach(elEntitlementEntity -> {
					elEntitlementDto.setElId(elEntitlementEntity.getElId());

					List<String> gstnList = getGSTNDetails(dto.getGroupCode(),
							entityInfoEntity.getId());
					if (!gstnList.isEmpty()) {
						elEntitlementDto.setGstinList(gstnList);
					}
					getGSTNDetails(dto.getGroupCode(),
							entityInfoEntity.getId());
					String[] arrayFuncCode = elEntitlementEntity
							.getFunctionalityCode().split(",");
					List<String> functionality = Arrays.asList(arrayFuncCode);
					elEntitlementDto.setFunctionality(functionality);

					elEntitlementDto.setFromTaxPeriod(
							elEntitlementEntity.getFromTaxPeriod());
					elEntitlementDto.setToTaxPeriod(
							elEntitlementEntity.getToTaxPeriod());
					elEntitlementDto.setContractStartPeriod(
							elEntitlementEntity.getContractStartPeriod());
					elEntitlementDto.setContractEndPeriod(
							elEntitlementEntity.getContractEndPeriod());
					elEntitlementDto
							.setElValue(elEntitlementEntity.getElValue());
					elEntitlementDto
							.setRenewal(elEntitlementEntity.getRenewal());
					elEntitlementDto.setGfisId(elEntitlementEntity.getGfisId());
					elEntitlementDto.setPaceId(elEntitlementEntity.getPaceId());
				});
				elEntitlementDtos.add(elEntitlementDto);
			});
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ELEntitlementServiceImpl getLatestElEntDetails END");
		}
		return elEntitlementDtos;
	}

	public List<ELEntitlementDto> getEleEntitlementHistory(
			ElEntitlementHistoryReqDto dto) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"ELEntitlementServiceImpl getEleEntitlementHistory BEGIN");
		}
		List<ELEntitlementDto> elEntitlementDtos = new ArrayList<>();

		LocalDate fromDate = dto.getFromUpdateDate();
		LocalDate toDate = dto.getToUpdateDate();

		List<ELEntitlementEntity> elEntitlementEntities = elEntitlementRepository
				.getELEntitlementHistory(dto.getEntityId(), dto.getElValue(),
						fromDate, toDate);
		if (!elEntitlementEntities.isEmpty()) {
			elEntitlementEntities.forEach(elEntitlementEntity -> {
				ELEntitlementDto elEntitlementDto = new ELEntitlementDto();
				elEntitlementDto.setElId(elEntitlementEntity.getElId());
				elEntitlementDto.setEntityId(dto.getEntityId());
				EntityInfoEntity entity = entityInfoDetailsRepository
						.findEntityByEntityId(dto.getEntityId());
				elEntitlementDto.setEntityName(entity.getEntityName());
				elEntitlementDto.setGroupCode(entity.getGroupCode());
				elEntitlementDto.setPan(entity.getPan());
				elEntitlementDto.setCompanyCode(entity.getCompanyHq());
				List<String> gstnList = getGSTNDetails(dto.getGroupCode(),
						entity.getId());
				if (!gstnList.isEmpty()) {
					elEntitlementDto.setGstinList(gstnList);
				}
				String[] arrayFuncCode = elEntitlementEntity
						.getFunctionalityCode().split(",");
				List<String> functionality = Arrays.asList(arrayFuncCode);
				elEntitlementDto.setFunctionality(functionality);
				elEntitlementDto.setFromTaxPeriod(
						elEntitlementEntity.getFromTaxPeriod());
				elEntitlementDto
						.setToTaxPeriod(elEntitlementEntity.getToTaxPeriod());
				elEntitlementDto.setContractStartPeriod(
						elEntitlementEntity.getContractStartPeriod());
				elEntitlementDto.setContractEndPeriod(
						elEntitlementEntity.getContractEndPeriod());
				elEntitlementDto.setElValue(elEntitlementEntity.getElValue());
				elEntitlementDto.setRenewal(elEntitlementEntity.getRenewal());
				elEntitlementDto.setGfisId(elEntitlementEntity.getGfisId());
				elEntitlementDto.setPaceId(elEntitlementEntity.getPaceId());
				elEntitlementDto
						.setUpdateDate(elEntitlementEntity.getModifiedOn());
				elEntitlementDtos.add(elEntitlementDto);
			});
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"ELEntitlementServiceImpl getEleEntitlementHistory END");
		}
		return elEntitlementDtos;
	}

	private List<String> getGSTNDetails(String groupCode, Long entityId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ELEntitlementServiceImpl getGSTNDetails BEGIN");
		}
		List<GSTNDetailEntity> gstnDetailEntities = gstNDetailRepository
				.findByGstnAll(groupCode, entityId);
		List<String> gstnList = new ArrayList<>();
		if (!gstnDetailEntities.isEmpty()) {
			gstnDetailEntities.forEach(gstnDetailEntity -> gstnList
					.add(gstnDetailEntity.getGstin()));
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ELEntitlementServiceImpl getGSTNDetails END");
		}
		return gstnList;
	}

	@SuppressWarnings("unused")
	@Override
	public Messages updateLatestElEntDetails(
			final List<ElEntitlementReqDto> elEntitlementReqDtos) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"ELEntitlementServiceImpl updateLatestElEntDetails BEGIN");
		}
		List<String> errorMsgs = null;
		Messages mesg = new Messages();
		List<ELEntitlementEntity> list = new ArrayList<>();
		List<Long> ids = new ArrayList<>();

		for (ElEntitlementReqDto elEntitlementReqDto : elEntitlementReqDtos) {
			errorMsgs = new ArrayList<>();
			ELEntitlementEntity elEntitlementEntity = new ELEntitlementEntity();

			// update Old value isflag = true
			ids.add(elEntitlementReqDto.getElId());

			elEntitlementEntity.setEntityId(elEntitlementReqDto.getEntityId());
			Long groupId = groupInfoDetailsRepository
					.findByGroupId(elEntitlementReqDto.getGroupCode());
			elEntitlementEntity.setGroupId(groupId);
			elEntitlementEntity
					.setGroupCode(elEntitlementReqDto.getGroupCode());
			List<String> functionalityList = elEntitlementReqDto
					.getFunctionality();
			StringBuilder funcCode = new StringBuilder();
			functionalityList.forEach(functionality -> {
				funcCode.append(functionality);
				funcCode.append(",");
			});
			elEntitlementEntity.setFunctionalityCode(funcCode.toString());
			SimpleDateFormat uiDateFormat = new SimpleDateFormat("MMyyyy");
			SimpleDateFormat dbDateFormat = new SimpleDateFormat(FARMATTER);
			Calendar fromCalendar = Calendar.getInstance();
			Calendar toCalendar = Calendar.getInstance();
			Date startDate = null;
			Date endDate = null;
			try {
				fromCalendar.setTime(uiDateFormat
						.parse(elEntitlementReqDto.getFromTaxPeriod()));
				String fromTaxPeriod = dbDateFormat
						.format(fromCalendar.getTime());
				startDate = new SimpleDateFormat(FARMATTER)
						.parse(fromTaxPeriod);
				toCalendar.setTime(uiDateFormat
						.parse(elEntitlementReqDto.getToTaxPeriod()));
				String toTaxPeriod = dbDateFormat.format(toCalendar.getTime());
				endDate = new SimpleDateFormat(FARMATTER).parse(toTaxPeriod);

			} catch (ParseException e) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Error parsing date range", e);
				}
			}
			elEntitlementEntity
					.setFromTaxPeriod(elEntitlementReqDto.getFromTaxPeriod());
			if (startDate != null && endDate != null
					&& startDate.after(endDate)) {
				errorMsgs.add("To Tax Period Should be After From Tax period");
			} else {
				elEntitlementEntity
						.setToTaxPeriod(elEntitlementReqDto.getToTaxPeriod());
			}
			if (null != elEntitlementReqDto.getElValue()
					&& !StringUtils.isEmpty(elEntitlementReqDto.getElValue())) {
				elEntitlementEntity
						.setElValue(elEntitlementReqDto.getElValue());
			} else {
				errorMsgs.add("El Value cannot be empty");
			}
			elEntitlementEntity.setContractStartPeriod(
					elEntitlementReqDto.getContractStartPeriod());

			Calendar startPeriodCalendar = Calendar.getInstance();
			Calendar endPeriodCalendar = Calendar.getInstance();
			Date startPeriodDate = null;
			Date endPeriodDate = null;
			try {
				startPeriodCalendar.setTime(uiDateFormat
						.parse(elEntitlementReqDto.getContractStartPeriod()));
				String startPeriod = dbDateFormat
						.format(startPeriodCalendar.getTime());
				startPeriodDate = new SimpleDateFormat(FARMATTER)
						.parse(startPeriod);
				endPeriodCalendar.setTime(uiDateFormat
						.parse(elEntitlementReqDto.getContractEndPeriod()));
				String endPeriod = dbDateFormat
						.format(endPeriodCalendar.getTime());
				endPeriodDate = new SimpleDateFormat(FARMATTER)
						.parse(endPeriod);

			} catch (ParseException e) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Error parsing date range", e);
				}
			}

			if (null != startPeriodDate && null != endPeriodDate
					&& startPeriodDate.after(endPeriodDate)) {
				errorMsgs.add(
						"Contract End Period Should be After Contract Start period");
			} else {
				elEntitlementEntity.setContractEndPeriod(
						elEntitlementReqDto.getContractEndPeriod());
			}
			if (null == elEntitlementReqDto.getRenewal()
					&& StringUtils.isEmpty(elEntitlementReqDto.getRenewal())) {
				errorMsgs.add("Renewal can not be empty");
			} else {
				elEntitlementEntity
						.setRenewal(elEntitlementReqDto.getRenewal());
			}
			if (null == elEntitlementReqDto.getGfisId()
					&& StringUtils.isEmpty(elEntitlementReqDto.getGfisId())) {
				errorMsgs.add("Gfis Id can not be empty");
			} else {
				elEntitlementEntity.setGfisId(elEntitlementReqDto.getGfisId());
			}
			if (null == elEntitlementReqDto.getPaceId()
					&& StringUtils.isEmpty(elEntitlementReqDto.getPaceId())) {
				errorMsgs.add("Pace Id can not be empty");
			} else {
				elEntitlementEntity.setPaceId(elEntitlementReqDto.getPaceId());
			}
			// ToDo Change the null value to name of the Logged in user
			// This to be done by Priyanka
			User user = SecurityContext.getUser();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("User Name: {}", user.getUserPrincipalName());
			}
			elEntitlementEntity.setModifiedBy(user.getUserPrincipalName());
			LocalDate localDate = EYDateUtil
					.toLocalDateTimeFromUTC(LocalDate.now());
			elEntitlementEntity.setModifiedOn(localDate);
			elEntitlementEntity.setIsDelete(false);
			list.add(elEntitlementEntity);
		}
		if (errorMsgs != null && !errorMsgs.isEmpty()) {
			mesg.setMessages(errorMsgs);
		} else {
			if (ids != null && !ids.isEmpty()) {
				elEntitlementRepository.updateEntitlement(ids);
			}
			if (list != null && !list.isEmpty()) {
				elEntitlementRepository.saveAll(list);
			}

		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"ELEntitlementServiceImpl updateLatestElEntDetails END");
		}
		return mesg;
	}

}