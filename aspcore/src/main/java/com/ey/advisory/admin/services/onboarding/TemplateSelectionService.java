package com.ey.advisory.admin.services.onboarding;

import java.util.List;

import com.ey.advisory.core.dto.BankDetailsReqDto;
import com.ey.advisory.core.dto.BankDetailsRespDto;
import com.ey.advisory.core.dto.TemplateSelectionReqDto;
import com.ey.advisory.core.dto.TemplateSelectionRespDto;

public interface TemplateSelectionService {

	public List<TemplateSelectionRespDto> getTemplateSelection(
			final TemplateSelectionReqDto tempSelcReqDto);

	public void saveTemplateSelection(final TemplateSelectionReqDto reqDto);

	public List<BankDetailsRespDto> getBankDetails(
			final BankDetailsReqDto reqDto);

	public void saveBankDetails(final List<BankDetailsReqDto> reqDtos);
}
