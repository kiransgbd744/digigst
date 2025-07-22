package com.ey.advisory.processors.test;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.gstr2.reconresults.filter.Gstr2ReconResponseUploadDao;
import com.ey.advisory.app.gstr2.reconresults.filter.Gstr2ReconResultResponseDao;
import com.ey.advisory.app.gstr2.reconresults.filter.Gstr2ReconResultsReqDto;
import com.ey.advisory.app.gstr2.reconresults.filter.Gstr2ReconResultsService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */
@Slf4j
@Component("Gstr2ReconResultResponseProcessor")
public class Gstr2ReconResultResponseProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository fileStatusRepository;

	@Autowired
	@Qualifier("FileStatusDownloadReportRepository")
	private FileStatusDownloadReportRepository reportDownloadRepo;

	@Autowired
	@Qualifier("Gstr2ReconResultResponseDaoImpl")
	private Gstr2ReconResultResponseDao service;

	@Autowired
	@Qualifier("Gstr2ReconResultsServiceImpl")
	Gstr2ReconResultsService reconResultservice;

	@Autowired
	@Qualifier("Gstr2ReconResponseUploadDaoImpl")
	Gstr2ReconResponseUploadDao reconlinkIdsService;
	
	@Autowired
	@Qualifier("Gstr2ImsReconResultResponseDaoImpl")
	private Gstr2ReconResultResponseDao imsService;
	
	

	@Override
	public void execute(Message message, AppExecContext context) {

		LOGGER.debug("Begin Gstr2APRReconResultResponseProcessor job");

		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();

		JsonParser parser = new JsonParser();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Gstr2APRReconResultResponseProcessor Upload Job"
							+ " executing for groupcode %s and params %s",
					groupCode, jsonString);
			LOGGER.debug(msg);
		}
		JsonObject json = (JsonObject) parser.parse(jsonString);
		Long batchId = json.get("batchId").getAsLong();
		Long fileId = json.get("fileId").getAsLong();

		Gson gson = GsonUtil.newSAPGsonInstance();

		String reconType = json.get("reconType").getAsString();

		JsonObject reqJson = json.get("reqDto").getAsJsonObject();

		Gstr2ReconResultsReqDto reqDto = gson.fromJson(reqJson,
				Gstr2ReconResultsReqDto.class);

		String respIden = json.get("respIden").getAsString();
		String userName = json.get("userName").getAsString();

		try {

			fileStatusRepository.updateFileStatus(fileId, "InProgress");

			LOGGER.debug("GSTR2 Response Upload File is in progress ");

			if (!respIden.equalsIgnoreCase("response")) {

				FileStatusDownloadReportEntity entity = new FileStatusDownloadReportEntity();
				entity.setFileId(batchId);
				entity.setReportCateg(reconType + "ReconResultUI");
				entity.setDerivedRetPeriodFrom(
						Long.valueOf(reqDto.getFromTaxPeriod()));
				entity.setDerivedRetPeriodFromTo(
						Long.valueOf(reqDto.getToTaxPeriod()));
				entity.setGstins(GenUtil.convertStringToClob(
						convertToQueryFormat(reqDto.getGstins())));
				entity.setReportType(convertToQueryFormat(reqDto.getReportTypes()));
				entity.setReconCriteria(reqDto.getReconCriteria());
				if (reqDto.getIndentifier().equalsIgnoreCase("Force")) {
					entity.setUsrResp("Lock");
					entity.setFmResp("Lock");
				} else if (reqDto.getIndentifier().equalsIgnoreCase("3B")) {
					entity.setUsrResp(reqDto.getTaxPeriodGstr3b());
					entity.setTaxPeriod(reqDto.getTaxPeriodGstr3b());
				} else if (reqDto.getIndentifier().equalsIgnoreCase("Unlock")) {
					entity.setUsrResp("Unlock");
					entity.setFmResp("Unlock");
				} else if (reqDto.getIndentifier().equalsIgnoreCase("IMS")) {
					entity.setUsrAcs4(reqDto.getImsUserResponse());
					entity.setUsrAcs5(reqDto.getImsResponseRemarks());
					entity.setDocType((convertToQueryFormat(reqDto.getDocType())));
				}

				entity.setUsrAcs1(reqDto.getResponseRemarks());
				
				LOGGER.debug("Setting VENDOR_PAN,VENDOR_GSTIN,USERACCESS6  ");
				entity.setVendorPan(convertToQueryFormat(reqDto.getVendorPans()));
				
				entity.setVendorGstin(convertToQueryFormat(reqDto.getVendorGstins()));
				
				entity.setUsrAcs6(reqDto.getTaxPeriodBase());

				reportDownloadRepo.save(entity);

				if (!"IMS".equalsIgnoreCase(reqDto.getIndentifier())) {
					String response = service.validateResponseAndErrorFileCrea(
							batchId, reconType, fileId);
					if (!"No_Data_found".equalsIgnoreCase(response)) {
						fileStatusRepository.updateFileStatus(fileId,
								"Completed");
					}
				} else {
					String response = imsService.validateResponseAndErrorFileCrea(
							batchId, reconType, fileId);
					if (!"No_Data_found".equalsIgnoreCase(response)) {
						fileStatusRepository.updateFileStatus(fileId,
								"Completed");
					}

				}

			} else {

				String refId = reconResultservice.reconResponseUpload(reqDto,
						fileId, userName, batchId, null);
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Gstr2APRReconResultResponseProcessor groupCode %s saved into tbl for batchId %s",
							groupCode, refId);
					LOGGER.debug(msg);
				}
				int reconLinkIdSize = reqDto.getReconLinkId() != null
						? reqDto.getReconLinkId().size() : 0;
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Gstr2APRReconResultResponseProcessor reconLinkIdSize %d",
							reconLinkIdSize);
					LOGGER.debug(msg);
				}
				
				if (!"IMS".equalsIgnoreCase(reqDto.getIndentifier())) {
					String response = service.validateResponseAndErrorFileCrea(
							batchId, reconType, fileId);
					if (!"No_Data_found".equalsIgnoreCase(response)) {
						fileStatusRepository.updateFileStatusProcessed(fileId,
								"Completed", reconLinkIdSize);
					}
				} else {
					String response = imsService.validateResponseAndErrorFileCrea(
							batchId, reconType, fileId);
					if (!"No_Data_found".equalsIgnoreCase(response)) {
						fileStatusRepository.updateFileStatusProcessed(fileId,
								"Completed", reconLinkIdSize);
					}

				}

				/*String response = service.validateResponseAndErrorFileCrea(
						batchId, reconType, fileId);

				if (!"No_Data_found".equalsIgnoreCase(response)) {
					fileStatusRepository.updateFileStatusProcessed(fileId,
							"Completed", reconLinkIdSize);
				}*/

			}

		} catch (Exception ex) {
			String msg = String.format(
					"Error occured in Gstr2APRReconResultResponseProcessor for %s ",
					ex);
			LOGGER.error(msg, ex);
			fileStatusRepository.updateFailedStatus(fileId,
					StringUtils.abbreviate(ex.getLocalizedMessage(), 4000),
					"Failed");//.truncate as there
			throw new AppException(msg, ex);

		}

	}
	
	private static String removeSingleQuotes(String reportTypes) {
		return reportTypes.replace("'", "");
	}

	private String convertToQueryFormat(List<String> list) {

		String queryData = null;

		if (list == null || list.isEmpty())
			return null;

		queryData = "'" + list.get(0) + "'";
		for (int i = 1; i < list.size(); i++) {
			queryData += "," + "'" + list.get(i) + "'";
		}

		return queryData;

	}
}
