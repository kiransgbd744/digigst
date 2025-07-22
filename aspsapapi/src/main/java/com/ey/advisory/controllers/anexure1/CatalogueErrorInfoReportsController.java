/**
 * 
 */
package com.ey.advisory.controllers.anexure1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.Anx1CatalogErrInfoReportsReqDto;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.services.reports.Anx1OutwardErrInfoCatalogHandler;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Laxmi.Salukuti
 *
 */
@RestController
public class CatalogueErrorInfoReportsController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CatalogueErrorInfoReportsController.class);

	@Autowired
	@Qualifier("Anx1OutwardErrInfoCatalogHandler")
	private Anx1OutwardErrInfoCatalogHandler anx1OutwardErrInfoCatalogHandler;

	@RequestMapping(value = "/ui/errorInfoCatalog", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadErrInfoCatalogueReport(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Begining from downloadErrInfoCatalogueReport request : {}",
					jsonString);
		}
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			String fileName = null;
			Workbook workbook = null;
			Anx1CatalogErrInfoReportsReqDto criteria = gson.fromJson(json,
					Anx1CatalogErrInfoReportsReqDto.class);

			if (criteria.getDataType() != null
					&& !criteria.getDataType().isEmpty()) {
				if ((criteria.getDataType() != null && criteria.getDataType()
						.equalsIgnoreCase(DownloadReportsConstant.OUTWARD))) {
					if (criteria.getType() != null && criteria.getType()
							.equalsIgnoreCase(DownloadReportsConstant.ERR)) {
						workbook = anx1OutwardErrInfoCatalogHandler
								.downloadErrInfoCataloag(criteria);
						fileName = "Anx1OutwardErrorCatalogue";
					} else if (criteria.getType() != null && criteria.getType()
							.equalsIgnoreCase(DownloadReportsConstant.INFO)) {
						workbook = anx1OutwardErrInfoCatalogHandler
								.downloadErrInfoCataloag(criteria);
						fileName = "Anx1OutwardInfoCatalogue";
					}
				}
				if (criteria.getDataType() != null && criteria.getDataType()
						.equalsIgnoreCase(DownloadReportsConstant.INWARD)) {
					if (criteria.getType() != null && criteria.getType()
							.equalsIgnoreCase(DownloadReportsConstant.ERR)) {
						workbook = anx1OutwardErrInfoCatalogHandler
								.downloadErrInfoCataloag(criteria);
						fileName = "Anx1InwardErrorCatalogue";
					} else if (criteria.getType() != null && criteria.getType()
							.equalsIgnoreCase(DownloadReportsConstant.INFO)) {
						workbook = anx1OutwardErrInfoCatalogHandler
								.downloadErrInfoCataloag(criteria);
						fileName = "Anx1InwardInfoCatalogue";
					}
				}
				if (criteria.getDataType() != null && criteria.getDataType()
						.equalsIgnoreCase(DownloadReportsConstant.OTHERS)) {
					if (criteria.getType() != null && criteria.getType()
							.equalsIgnoreCase(DownloadReportsConstant.ERR)) {
						workbook = anx1OutwardErrInfoCatalogHandler
								.downloadErrInfoCataloag(criteria);
						fileName = "Anx1OthersErrorCatalogue";
					} else if (criteria.getType() != null && criteria.getType()
							.equalsIgnoreCase(DownloadReportsConstant.INFO)) {
						workbook = anx1OutwardErrInfoCatalogHandler
								.downloadErrInfoCataloag(criteria);
						fileName = "Anx1OthersInfoCatalogue";
					}
				}
			} else {
				LOGGER.error("invalid input");
				throw new Exception("invalid input");
			}
			if (workbook == null) {
				workbook = new Workbook();
			}
			if (fileName != null) {
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition", String
						.format("attachment; filename=" + fileName + ".xlsx"));
				workbook.save(response.getOutputStream(), SaveFormat.XLSX);
				response.getOutputStream().flush();
			}
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

		} catch (Exception ex) {
			String msg = "Unexpected error while retriving download report";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}

	}
}
