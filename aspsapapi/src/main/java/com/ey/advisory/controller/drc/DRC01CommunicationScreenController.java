package com.ey.advisory.controller.drc;


import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.caches.DefaultDrc01cReasonCache;
import com.ey.advisory.app.data.entities.drc.TblDrcCommRequestDetails;
import com.ey.advisory.app.data.repositories.client.drc.Drc01RequestCommDetailsRepository;
import com.ey.advisory.app.data.services.drc.DrcCommRespDto;
import com.ey.advisory.app.data.services.drc.DrcGetReminderFrequencyRespDto;
import com.ey.advisory.app.data.services.drc01c.Drc01EmailCommService;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
public class DRC01CommunicationScreenController {

	@Autowired
	private GSTNDetailRepository gstinDetailRepo;

	@Autowired
	@Qualifier("Drc01EmailCommServiceImpl")
	private Drc01EmailCommService drcService;


	@Autowired
	@Qualifier("DefaultDrc01cReasonCache")
	DefaultDrc01cReasonCache defaultReasonCache;

	@Autowired
	Drc01RequestCommDetailsRepository requestRepo;
	
	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;

	private static final List<String> REG_TYPES = ImmutableList.of("REGULAR",
			"SEZ", "SEZU", "SEZD");

	@PostMapping(value = "/ui/getDrc01CommDetails", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getDrcDetails(@RequestBody String jsonString,
			HttpServletRequest req) {
		JsonObject jsonObj = new JsonObject();
		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject().getAsJsonObject("req");
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		DrcGetReminderFrequencyRespDto getRespDetails = new DrcGetReminderFrequencyRespDto();
		List<DrcCommRespDto> getRespDetail = new ArrayList<>();
		
		try {
			JsonArray gstins = new JsonArray();
			getRespDetails = gson.fromJson(requestObject, DrcGetReminderFrequencyRespDto.class);
			
			List<String> gstnsList = null;
			List<Long> entityIds = new ArrayList<>();
			
			Long entityId = getRespDetails.getEntityId();
			
			entityIds.add(entityId);
			if (!(getRespDetails.getGstins().isEmpty())
					&& (getRespDetails.getGstins().size() > 0)) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"As gstins are provided in request, not Invoking"
									+ "service to get gstins for entity");
				}
			
				gstnsList = getRespDetails.getGstins();
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Invoking service to get gstins for entity");
				}
				Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
						.getOutwardSecurityAttributeMap();
				Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
						.dataSecurityAttrMapForQuery(entityIds,
								outwardSecurityAttributeMap);
				List<String> dataSecList = dataSecAttrs
						.get(OnboardingConstant.GSTIN);

				gstnsList = gstinDetailRepo
						.filterGstinBasedByRegType(dataSecList, REG_TYPES);
			}
			if (gstnsList == null || gstnsList.isEmpty()) {
				JsonObject resp = new JsonObject();
				String msg = "Gstins cannot be empty";
				getRespDetails.setErrMsg(msg);
				JsonElement respBody = gson.toJsonTree(getRespDetails);
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", gson.toJsonTree(respBody));
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}
			
			
			getRespDetail = drcService.getDRC1CommDetails(gstnsList, getRespDetails);
			
			JsonElement respBody = gson.toJsonTree(getRespDetail);
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			jsonObj.add("resp", respBody);
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Unable to Fetch the DRC01C Details. ", ex);
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(ex.getMessage());
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(respBody));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}
	
	@GetMapping(value = "/ui/downloadDRC01Report")
	public void fileDownloads(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		LOGGER.debug("inside fileDownloads method and file type is {} ",
				"DRC01 report DOwnload");

		String docId = null;

		Document document = null;
		
		String tenantCode = TenantContext.getTenantId();
		LOGGER.debug("Tenant Id Is {}", tenantCode);
		String reqId = request.getParameter("requestId");
		Long requestId = Long.valueOf(reqId);
		
		List<TblDrcCommRequestDetails> reqStatusEntity = requestRepo
				.findByRequestId(requestId);

		if (reqStatusEntity != null) {
			docId = reqStatusEntity.get(0).getDocId();

			document = DocumentUtility.downloadDocumentByDocId(docId);
		}
		
		if (document == null) {
			return;
		}

		InputStream inputStream = document.getContentStream().getStream();
		int read = 0;
		byte[] bytes = new byte[1024];

		if (document != null) {
			response.setHeader("Content-Disposition",
					String.format("attachment; filename =" + reqStatusEntity.get(0).getFilePath()));
			OutputStream outputStream = response.getOutputStream();
			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
		}
	}


}
