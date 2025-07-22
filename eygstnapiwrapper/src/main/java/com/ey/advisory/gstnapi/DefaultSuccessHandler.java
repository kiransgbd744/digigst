package com.ey.advisory.gstnapi;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.StaticContextHolder;

import lombok.extern.slf4j.Slf4j;

@Component("DefaultSuccessHandler")
@Slf4j
public class DefaultSuccessHandler implements SuccessHandlerWrapper {


	@Override
	public void handleSuccess(String successHandler, SuccessResult result,
			String apiParams) {
		
		GstinGetStatusService gstinGetStatusService = StaticContextHolder.getBean(
				"GstinGetStatusServiceImpl", GstinGetStatusService.class);
		
		gstinGetStatusService.uploadJsonFileToRepoForGSTN(result, apiParams);

		StaticContextHolder.getBean(successHandler, SuccessHandler.class)
				.handleSuccess(result, apiParams);

	}

}
