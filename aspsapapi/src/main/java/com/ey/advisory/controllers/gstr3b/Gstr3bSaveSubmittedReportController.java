package com.ey.advisory.controllers.gstr3b;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.Workbook;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.gstr3b.Gstr3bSaveSubmitReportService;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun KA
 *
 */

@RestController
@Slf4j
public class Gstr3bSaveSubmittedReportController {
	
	@Autowired
	private GSTNDetailRepository gstinDetailRepo;
	
	@Autowired
	@Qualifier("Gstr3bSaveSubmitReportServiceImpl")
	Gstr3bSaveSubmitReportService gstr3bSaveSubmitReportService;
	
	@GetMapping(value = "/ui/getGstr3bSaveSubmitReportDownload", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr3BEntityReportDownload(
			HttpServletRequest request, HttpServletResponse response) {
		JsonObject errorResp = new JsonObject();
		List<String> gstnsList = new ArrayList<>();
		Workbook workbook = null;

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE Gstr3BSaveSubmit Report Controller");
			}

			String taxPeriod = request.getParameter("taxPeriod");
			String gstin = request.getParameter("gstin");
			
			
			if (gstin == null) {
			String msg = "Gstins cannot be empty";
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.OK);
			}
			
			//gstnsList.add("29AAAPH9357H000");
		
		/*workbook = gstr3bSaveSubmitReportService
				.getGstr3bSaveReportData(taxPeriod, gstin);*/
		
		/*if (workbook != null) {
			response.setContentType("APPLICATION/OCTET-STREAM");
			response.setHeader("Content-Disposition",
					String.format("attachment; filename = "
			+"Gstr3bGstinReport.xlsx"));
			workbook.save(response.getOutputStream(), SaveFormat.XLSX);
		}*/
		
		return new ResponseEntity<>("Success", HttpStatus.OK);
		
	} catch (Exception ex) {
		String msg = "Unexpected error while retriving Data Status ";
		LOGGER.error(msg, ex);
		errorResp.add("hdr",
				new Gson().toJsonTree(new APIRespDto("E", msg)));
		return new ResponseEntity<>(errorResp.toString(),
				HttpStatus.INTERNAL_SERVER_ERROR);

	}
	}

}
