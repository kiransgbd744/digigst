package com.ey.advisory.app.serviceoption;

import java.util.List;

import com.ey.advisory.core.dto.ServiceOptionDto;
import com.ey.advisory.core.dto.ServiceOptionReqDto;

public interface ServiceOptionService {

	public List<ServiceOptionDto> getServiceOption(final ServiceOptionReqDto servOptReqDto);

	public void saveServiceOption(final List<ServiceOptionReqDto> servOptReqDtos);
}
