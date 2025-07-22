package com.ey.advisory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.docs.dto.PageReqDto;
import com.ey.advisory.app.docs.dto.einvoice.GstinStatusDocSearchReqDto;
import com.ey.advisory.app.services.search.docsearch.GstinStatusDocSearchService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Shashikant.Shukla
 */

@RestController
@Slf4j
public class GstinSavedDocumentController {

	public static final String success = "Success";
	public static final String failed = "Failed";

	@Autowired
	@Qualifier("GstinStatusDocSearchService")
	private GstinStatusDocSearchService docSearch;

	/*
	 * This Method Search for the document based on the filter
	 */
	@PostMapping(value = "/ui/gstinSavedDocSearch", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getEInvoiceDocument(
			@RequestBody String jsonString) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWB24HRFormat();
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json " + reqJson);
			}
			SearchCriteria dto = gson.fromJson(reqJson,
					GstinStatusDocSearchReqDto.class);
			String hdrJson = obj.get("hdr").getAsJsonObject().toString();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Hdr Json " + hdrJson);
			}
			PageReqDto hdrRequest = gson.fromJson(hdrJson, PageReqDto.class);
			int pageNo = hdrRequest.getPageNum();
			int pageSize = hdrRequest.getPageSize();
			PageRequest pageRequest = new PageRequest(pageNo, pageSize);
			SearchResult<GstinStatusDocSearchReqDto> searchResult = docSearch
					.findNew(dto, pageRequest, GstinStatusDocSearchReqDto.class);
			int totalCount = searchResult.getTotalCount();
			PageRequest pageReq = searchResult.getPageReq();
			int pageNoResp = pageReq.getPageNo();
			int pageSizeResp = pageReq.getPageSize();
			resp.add("hdr",
					gson.toJsonTree(APIRespDto.createSuccessResp(totalCount,
							pageNoResp, pageSizeResp, null, null)));
			resp.add("resp", gson.toJsonTree(searchResult.getResult()));
		} catch (Exception e) {
			resp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", e.getMessage())));
			LOGGER.error("Exception Occured:{} ", e);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

}
