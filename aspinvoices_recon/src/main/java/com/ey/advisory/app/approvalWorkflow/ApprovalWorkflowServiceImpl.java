package com.ey.advisory.app.approvalWorkflow;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.UserCreationRepository;
import com.ey.advisory.app.data.entities.client.asprecon.ApprovalMappingEntity;
import com.ey.advisory.app.data.entities.client.asprecon.ApprovalPreferenceChildEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.ApprovalMappingRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ApprovalPreferenceRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("ApprovalWorkflowServiceImpl")
public class ApprovalWorkflowServiceImpl implements ApprovalWorkflowService {

	@Autowired
	private GSTNDetailRepository gstinInfoRepo;

	@Autowired
	private ApprovalPreferenceRepository prefRepo;

	@Autowired
	private ApprovalMappingRepository approvalRepo;

	@Autowired
	private UserCreationRepository userRepo;

	@Override
	public List<ApprovalDataRespDto> getChekerMakerSummary(Long entityId,
			String returnType) {

		List<ApprovalDataRespDto> respList = new ArrayList<>();

		try {
			List<String> gstins = gstinInfoRepo.getGstinBasedOnRegType(entityId,
					GenUtil.getRegTypesBasedOnTypeForACD(returnType));

			if (gstins.isEmpty()) {
				String msg = String.format(
						"No Active Gstin's For Selected Entity Id %s and Return Type %s",
						entityId, returnType);
				LOGGER.error(msg);
				throw new AppException(msg);
			}

			// creating user info list based on entity id
			List<Object[]> userInfoList = userRepo
					.getUserInfoByEntityId(entityId);

			if (userInfoList.isEmpty()) {
				String msg = String.format(
						"No Users found for selected Entity Id %s", entityId);
				LOGGER.error(msg);
				throw new AppException(msg);
			}

			List<ApprovalEmailIdDto> userList = new ArrayList<>();

			userInfoList.forEach(mail -> {
				userList.add(new ApprovalEmailIdDto(mail[0].toString(),
						mail[1].toString()));
			});

			// Checker Maker list for gstins submitted
			List<Object[]> approvalList = approvalRepo
					.findByEntityIdAndIsDeleteFalseANDRetType(entityId,
							returnType);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("checker maker list for submitted gstins{}",
						approvalList);
			}

			if (!approvalList.isEmpty()) {
				Map<Object, List<Object[]>> approvalMap = approvalList.stream()
						.collect(Collectors.groupingBy(o -> o[0]));

				for (Map.Entry<Object, List<Object[]>> entry : approvalMap
						.entrySet()) {
					ApprovalDataRespDto respDto = new ApprovalDataRespDto(
							entry.getKey().toString(), returnType, userList);
					respDto.setPresent(true);
					List<Object[]> entity = entry.getValue();

					entity.forEach(o -> {
						String email = String.format("%s(%s)", o[2].toString(),
								o[1].toString());
						if (o[3].toString().equalsIgnoreCase("true")) {
							respDto.addSelectedMakers(email);
						} else
							respDto.addSelectedCheckers(email);
					});
					respList.add(respDto);
					gstins.remove(entry.getKey().toString());
				}
			}

			// Remaining gstin
			gstins.forEach(gstn -> {
				ApprovalDataRespDto respDto = new ApprovalDataRespDto(gstn,
						returnType, userList);
				respList.add(respDto);
			});

		} catch (Exception ex) {
			if (LOGGER.isDebugEnabled()) {
				String msg = "ApprovalWorkflowServiceImpl"
						+ ".getChekerMakerSummary ended with exception";
				LOGGER.debug(msg, ex);
			}
			throw new AppException(ex);
		}
		
		respList.sort(Comparator
				.comparing(ApprovalDataRespDto::getGstin));
		
		return respList;
	}

	@Override
	public String createChekerMakerdetails(ApprovalSubmitChecMakerDto reqDto, String userName) {

		List<ApprovalDataRespDto> chekMakerList = null;
		try {

			// getting the list of submit checker maker list
			chekMakerList = reqDto.getGstinInfo();
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("checker maker list submitted for{}",
						chekMakerList);
			}

			// iterating and checking whether the record is present or not
			List<ApprovalDataRespDto> chdPrtList = new ArrayList<>();
			chekMakerList.forEach(o -> {
				if (o.isPresent()) {
					// This block will be executed for the existing records hence 
					// we soft delete the existing records before creating a new records
					approvalRepo.softDeleteExistingEntries(
							reqDto.getEntityId(), o.getGstin(),
							userName, reqDto.getRetType());
				}
				chdPrtList.add(o);
			});

			convertAndPersistTheEntity(chdPrtList, reqDto.getEntityId(),
					userName,reqDto.getRetType());
			return "SUCCESS";
		} catch (Exception e) {
			LOGGER.error("error", e);
			throw new AppException(e);
		}

	}

	private void convertAndPersistTheEntity(List<ApprovalDataRespDto> gstinList,
			Long entityId, String userName, String retType)
	{
		List<ApprovalPreferenceChildEntity> finalCheckerMakerList = new ArrayList<>();
		gstinList.forEach(o -> {

			ApprovalMappingEntity obj = new ApprovalMappingEntity();
			obj.setEntityId(entityId);
			obj.setGstin(o.getGstin());
			obj.setCreatedOn(LocalDateTime.now());
			obj.setCreatedBy(userName);
			obj.setRetType(retType);

			ApprovalMappingEntity saveObj = approvalRepo.save(obj);

			finalCheckerMakerList.addAll(convertToChdEntity(
					o.getSelectedMakers(), true, saveObj.getId()));
			finalCheckerMakerList.addAll(convertToChdEntity(
					o.getSelectedCheckers(), false, saveObj.getId()));
		});

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("checker maker list saved{}", gstinList.size());
		}
		prefRepo.saveAll(finalCheckerMakerList);

	}

	private List<ApprovalPreferenceChildEntity> convertToChdEntity(
			List<String> selUserInfo, boolean flag, Long id) {
		List<ApprovalPreferenceChildEntity> chldEntity = new ArrayList<>();

		selUserInfo.forEach(chdEntity -> {
			ApprovalPreferenceChildEntity entity = new ApprovalPreferenceChildEntity();
			entity.setMappingId(id);

			int index = chdEntity.indexOf('(');
			entity.setEmail(chdEntity.substring(0, index));
			entity.setName(
					chdEntity.substring(index + 1, chdEntity.length() - 1));
			entity.setIsMaker(flag);
			chldEntity.add(entity);

		});
		return chldEntity;
	}
}
