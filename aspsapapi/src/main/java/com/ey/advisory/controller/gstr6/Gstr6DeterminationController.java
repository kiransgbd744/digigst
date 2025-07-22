package com.ey.advisory.controller.gstr6;

import java.lang.reflect.Type;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.docs.dto.Anx1ReportSearchReqDto;
import com.ey.advisory.app.docs.dto.DocSeriesSDeleteReqDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6DeterminationResponseDto;
import com.ey.advisory.app.docs.service.gstr6.Gstr6DeterminationService;
import com.ey.advisory.app.downloadreports.DownloadReportsConstant;
import com.ey.advisory.app.search.reports.BasicCommonSecParamReports;
import com.ey.advisory.app.services.reports.Gstr6DeterminationReportHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.search.SearchResult;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@RestController
@Slf4j
public class Gstr6DeterminationController {

	@Autowired
	@Qualifier("BasicCommonSecParamReports")
	private BasicCommonSecParamReports basicCommonSecParamReports;

	@Autowired
	@Qualifier("Gstr6DeterminationServiceImpl")
	private Gstr6DeterminationService gstr6DeterminationService;

	@Autowired
	@Qualifier("Gstr6DeterminationReportHandler")
	private Gstr6DeterminationReportHandler gstr6DeterminationReportHandler;
	
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;
	
	@PostMapping(value = "/ui/gstr6Determination", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> gstr6Determination(
			@RequestBody String jsonInput) {

		LOGGER.debug("Requests for Gstr6 Determination jsonInputs {}",
				jsonInput);
		JsonObject jsonObj = new JsonParser().parse(jsonInput).getAsJsonObject()
				.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			Anx1ReportSearchReqDto criteria = gson.fromJson(jsonObj,
					Anx1ReportSearchReqDto.class);
			criteria.setDataType(GSTConstants.INWARD);
			LOGGER.debug("DataStatus Adapter Filters Setting to Request BEGIN");
			Anx1ReportSearchReqDto dataSecSearcParam = basicCommonSecParamReports
					.setDataSecuritySearchParams(criteria);
			SearchResult<Gstr6DeterminationResponseDto> gstr6Determinationvalues = gstr6DeterminationService
					.getGstr6Determinationvalues(dataSecSearcParam);

			StringBuilder build = new StringBuilder();

			List<Long> entityIds = criteria.getEntityId();
			Long entityId = entityIds.get(0);
			if (entityId != null) {
				build.append(" AND ENTITY_ID  = :entityId ");
			}

			String buildQuery = build.toString();
			String queryStr = createAnswerQueryString(buildQuery);

			Query q = entityManager.createNativeQuery(queryStr);
			if (entityId != null) {
				q.setParameter("entityId", entityId);
			}
			
			@SuppressWarnings("unchecked")
			Object answer = q.getSingleResult();

			LOGGER.debug("DataStatus Adapter Filters Setting to Request END");
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			if (answer != null) {
				if (answer.toString().equalsIgnoreCase("B")
						|| answer.toString().equalsIgnoreCase("D"))
					resp.add("creditAnnexure", gson.toJsonTree(true));
				else
					resp.add("creditAnnexure", gson.toJsonTree(false));
			}
			resp.add("resp",
					gson.toJsonTree(gstr6Determinationvalues.getResult()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Gstr6 Determination ";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/ui/gstr6DeterminationDownloadReports", produces = MediaType.APPLICATION_JSON_VALUE)

	public void gstr6DeterminationDownloadsReports(@RequestBody String json,
			HttpServletResponse response) {

		JsonObject jsonObj = new JsonParser().parse(json).getAsJsonObject()
				.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();

		try {

			String fileName = null;
			Workbook workBook = null;
			Anx1ReportSearchReqDto criteria = gson.fromJson(jsonObj,
					Anx1ReportSearchReqDto.class);
			criteria.setDataType(GSTConstants.INWARD);
			LOGGER.debug("DataStatus Adapter Filters Setting to Request BEGIN");
			/**
			 * Start - Set Data Security Attributes
			 */

			Anx1ReportSearchReqDto dataSecSearcParam = basicCommonSecParamReports
					.setDataSecuritySearchParams(criteria);
			/**
			 * End - Set Data Security Attributes
			 */
			criteria.setType(DownloadReportsConstant.GSTR6_DETERMINATION);

			if (criteria.getType() != null
					&& criteria.getType().equalsIgnoreCase(
							DownloadReportsConstant.GSTR6_DETERMINATION)) {
				workBook = gstr6DeterminationReportHandler
						.findGstr6DeterminationReports(dataSecSearcParam);
				fileName = "Credit_Distribution_Data_RecordS";

			}
			if (workBook == null) {
				workBook = new Workbook();
			}
			if (fileName != null) {
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition", String
						.format("attachment; filename=" + fileName + ".xlsx"));
				workBook.save(response.getOutputStream(), SaveFormat.XLSX);
				response.getOutputStream().flush();
			}
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Gstr6 Determination ";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}
	}

	@PostMapping(value = "/ui/gstr6Turnover", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> gstr6TurnOver(@RequestBody String jsonInput) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject json = new JsonParser().parse(jsonInput).getAsJsonObject()
				.get("req").getAsJsonObject();
		JsonObject resp = new JsonObject();
		try {
			Anx1ReportSearchReqDto criteria = gson.fromJson(json,
					Anx1ReportSearchReqDto.class);
			criteria.setDataType(GSTConstants.INWARD);
			LOGGER.debug("DataStatus Adapter Filters Setting to Request BEGIN");
			Anx1ReportSearchReqDto dataSecSearcParam = basicCommonSecParamReports
					.setDataSecuritySearchParams(criteria);
			SearchResult<Gstr6DeterminationResponseDto> gstr6TurnOvervalues = gstr6DeterminationService
					.getGstr6TurnOvervalues(dataSecSearcParam);
			LOGGER.debug("DataStatus Adapter Filters Setting to Request END");
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(gstr6TurnOvervalues.getResult()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Gstr6 Determination ";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PostMapping(value = "/ui/gstr6EditAndSave", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> gstr6EditAndSave(
			@RequestBody String jsonInput) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject json = new JsonParser().parse(jsonInput).getAsJsonObject();
		JsonArray asJsonArray = json.get("req").getAsJsonArray();
		JsonObject resp = new JsonObject();
		try {
			Type listType = new TypeToken<List<Gstr6DeterminationResponseDto>>() {
			}.getType();

			List<Gstr6DeterminationResponseDto> dtos = gson
					.fromJson(asJsonArray, listType);

			for (Gstr6DeterminationResponseDto dto : dtos) {
				gstr6DeterminationService.persistData(dto);
			}

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(
					"GSTR6 TurnOver for distributions records saved"));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Gstr6 Turn Over Delete and Save copy Data  ";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PostMapping(value = "/ui/gstr6TurnOverDelete", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> gstr6TurnoverDeleteFun(
			@RequestBody String json) {
		JsonObject jsonObj = new JsonParser().parse(json).getAsJsonObject()
				.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			
			DocSeriesSDeleteReqDto dtos = gson.fromJson(jsonObj,
					DocSeriesSDeleteReqDto.class);

			if (dtos != null) {
				gstr6DeterminationService.deleteData(dtos);
			}
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(
					"GSTR6 TurnOver for distributions records deleted"));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			String msg = "Unexpected error while retriving Gstr6 Turn Over Delete";
			LOGGER.debug(msg, e);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	private String createAnswerQueryString(String buildQuery) {
		return "SELECT ANSWER FROM ENTITY_CONFG_PRMTR WHERE CONFG_QUESTION_ID = "
				+ " (SELECT ID FROM CONFG_QUESTION WHERE QUESTION_CODE = 'I31') "
				+ " AND IS_DELETE = FALSE " + buildQuery;
	}
}
