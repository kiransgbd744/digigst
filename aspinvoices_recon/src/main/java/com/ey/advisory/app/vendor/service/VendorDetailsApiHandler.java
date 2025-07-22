/**
 * 
 */
package com.ey.advisory.app.vendor.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.asprecon.VendorValidatorPayloadRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GstnReturnFilingStatus;
import com.ey.advisory.core.dto.ReturnFilingGstnResponseDto;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */

@Slf4j
@Service("VendorDetailsApiHandler")
public class VendorDetailsApiHandler {

	@Autowired
	private GstnReturnFilingStatus service;

	@Autowired
	@Qualifier("VendorValidatorPayloadRepository")
	private VendorValidatorPayloadRepository payloadRepository;

	public void fetchFillingDetails(String payloadId, String fy,
			List<String> gstins) {

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Inside VendorDetailsApiHandler about to call"
						+ " callGstnApi paylaodId {}, gstins {}, fy {} ",
						payloadId, gstins, fy);
			}

			List<ReturnFilingGstnResponseDto> respList1 = service
					.callGstnApi(gstins, fy, true);

			List<String> errorCode = new ArrayList<>();
			List<String> errorMsg = new ArrayList<>();
			for (ReturnFilingGstnResponseDto list : respList1) {

				errorCode.add(list.getErrCode());
				errorMsg.add(list.getErrMsg());

			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("after calling callGstnApi paylaodId {}, "
						+ " gstins {}, fy {}, errorCode {},  errorMsg {} ",
						payloadId, gstins, fy, errorCode, errorMsg);
			}

			if (!errorCode.isEmpty()) {
				String errCode = Strings.join(errorCode, ',');
				String errMsg = Strings.join(errorMsg, ',');
				payloadRepository.updateFillingDetailsErrorStatus(payloadId,
						LocalDateTime.now(),
						StringUtils.truncate(errCode, 1000),
						StringUtils.truncate(errMsg, 1000));
			}

			String nextFy = getNextFiscalYearIfNotCurrent(fy);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("nextFy is {} , gstins {}, fy {} ", nextFy, gstins,
						fy);
			}

			if (nextFy != null) {
				List<ReturnFilingGstnResponseDto> respList = service
						.callGstnApi(gstins, nextFy, true);

				List<String> errorCode1 = new ArrayList<>();
				List<String> errorMsg1 = new ArrayList<>();
				for (ReturnFilingGstnResponseDto list : respList) {

					errorCode1.add(list.getErrCode());
					errorMsg1.add(list.getErrMsg());

				}

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("after calling callGstnApi paylaodId {}, "
							+ " gstins {}, nextFy {}, errorCode {},  errorMsg {} ",
							payloadId, gstins, nextFy, errorCode1, errorMsg1);
				}

				if (!errorCode1.isEmpty()) {
					String errCode = Strings.join(errorCode1, ',');
					String errMsg = Strings.join(errorMsg1, ',');
					payloadRepository.updateFillingDetailsErrorStatus(payloadId,
							LocalDateTime.now(),
							StringUtils.truncate(errCode, 1000),
							StringUtils.truncate(errMsg, 1000));
				}
			}

			
		} catch (Exception ex) {

			LOGGER.error("Error Occured in VendorDetailsApiHandler "
					+ "wheile calling GstnReturnFilingStatus {} ", ex);

			String errorCode = (ex instanceof AppException)
					? ((AppException) ex).getErrCode()
					: "ER8888";
			String errorMsg = "ERP (run time error) -" + ex.getMessage();
			errorCode = (errorCode == null) ? "ER8887" : errorCode;

			payloadRepository.updateFillingDetailsErrorStatus(payloadId,
					LocalDateTime.now(),
					errorCode.length() > 1000 ? errorCode.substring(0, 1000)
							: errorCode,
					errorMsg.length() > 1000 ? errorMsg.substring(0, 1000)
							: errorMsg);

			throw new AppException(ex);
		}

	}

	private String getNextFiscalYearIfNotCurrent(String fiscalYear) {
		int startYear = Integer.parseInt(fiscalYear.split("-")[0]);
		LocalDate currentDate = LocalDate.now();
		int currentFiscalYear = getFiscalYear(currentDate);

		if (startYear != currentFiscalYear) {
			int nextFiscalYearStart = startYear + 1;
			int nextFiscalYearEnd = nextFiscalYearStart + 1;
			return nextFiscalYearStart + "-" + (nextFiscalYearEnd % 100);
		}

		return null; // or throw an exception if needed
	}

	private int getFiscalYear(LocalDate date) {
		int year = date.getYear();
		// Assuming fiscal year starts in April
		if (date.getMonthValue() >= Month.APRIL.getValue()) {
			return year;
		} else {
			return year - 1;
		}
	}

}
