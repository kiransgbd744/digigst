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

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.docs.dto.FileStatusReqDto;
import com.ey.advisory.app.docs.dto.FileStatusResponseDto;
import com.ey.advisory.app.services.search.filestatussearch.ComprehensiveFileStatusService;
import com.ey.advisory.app.services.search.filestatussearch.ComprehensiveOldFileStatusService;
import com.ey.advisory.app.services.search.filestatussearch.FileStatusService;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.search.SearchResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 
 * @author Mahesh.Golla
 *
 */
/*
 * This controller responsible for getting the after uploading web vertical
 * excel status of files
 */
@RestController
public class SftpStatusController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SftpStatusController.class);
	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;

	@Autowired
	@Qualifier("FileStatusService")
	private FileStatusService fileStatusService;

	@Autowired
	@Qualifier("ComprehensiveFileStatusService")
	private ComprehensiveFileStatusService comprehensiveFileStatusService;

	@Autowired
	@Qualifier("ComprehensiveOldFileStatusService")
	private ComprehensiveOldFileStatusService comprehensiveOldFileStatusService;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@RequestMapping(value = { "/ui/sftpFileStatus",
			"/ui/newSftpFileStatus" }, method = RequestMethod.POST, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	/**
	 * File status
	 * 
	 * @param fileStatusJson
	 * @return ResponseEntity<String>
	 */
	public ResponseEntity<String> fileStatus(@RequestBody String fileStatusJson,
			HttpServletRequest request) {
		String tenantCode = TenantContext.getTenantId();
		LOGGER.error("Tenant Id Is {}", tenantCode);
		LOGGER.error("fileStatus methods{}");

		JsonObject jsonReqObj = (new JsonParser().parse(fileStatusJson)
				.getAsJsonObject());

		JsonObject json = jsonReqObj.get("req").getAsJsonObject();

		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		// Execute the File status service method and get the result.

		try {

			FileStatusReqDto fileReqDto = gson.fromJson(json,
					FileStatusReqDto.class);
			fileReqDto.setSource(JobStatusConstants.SFTP_WEB_UPLOAD);

			String path = request.getServletPath();
			if ("/ui/newSftpFileStatus.do".equals(path)) {
				if (GSTConstants.OUTWARD
						.equalsIgnoreCase(fileReqDto.getDataType())
						|| GSTConstants.GSTR1
								.equalsIgnoreCase(fileReqDto.getDataType())) {
					if (GSTConstants.COMPREHENSIVE_RAW
							.equalsIgnoreCase(fileReqDto.getFileType())) {
						SearchResult<FileStatusResponseDto> searchResult = comprehensiveFileStatusService
								.find(fileReqDto, null,
										FileStatusResponseDto.class);

						resp.add("hdr", gson
								.toJsonTree(APIRespDto.createSuccessResp()));
						resp.add("resp",
								gson.toJsonTree(searchResult.getResult()));
						return new ResponseEntity<>(resp.toString(),
								HttpStatus.OK);
					}
				} else {
					SearchResult<Gstr1FileStatusEntity> searchResult = fileStatusService
							.find(fileReqDto, null,
									Gstr1FileStatusEntity.class);

					resp.add("hdr",
							gson.toJsonTree(APIRespDto.createSuccessResp()));
					resp.add("resp", gson.toJsonTree(searchResult.getResult()));
					return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
				}
			} else {
				if (GSTConstants.OUTWARD
						.equalsIgnoreCase(fileReqDto.getDataType())
						|| GSTConstants.GSTR1
								.equalsIgnoreCase(fileReqDto.getDataType())) {
					if (GSTConstants.COMPREHENSIVE_RAW
							.equalsIgnoreCase(fileReqDto.getFileType())) {
						SearchResult<FileStatusResponseDto> searchResult = comprehensiveOldFileStatusService
								.find(fileReqDto, null,
										FileStatusResponseDto.class);

						resp.add("hdr", gson
								.toJsonTree(APIRespDto.createSuccessResp()));
						resp.add("resp",
								gson.toJsonTree(searchResult.getResult()));
						return new ResponseEntity<>(resp.toString(),
								HttpStatus.OK);
					}
				} else {
					SearchResult<Gstr1FileStatusEntity> searchResult = fileStatusService
							.find(fileReqDto, null,
									Gstr1FileStatusEntity.class);

					resp.add("hdr",
							gson.toJsonTree(APIRespDto.createSuccessResp()));
					resp.add("resp", gson.toJsonTree(searchResult.getResult()));
					return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
				}
			}
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving File Status ";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}
}