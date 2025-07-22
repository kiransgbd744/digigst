/*package com.ey.advisory.admin.services.onboarding.gstinfileupload.docs;

import java.io.InputStream;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.docs.dto.GstinRegFileArrivalMsgDto;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.GstinElRegRepository;
import com.ey.advisory.admin.services.gstin.jobs.GSTinConstants;
import com.ey.advisory.admin.services.onboarding.gstinfileupload.GstinFileUploadUtil;
import com.ey.advisory.admin.services.onboarding.gstinfileupload.GstinRegObjArrToEntityConverter;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.RowHandler;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.common.eyfileutils.tabular.impl.FileUploadDocRowHandler;

@Component("ELRFileArrivalHandler")
public class ELRFileArrivalHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ELRFileArrivalHandler.class);

	@Autowired
	@Qualifier("OnboardingDefaultTraverserFactoryImpl")
	private OnboardingFileTraverserFactory onboardingFileTraverserFactory;

	@Autowired
	@Qualifier("GstinFileUploadUtil")
	private GstinFileUploadUtil gstinFileUploadUtil;

	@Autowired
	@Qualifier("GstinRegObjArrToEntityConverter")
	private GstinRegObjArrToEntityConverter
		onboardingFileToELregGstinDetailsConvertion;

	@Autowired
	@Qualifier("GstinElRegRepository")
	private GstinElRegRepository gstinElRegRepository;

	public void processELREGFile(Message message, AppExecContext context) {

		String fileArrivalMsg = String.format("File Arrived - Message is: '%s'",
				message.getParamsJson());
		LOGGER.debug(fileArrivalMsg);
		// Extract the File Arrival message from the serialized Job params
		// object.

		// OnboardingFileArrivalMsgDto msg = extractAndValidateMessage(message);
		// Join the file path and file name to get the file path.
		
		 * String fileName = msg.getFileName(); String fileFolder =
		 * msg.getFilePath(); String oriFileName = fileName.substring(0,
		 * fileName.lastIndexOf("."));
		 
		// String fileName = "GSTINTemplate.xlsx";
		// String fileFolder = "C:/sasi_impr/testdata/";

		String fileFolder = GSTinConstants.GSTIN_ONBOARD_FOLDER_NAME;
		// String fileName = GSTinConstants.ELREGISTRATION_FILE_NAME;

		String fileName = "C:/sasi_impr/testdata/GstinTemplate1.xlsx";
		InputStream inputStream = null;
		try {
			Session openCmisSession = gstinFileUploadUtil.getCmisSession();
			LOGGER.debug("openCmisSession " + openCmisSession);
			Document document = gstinFileUploadUtil.getDocument(openCmisSession,
					fileName, fileFolder);
			LOGGER.debug("document name" + document.getName());
			inputStream = document.getContentStream().getStream();
		} catch (Exception e) {
			LOGGER.error("Exception occured in Gstin File Arrival Processor",
					e);
		}

		TabularDataSourceTraverser traverser = onboardingFileTraverserFactory
				.getTraverser(fileName);
		TabularDataLayout layout = new DummyTabularDataLayout(12);

		// Add a dummy row handler that will keep counting the rows.
		RowHandler rowHandler = new FileUploadDocRowHandler<>();
		long stTime = System.currentTimeMillis();
		traverser.traverse(fileName, layout, rowHandler, null);

		List<Object[]> elrList = ((FileUploadDocRowHandler<?>) rowHandler)
				.getFileUploadList();

		List<GSTNDetailEntity> gstinElrDoc = 
				onboardingFileToELregGstinDetailsConvertion
				.convert(elrList);
		gstinElRegRepository.saveAll(gstinElrDoc);

	}

	*//**
	 * Validate the Input JSON to check if 'path', 'fileName' and Group Code are
	 * actually present within the JSON. If not, throw an EWB Exception so that
	 * the file processing status is marked as 'Failed'. Otherwise, extract
	 * these values and return them.
	 * 
	 * @param jsonMessage
	 *            The 'Message' instance passed by the AsyncExecution framework
	 * 
	 *//*
	private GstinRegFileArrivalMsgDto extractAndValidateMessage(
			Message message) {
		GstinRegFileArrivalMsgDto msg = GsonUtil.newSAPGsonInstance().fromJson(
				message.getParamsJson(), GstinRegFileArrivalMsgDto.class);

		if (msg.getFilePath() == null || msg.getFileName() == null) {
			String errMsg = "Invalid Path/FileName received in msg";
			String logMsg = String.format(
					"Values received are -> "
							+ "Path: '%s', FileName: '%s', GroupCode: '%s'",
					msg.getFilePath(), msg.getFileName(),
					message.getGroupCode());
			LOGGER.error(String.format("%s %s", msg, logMsg));
			throw new AppException(errMsg);
		}
		// Log the extracted info from the message.
		if (LOGGER.isDebugEnabled()) {
			String logMsg = String.format(
					"Message Details are -> Path: '%s', "
							+ "File Name: '%s', Group Code: '%s'",
					msg.getFilePath(), msg.getFileName(),
					message.getGroupCode());
			LOGGER.debug(logMsg);
		}
		return msg;
	}
}
*/