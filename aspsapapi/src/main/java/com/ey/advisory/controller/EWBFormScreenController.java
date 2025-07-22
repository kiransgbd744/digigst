/**
 * 
 */
package com.ey.advisory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.docs.dto.DocumentDto;
import com.ey.advisory.app.docs.dto.PageReqDto;
import com.ey.advisory.app.services.search.docsearch.EWBFormScreenDocSearchService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.controllers.anexure1.InputValidationUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.DocSearchReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@RestController
@Slf4j
public class EWBFormScreenController {

	@Autowired
	@Qualifier("EWBFormScreenDocSearchService")
	private EWBFormScreenDocSearchService ewbFormScreenDocSearchService;

	@RequestMapping(value = "/ui/ewbFormSearch", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEwbFormDocs(
			@RequestBody String jsonString) {
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json " + reqJson);
			}
			// Gson gson = GsonUtil.gsonInstanceWithExpose();
			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			SearchCriteria dto = gson.fromJson(reqJson, DocSearchReqDto.class);
			String hdrJson = obj.get("hdr").getAsJsonObject().toString();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Hdr Json " + hdrJson);
			}
			PageReqDto hdrRequest = gson.fromJson(hdrJson, PageReqDto.class);
			int pageNo = hdrRequest.getPageNum();
			int pageSize = hdrRequest.getPageSize();
			PageRequest pageRequest = new PageRequest(pageNo, pageSize);
			SearchResult<DocumentDto> searchResult = ewbFormScreenDocSearchService
					.find(dto, pageRequest, DocumentDto.class);
			int totalCount = searchResult.getTotalCount();
			PageRequest pageReq = searchResult.getPageReq();
			int pageNoResp = pageReq.getPageNo();
			int pageSizeResp = pageReq.getPageSize();
			JsonObject resp = new JsonObject();
			resp.add("hdr",
					gson.toJsonTree(APIRespDto.createSuccessResp(totalCount,
							pageNoResp, pageSizeResp, null, null)));
			resp.add("resp", gson.toJsonTree(searchResult.getResult()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception in retrieving Outward Docs " + ex);
			return InputValidationUtil.createJsonErrResponse(ex);
		}
	}

}
