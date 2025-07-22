package com.ey.advisory.app.processors.handler;

import org.springframework.stereotype.Service;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Service("WorkFlowPersistanceHandler")
public class WorkFlowPersistanceHandler {/*

	@Autowired
	private MakerCheckerWorkFlowRepository makerCheckerWorkFlowRepo;

	@Autowired
	private RequestStatusGstinLevelRepository gstinApprovalRepo;

	@Autowired
	private RequestForApprovalRepository requestForApprovalRepo;

	@Autowired
	private RequestStatusCheckerRepository checkerApprovalRepo;

	public List<WorkFlowRespDto> getApprovalHistory(WorkFlowReqDto dto) {

		List<WorkFlowRespDto> respDtos = new ArrayList<>();
		WorkFlowRespDto respDto = new WorkFlowRespDto();
		List<Object[]> objArray = requestForApprovalRepo.findRequestDetails(
				dto.getEntityId(), dto.getGstin(), dto.getReturnPeriod(),
				dto.getWorkflowType());

		if (objArray != null) {
			objArray.forEach(object -> {

				respDto.setEntityId(dto.getEntityId());
				respDto.setReturnPeriod(dto.getReturnPeriod());
				respDto.setGstin(dto.getGstin());
				respDto.setRequestId(object[0] != null
						? Long.parseLong(String.valueOf(object[0])) : null);
				respDto.setRequestedOn(object[1] != null
						? LocalDateTime.parse(String.valueOf(object[1]))
						: null);
				respDto.setCheckerId(
						object[2] != null ? String.valueOf(object[2]) : null);
				respDto.setStatus(
						object[3] != null ? String.valueOf(object[3]) : null);
				respDto.setResponseComments(
						object[4] != null ? String.valueOf(object[4]) : null);
				respDto.setRespondedOn(object[5] != null
						? LocalDateTime.parse(String.valueOf(object[5]))
						: null);

				respDtos.add(respDto);

			});
		}

		return respDtos;

	}

	public void saveApprovalDetails(WorkFlowReqDto dto) {

		String userName = SecurityContext.getUser().getUserPrincipalName();
		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());

		RequestStatusGstinLevelEntity gstinLevelEntity = new RequestStatusGstinLevelEntity();

		gstinLevelEntity.setGstin(dto.getGstin());
		gstinLevelEntity.setReturnPeriod(dto.getReturnPeriod());
		gstinLevelEntity.setCreatedBy(userName);
		gstinLevelEntity.setCreatedOn(now);
		gstinLevelEntity.setDelete(false);

		gstinLevelEntity = gstinApprovalRepo.save(gstinLevelEntity);

		RequestForApprovalEntity reqEntity = new RequestForApprovalEntity();
		reqEntity.setEntityId(dto.getEntityId());
		reqEntity.setGstin(dto.getGstin());
		reqEntity.setReturnPeriod(dto.getReturnPeriod());
		reqEntity.setWorkFlowType(dto.getWorkflowType());
		reqEntity.setMakerId(dto.getMackerId());
		reqEntity.setMakerComments(dto.getRequestComments());
		reqEntity.setRequestTime(now);
		reqEntity.setCreatedOn(now);
		reqEntity.setCreatedBy(userName);
		reqEntity.setDelete(false);

		reqEntity = requestForApprovalRepo.save(reqEntity);

		List<RequestStatusCheckerEntity> checkerLevelEntities = new ArrayList<>();
		for (String checkerId : dto.getCheckerIds()) {
			RequestStatusCheckerEntity checkerLevelEntity = new RequestStatusCheckerEntity();
			checkerLevelEntity.setRequestId(reqEntity.getId());
			checkerLevelEntity.setCheckerId(checkerId);
			checkerLevelEntity.setCreatedOn(now);
			checkerLevelEntity.setCreatedBy(userName);
			checkerLevelEntity.setDelete(false);
			checkerLevelEntities.add(checkerLevelEntity);

		}
		checkerApprovalRepo.saveAll(checkerLevelEntities);

	}

	public List<String> findCheckersBy(WorkFlowReqDto dto) {

		List<String> checkers = makerCheckerWorkFlowRepo
				.findAllCheckersByGstinAndWorkFlowType(dto.getEntityId(),
						dto.getGstin(), dto.getWorkflowType(),
						dto.getMackerId());

		return checkers;
	}
*/}
