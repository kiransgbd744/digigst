package com.ey.advisory.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.ey.advisory.app.services.search.docsearch.GstnBasicDocSearchService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.GstnDocSearchReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

@RestController
public class GstnDocumentController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GstnDocumentController.class);
	
	@Autowired
	@Qualifier("GstnBasicDocSearchService")
	private GstnBasicDocSearchService docSearch;
	
	
	@RequestMapping(value = "/ui/gstnDocSearch", method = RequestMethod.POST,
			produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getDocuments(@RequestBody String jsonString) {
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.gsonInstanceWithExpose();
			SearchCriteria dto = gson.fromJson(reqJson, GstnDocSearchReqDto.class);
			String hdrJson = obj.get("hdr").getAsJsonObject().toString();
			PageReqDto hdrRequest = gson.fromJson(hdrJson, PageReqDto.class);
			int pageNo = hdrRequest.getPageNum();
			int pageSize = hdrRequest.getPageSize();
			PageRequest pageRequest = new PageRequest(pageNo,pageSize); 
			
			SearchResult<DocumentDto> searchResult = docSearch.find(dto,
					pageRequest, DocumentDto.class);
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
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while searching documents";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} 
	}

	
	
}
