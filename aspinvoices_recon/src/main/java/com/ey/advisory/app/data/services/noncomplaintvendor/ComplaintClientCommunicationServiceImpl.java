package com.ey.advisory.app.data.services.noncomplaintvendor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GstnReturnFilingStatus;
import com.ey.advisory.core.dto.ReturnFilingGstnResponseDto;

import lombok.extern.slf4j.Slf4j;

@Service("ComplaintClientCommunicationServiceImpl")
@Slf4j
public class ComplaintClientCommunicationServiceImpl {
	@Autowired
	private GstnReturnFilingStatus gstnReturnFiling;

	public void persistGstnApiForSelectedFinancialYear(String finYear,
			String gstin) {
		try {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(
						"inside the method of persistGstnApiForSelectedFinancialYear "
								+ "of class "
								+ "ComplaintClientCommunicationServiceImpl "
								+ "and selected year:{}",
						finYear);
			List<ReturnFilingGstnResponseDto> retFilingList = gstnReturnFiling
					.callGstnApi(Arrays.asList(gstin), finYear, false);
			List<ReturnFilingGstnResponseDto> successRespList = retFilingList
					.stream().filter(dto -> dto.getStatus() != null)
					.collect(Collectors.toList());
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(
						"About to persist Counter party Return Filling status "
								+ "sanbox response data into table"
								+ retFilingList);
			gstnReturnFiling.persistReturnFillingStatus(successRespList, false);

		} catch (Exception e) {
			String errMsg = String.format(
					"Error while saving the return filling status of client for FY %s",
					finYear);
			LOGGER.error(errMsg, e);
			throw new AppException(errMsg);
		}
	}

}
