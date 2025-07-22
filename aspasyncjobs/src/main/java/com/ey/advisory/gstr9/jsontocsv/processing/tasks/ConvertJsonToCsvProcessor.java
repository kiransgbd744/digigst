package com.ey.advisory.gstr9.jsontocsv.processing.tasks;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.ZipGenStatusEntity;
import com.ey.advisory.apidashboard.common.service.Gstr9ControlService;
import com.ey.advisory.app.data.repositories.client.GstinGetStatusRepository;
import com.ey.advisory.app.data.repositories.client.ZipGenStatusRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.JsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.common.multitenancy.CommonContext;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.gstr9.jsontocsv.converters.JsonToCsvConverter;
import com.ey.advisory.jsontocsv.services.JsonToCsvConvertorFactory;
import com.ey.advisory.processing.messages.JsonFileArrivalMessage;
import com.google.gson.stream.JsonReader;

import lombok.extern.slf4j.Slf4j;

/**
 * This processor is responsible for converting the Gstr1, Gstr2A and Gstr3B
 * JSONs to CSVs, as part of the Gstr9 requirements. This processor assumes that
 * the input JSON files required for the conversion are available in a
 * pre-defined location (Currently, the location is a BLOB container in Azure.
 * This processor fetches the BLOB containing the required JSON into a temporary
 * file in the VM and uses it to perform the conversion. It then uploads the
 * converted CSV back to another CSV container in the Azure blob store, which is
 * then available for consumption by the DB stored procedures for performing the
 * Gstr9 computation.
 * 
 * @author Sai.Pakanati
 *
 */
@Slf4j
@Component("ConvertJsonToCsvProcessor")
public class ConvertJsonToCsvProcessor implements TaskProcessor {

	@Autowired
	private JsonToCsvConvertorFactory convertorFactory;

	@Autowired
	private GstinGetStatusRepository gstinStatusRepository;

	@Autowired
	private Gstr9ControlService gstr9ControlService;

	@Autowired
	private ZipGenStatusRepository zipGenStatusRepo;

	/**
	 * The Csv Converter strategy that reads the JSON files from the BLOB
	 * container and writes the converted files to another blob container.
	 */
	@Autowired
	@Qualifier("CsvConvSapDocRepoStrategy")
	private ObjectFactory<CsvConversionFileSystemStrategy> mainStrategyFactory;

	@Override
	public void execute(Message message, AppExecContext context) {

		String gstin = null;
		String taxPeriod = null;
		String section = null;
		String returnType = null;
		try {
			JsonFileArrivalMessage msg = extractAndValidateMessage(message);
			gstin = msg.getGstin();
			taxPeriod = msg.getTaxPeriod();
			section = msg.getInvoiceType();
			returnType = msg.getReturnType();

			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Before Csv Flag Reset , JobId '%d'", message.getId());
				LOGGER.debug(logMsg);
			}
			//Reset the CsvGeneratedFlag to zero and Job/workflow Status to Null
			resetCsvFlag(msg);

			updateWorkflowStatus(gstin, taxPeriod, returnType, section,
					JobStatusConstants.CSV_STARTED);
			// Perform the actual conversion from Json to Csv Files.
			convertJsonFilesToCsv(msg);

			// Update the control table CSV Generation status.
			updateControlTable(gstin, taxPeriod, returnType, section);
			updateWorkflowStatus(gstin, taxPeriod, returnType, section,
					JobStatusConstants.CSV_END);

			// Reset the zip file/blob path to null. Only then invoke the
			// DB stored proc.
			resetZipPath(msg);

		} catch (Exception ex) {
			if (isValidMessage(gstin, taxPeriod, returnType, section)) {
				updateWorkflowStatus(gstin, taxPeriod, returnType, section,
						JobStatusConstants.CSV_FAILED);
			}
			String errMsg = "Error Occured while Json2Csv Conversion";
			LOGGER.error(errMsg, ex);
			// Throw the App Exception here. If the exception obtained is
			// AppException, then propagate it. Otherwise, create a new
			// app exception. This particular constructor of the AppException
			// will extract the nested exception and attach the message to the
			// specified string. (This way the person who monitors the
			// EY_JOB_DETAILS database will come to know the root cause of the
			// exception).
			throw new AppException(errMsg, ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);
		} finally {
			CommonContext.clearContext();
		}
	}

	private void convertJsonFilesToCsv(JsonFileArrivalMessage msg) {

		// First get the converter.
		String converterName = msg.getReturnType() + "_" + msg.getInvoiceType();
		JsonToCsvConverter json2CsvConverter = convertorFactory
				.getConvertor(converterName);

		CommonContext.setGstin(msg.getGstin());
		CommonContext.setTaxPeriod(msg.getTaxPeriod());
		CommonContext.setReturnType(msg.getReturnType());

		// Get the strategy reference and initialize it.
		CsvConversionFileSystemStrategy curStrategy = mainStrategyFactory
				.getObject();

		// Get the list of files from the storage.
		List<? extends Object> jsonFiles = curStrategy.listInputJsons(msg);
		if (LOGGER.isDebugEnabled()) {
			String infoMsg = String.format("About to perform Pre processing"
					+ " which is cleaning of existing csv files in doc Repo");
			LOGGER.debug(infoMsg);
		}
		// Cleanup the existing file in the storage.
		curStrategy.performPreProcessing(msg);

		// Create the Output Writer using the strategy and write the header
		// of the CSV file into the writer.
		List<BufferedWriter> writers = null;
		JsonReader reader = null;
		try {
			int writersCount = json2CsvConverter.getNoOfConvOutputs();
			if (LOGGER.isDebugEnabled()) {
				String infoMsg = String.format("About to create the writers,"
						+ " Total No Of writers supposed to get created '%d'",
						writersCount);
				LOGGER.debug(infoMsg);
			}
			writers = curStrategy.createOutputCsvWriters(writersCount, msg);
			if (LOGGER.isDebugEnabled()) {
				String infoMsg = String.format(
						"created the writers,"
								+ " Total No Of writers created '%d'",
						writersCount);
				LOGGER.debug(infoMsg);
			}
			String[] hdrStrings = json2CsvConverter.getCsvHeaderStrings();
			int hdrCount = 0;
			for (BufferedWriter writer : writers) {
				writer.write(hdrStrings[hdrCount++]);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Written the Header to Csv File writer");
				}
			}
			final BufferedWriter[] writerArr = new BufferedWriter[writers
					.size()];
			for (int i = 0; i < writers.size(); i++) {
				writerArr[i] = writers.get(i);
			}

			for (Object fileHandle : jsonFiles) {

				// First create the reader.
				reader = curStrategy.createJsonReader(fileHandle);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Conversion of Json to Csv Started");
				}
				json2CsvConverter.convertJsonTOCsv(reader, writerArr);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Conversion of Json to Csv completed");
				}
				// cleanup the input resources and close it and release any
				// temporary resources.
				curStrategy.cleanupInputJsonReader(reader);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Clean Up of input resources completed");
				}
			}
			curStrategy.flushOutputWriters(writers);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Writers has been flushed to Temp File");
			}
			// Perform any final cleanup activities here.
			curStrategy.performPostProcessingOnSuccess(msg);
		} catch (IOException ex) {
			String errMsg = "Error Occured while Json2Csv Conversion";
			LOGGER.error(errMsg, ex);
			throw new AppException(errMsg, ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);
		} finally {

			if (reader != null) {
				curStrategy.cleanupInputJsonReader(reader);
			}

			if (!writers.isEmpty()) {
				curStrategy.cleanupOutputCsvWriters(writers);
			}

			curStrategy.performPostProcessingOnError();
		}
	}

	private void updateControlTable(String gstin, String taxPeriod,
			String returnType, String invoiceType) {
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"About to update CsvGenStatus in control table for"
							+ "GSTIN: '%s', TaxPeriod: '%s', RetType: '%s', "
							+ "InvType: '%s'",
					gstin, taxPeriod, returnType, invoiceType);
			LOGGER.debug(msg);
		}
		int rowsAffected = gstinStatusRepository.updateCsvGenerationStatus(true,
				LocalDateTime.now(), gstin, taxPeriod, returnType, invoiceType);
		if (rowsAffected == 0) {
			String msg = String.format("No record found "
					+ "in the control table for GSTIN: '%s', "
					+ "TaxPeriod: '%s', ReturnType: '%s', InvType: " + "'%s'",
					gstin, taxPeriod, returnType, invoiceType);
			LOGGER.error(msg);
			throw new AppException(msg);
		}
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Updated CsvGenStatus in control table for"
							+ "GSTIN: '%s', TaxPeriod: '%s', RetType: '%s', "
							+ "InvType: '%s'",
					gstin, taxPeriod, returnType, invoiceType);
			LOGGER.debug(msg);
		}
	}

	private JsonFileArrivalMessage extractAndValidateMessage(Message message) {

		JsonFileArrivalMessage msg = JsonUtil.newGsonInstance(false).fromJson(
				message.getParamsJson(), JsonFileArrivalMessage.class);

		if (msg.getGstin() == null || msg.getInvoiceType() == null
				|| msg.getTaxPeriod() == null || msg.getReturnType() == null) {
			String errMsg = "Invalid FilePath Details received in msg";
			LOGGER.error(String.format("%s", msg));
			throw new AppException(errMsg);
		}

		// Log the extracted info from the message.
		if (LOGGER.isDebugEnabled()) {
			String logMsg = String.format(
					"Message Details are -> Group Code: '%s'",
					message.getGroupCode());
			LOGGER.debug(logMsg);
		}

		return msg;
	}

	private void resetCsvFlag(JsonFileArrivalMessage msg) {
		gstinStatusRepository.updateCsvGenerationStatus(false,
				LocalDateTime.now(), msg.getGstin(), msg.getTaxPeriod(),
				msg.getReturnType(), msg.getInvoiceType());

		updateWorkflowStatus(msg.getGstin(), msg.getTaxPeriod(),
				msg.getReturnType(), msg.getInvoiceType(), null);

		if (LOGGER.isDebugEnabled()) {
			String infoMsg = String.format(
					"Csv Generation Flag Reseted to 0 and"
							+ " Job Status to Null, for  -> GSTIN: '%s', "
							+ "RetType: '%s', InvType: '%s', Tax Period: '%s'",
					msg.getGstin(), msg.getReturnType(), msg.getInvoiceType(),
					msg.getTaxPeriod());
			LOGGER.debug(infoMsg);
		}
	}

	private void resetZipPath(JsonFileArrivalMessage msg) {

		try {
			int rowsEffected = zipGenStatusRepo.updateGstr9ZipFilePathIfNotNull(
					msg.getReturnType(), msg.getGstin(), msg.getTaxPeriod(),
					null, LocalDateTime.now(), null);

			if (rowsEffected == 0) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Creating new entry for zip file generation "
							+ "with gstin :{}, taxperiod :{}, return type :{}",
							msg.getGstin(), msg.getTaxPeriod(),
							msg.getReturnType());
				}
				try {

					Integer derivedTaxPeriod = Integer
							.valueOf(msg.getTaxPeriod().substring(2).concat(
									msg.getTaxPeriod().substring(0, 2)));

					ZipGenStatusEntity zipGenEntry = new ZipGenStatusEntity(
							msg.getGstin(), msg.getTaxPeriod(), null,
							derivedTaxPeriod, msg.getReturnType(),
							LocalDateTime.now(), null, null);

					zipGenStatusRepo.save(zipGenEntry);

				} catch (DataIntegrityViolationException ex) {
					// Log the exception and proceed. This is normal and can
					// happen if a race condition occurs when 2 threads try
					// to insert into the AnnualReturnController table.
					String errMsg = String.format(
							"Trying to insert duplicate entry in tblGstr9ZipGenStatus "
									+ "table. Ignoring the error");
					// Don't throw back the error.
					LOGGER.error(errMsg, ex);
				}

			} else {

				if (LOGGER.isDebugEnabled()) {
					String msg1 = String.format(
							"After Resetting Monthly Zip Gen status for"
									+ "GSTIN: '%s', TaxPeriod: '%s', RetType: '%s', ",
							msg.getGstin(), msg.getTaxPeriod(),
							msg.getReturnType());
					LOGGER.debug(msg1);
				}
			}

		} catch (Exception ex) {
			// Log the exception and proceed. This is normal and can
			// happen if a race condition occurs when 2 threads try
			// to insert into the AnnualReturnController table.
			String errMsg = String
					.format("Trying to update entry in tblGstr9ZipGenStatus "
							+ "table. Ignoring the error");
			// Don't throw back the error.
			LOGGER.error(errMsg, ex);
		}

	}

	private void updateWorkflowStatus(String gstin, String taxPeriod,
			String returnType, String invoiceType, String jobStatus) {
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"About to update JobStatus in control table for"
							+ "GSTIN: '%s', TaxPeriod: '%s', RetType: '%s', "
							+ "InvType: '%s', Status to '%s'",
					gstin, taxPeriod, returnType, invoiceType, jobStatus);
			LOGGER.debug(msg);
		}
		int rowsAffected = gstr9ControlService.updateWorkflowStatus(jobStatus,
				LocalDateTime.now(), gstin, taxPeriod, returnType, invoiceType);
		if (rowsAffected == 0) {
			String msg = String.format(
					"No record found "
							+ "in the control table for GSTIN: '%s', "
							+ "TaxPeriod: '%s', ReturnType: '%s',"
							+ " InvType: '%s', Status to '%s'",
					gstin, taxPeriod, returnType, invoiceType, jobStatus);
			LOGGER.error(msg);
			throw new AppException(msg);
		}
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Updated Job Status in control table for"
							+ "GSTIN: '%s', TaxPeriod: '%s', RetType: '%s', "
							+ "InvType: '%s', Status to '%s'",
					gstin, taxPeriod, returnType, invoiceType, jobStatus);
			LOGGER.debug(msg);
		}
	}

	private boolean isValidMessage(String gstin, String taxPeriod,
			String returnType, String invoiceType) {
		return !(gstin == null || taxPeriod == null || returnType == null
				|| invoiceType == null);
	}

}
