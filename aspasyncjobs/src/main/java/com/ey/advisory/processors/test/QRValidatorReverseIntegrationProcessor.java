package com.ey.advisory.processors.test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRPDFJSONResponseSummaryRepo;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRPDFResponseSummaryRepo;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRResponseSummaryRepo;
import com.ey.advisory.app.data.repositories.client.qrcodevalidator.QRUploadFileStatusRepo;
import com.ey.advisory.app.data.services.qrcodevalidator.QRCodeValidatorService;
import com.ey.advisory.app.data.services.qrcodevalidator.QRCodeValidatorServiceImpl;
import com.ey.advisory.app.data.services.qrcodevalidator.QRPDFJsonValidatorServiceImpl;
import com.ey.advisory.app.data.services.qrcodevalidator.QRPDFValidatorServiceImpl;
import com.ey.advisory.app.sftp.service.SFTPFileTransferService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Saif.S
 *
 */
@Slf4j
@Component("QRValidatorReverseIntegrationProcessor")
public class QRValidatorReverseIntegrationProcessor implements TaskProcessor {

	@Autowired
	QRUploadFileStatusRepo qrUploadFileStatusRepo;

	@Autowired
	QRResponseSummaryRepo qrRespSummRepo;

	@Autowired
	QRPDFResponseSummaryRepo qrpdfRespSummRepo;

	@Autowired
	QRPDFJSONResponseSummaryRepo qrpdfjsonRespSummRepo;

	@Autowired
	@Qualifier("QRCodeValidatorServiceImpl")
	QRCodeValidatorService qrCodeService;

	@Autowired
	@Qualifier("SFTPFileTransferServiceImpl")
	private SFTPFileTransferService sftpService;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;
	
	@Autowired
	@Qualifier("QRCodeValidatorServiceImpl")
	private QRCodeValidatorServiceImpl qrCodeValidatorServiceImpl;
	
	@Autowired
	@Qualifier("QRPDFValidatorServiceImpl")
	private QRPDFValidatorServiceImpl qrPDFValidatorServiceImpl;
	
	@Autowired
	@Qualifier("QRPDFJsonValidatorServiceImpl")
	private QRPDFJsonValidatorServiceImpl qrPDFJsonValidatorServiceImpl;
	

	@Override
	public void execute(Message message, AppExecContext context) {

		Gson gson = new Gson();
		String jsonString = message.getParamsJson();
		File tempDir = null;
		try {
			tempDir = createTempDir();
			List<Long> activeIds = null;
			String idsString = "";

			Type listType = new TypeToken<ArrayList<Long>>() {
			}.getType();

			Map<String, Config> configMap = configManager.getConfigs("SFTP",
					"ey.internal.qrvalidator.sftp.response");

			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Recieved FileIds to reverseInterate: {}",
						requestObject.toString());
			}

			if (requestObject.has("A")) {
				idsString = requestObject.get("A").getAsString();
				activeIds = gson.fromJson(idsString, listType);
				qrCodeValidatorServiceImpl.revIntegrateQRData(activeIds, configMap, tempDir);
			}

			if (requestObject.has("B")) {
				idsString = requestObject.get("B").getAsString();
				activeIds = gson.fromJson(idsString, listType);
				qrPDFValidatorServiceImpl.revIntegrateQRPDFData(activeIds, configMap, tempDir);
			}

			if (requestObject.has("C")) {
				idsString = requestObject.get("C").getAsString();
				activeIds = gson.fromJson(idsString, listType);
				qrPDFJsonValidatorServiceImpl.revIntegrateQRPDFJsonData(activeIds, configMap, tempDir);
			}

			
		} catch (Exception ee) {
			String msg = "Exception occured while reverse integrating reports"
					+ " to SFTP Destination from QR Validator";
			LOGGER.error(msg, ee);
			throw new AppException(msg, ee);
		} finally {
			if (tempDir != null) {
				GenUtil.deleteTempDir(tempDir);
			}
		}
	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("QRValidatorReverseIntgFiles")
				.toFile();
	}
}
