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
import com.ey.advisory.app.gstr2.reconresults.filter.Gstr2ReconResultMultiUnlockDao;
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
@Component("Gstr2ReconResultMultiUnlockProcessor")
public class Gstr2ReconResultMultiUnlockProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository fileStatusRepository;

	@Autowired
	@Qualifier("FileStatusDownloadReportRepository")
	private FileStatusDownloadReportRepository reportDownloadRepo;

	@Autowired
	@Qualifier("Gstr2ReconResultMultiUnlockDaoImpl")
	private Gstr2ReconResultMultiUnlockDao service;

	@Autowired
	@Qualifier("Gstr2ReconResultsServiceImpl")
	Gstr2ReconResultsService reconResultservice;

	@Autowired
	@Qualifier("Gstr2ReconResponseUploadDaoImpl")
	Gstr2ReconResponseUploadDao reconlinkIdsService;

	@Override
	public void execute(Message message, AppExecContext context) {

		LOGGER.debug("Begin Gstr2ReconResultMultiUnlockProcessor job");

		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();

		JsonParser parser = new JsonParser();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Gstr2ReconResultMultiUnlockProcessor Upload Job"
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

			LOGGER.debug("GSTR2 Multi Unlock is in progress ");

			if (respIden.equalsIgnoreCase("Multi")) {

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
				
				entity.setUsrResp("Unlock");
				entity.setFmResp("Unlock");
				entity.setUsrAcs1(reqDto.getResponseRemarks());
				
				LOGGER.debug("Setting VENDOR_PAN,VENDOR_GSTIN,USERACCESS6  ");
				entity.setVendorPan(convertToQueryFormat(reqDto.getVendorPans()));
				
				entity.setVendorGstin(convertToQueryFormat(reqDto.getVendorGstins()));
				
				entity.setUsrAcs6(reqDto.getTaxPeriodBase());

				reportDownloadRepo.save(entity);
			
				service.unlockResponseAndErrorFileCrea(batchId, reconType,
						fileId);
		
				fileStatusRepository.updateFileStatus(fileId, "Completed");
				
		
			} else {

				String refId = reconResultservice.reconResponseUpload(reqDto,
						fileId, userName, batchId,"Multi");
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Gstr2ReconResultMultiUnlockProcessor groupCode %s saved into tbl for batchId %s",
							groupCode, refId);
					LOGGER.debug(msg);
				}
				int reconLinkIdSize = reqDto.getReconLinkId() != null
						? reqDto.getReconLinkId().size() : 0;
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Gstr2ReconResultMultiUnlockProcessor reconLinkIdSize %d",
							reconLinkIdSize);
					LOGGER.debug(msg);
				}
				
				service.unlockResponseAndErrorFileCrea(batchId, reconType,
						fileId);
				
				fileStatusRepository.updateFileStatusProcessed(fileId, "Completed",
						reconLinkIdSize);
				}
				
		} catch (Exception ex) {
			String msg = String.format(
					"Error occured in Gstr2ReconResultMultiUnlockProcessor for %s ",
					ex);
			LOGGER.error(msg, ex);
			fileStatusRepository.updateFailedStatus(fileId,
					StringUtils.abbreviate(ex.getLocalizedMessage(), 4000),
					"Failed");//.truncate as there
			throw new AppException(msg, ex);

		}

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
