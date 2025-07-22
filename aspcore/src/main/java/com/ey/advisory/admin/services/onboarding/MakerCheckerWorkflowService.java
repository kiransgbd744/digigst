package com.ey.advisory.admin.services.onboarding;

import java.util.List;

import com.ey.advisory.core.dto.MakerCheWorkFlowFinalRespDto;
import com.ey.advisory.core.dto.MakerCheckerReqDto;
import com.ey.advisory.core.dto.MakerCheckerWorkflowRespDto;

public interface MakerCheckerWorkflowService {

	public List<MakerCheckerWorkflowRespDto> getMakerCheckerWorkflowDetails(
			Long entityId);

	public void updateMkrCkrMapEntity(List<MakerCheckerReqDto> reqDtos);

	public MakerCheWorkFlowFinalRespDto getMkrCkrWorkFlow(Long entityId);

	public void updateMkrCkrWorkFlow(List<MakerCheckerReqDto> reqDtos);
}
