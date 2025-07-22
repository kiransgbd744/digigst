package com.ey.advisory.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.services.ledger.GetCashLedgerDetails;
import com.ey.advisory.app.util.Anx1CsvDownloadUtil;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.core.config.ConfigConstants;
import com.google.gson.Gson;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class GetRcmLedgerDetailsController {

	@Autowired
	@Qualifier("getCashLedgerDetailsImpl")
	private GetCashLedgerDetails getCash;

	@Autowired
	@Qualifier("Anx1CsvDownloadUtil")
	private Anx1CsvDownloadUtil anx1CsvDownloadUtil;

	@RequestMapping(value = "/ui/getRCMDetailedScnExcelReport", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void getRCMDetailedScnExcelReport(@RequestBody String jsonReq,
			HttpServletResponse response) throws IOException {
		Gson gson = GsonUtil.newSAPGsonInstance();
		LOGGER.debug(
				"inside controller calling GetRcmLedgerDetailsController method  with arg {}",
				jsonReq);
		Workbook workbook = getCash.rcmDetailscnExcelRpt(jsonReq);
		String fileName = DocumentUtility.getUniqueFileName(
				ConfigConstants.RCMLedger_Report);
		if (workbook != null) {
			response.setContentType("APPLICATION/OCTET-STREAM");
			response.setHeader("Content-Disposition",
					String.format("attachment; filename=" + fileName));
			try {
				workbook.save(response.getOutputStream(), SaveFormat.XLSX);
			} catch (Exception e) {
				String msg = String.format(
						"Exception occured while "
								+ "generating the excel sheet, %s ",
						e.getMessage());
				LOGGER.error(msg);
				throw new AppException(e.getMessage(), e);
			}
			response.getOutputStream().flush();
		}

	}

	// negetive ledger
	@RequestMapping(value = "/ui/getNegativeLedgerDetailscnExcelReport", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void getNegativeLedgerDetailscnExcelReport(
			@RequestBody String jsonReq,
			HttpServletResponse response) throws IOException {
		Gson gson = GsonUtil.newSAPGsonInstance();
		LOGGER.debug(
				"inside controller calling getNegativeLedgerDetailscnExcelReport method  with arg {}",
				jsonReq);
		Workbook workbook = getCash.negativeLedgerDetailscnExcelRpt(jsonReq);
		String fileName = DocumentUtility.getUniqueFileName(
				ConfigConstants.NegativeLiabilityLedger_Report);
		if (workbook != null) {
			response.setContentType("APPLICATION/OCTET-STREAM");
			response.setHeader("Content-Disposition",
					String.format("attachment; filename=" + fileName));
			try {
				workbook.save(response.getOutputStream(), SaveFormat.XLSX);
			} catch (Exception e) {
				String msg = String.format(
						"Exception occured while "
								+ "generating the excel sheet, %s ",
						e.getMessage());
				LOGGER.error(msg);
				throw new AppException(e.getMessage(), e);
			}
			response.getOutputStream().flush();
		}

	}

}
