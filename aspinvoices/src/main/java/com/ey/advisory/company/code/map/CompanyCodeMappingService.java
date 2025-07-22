package com.ey.advisory.company.code.map;

import java.util.List;

public interface CompanyCodeMappingService {

	public List<CompanyCodeMappingRespDto> getCompanyCodeMapping(
			final CompanyCodeMappingReqDto reqDto);

	public void saveCompanyCodeMapping(
			final List<CompanyCodeMappingReqDto> reqDtos);
}
