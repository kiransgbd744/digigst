package com.ey.advisory.app.service.reconresponse;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.common.eyfileutils.tabular.RowHandler;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

import lombok.extern.slf4j.Slf4j;

@Component("UpdateReconResponseRowHandler")
@Scope("prototype")
@Slf4j
public class UpdateReconResponseRowHandler implements RowHandler {

	private static final int CHUNK_SIZE = 10000;

	@Autowired
	@Qualifier("GenerateErrorReportImpl")
	private GenerateErrorReport generateErrorReport;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	Gstr1FileStatusRepository fileStatusRepository;

	private List<GetReconResponseValidDto> chunk = new ArrayList<GetReconResponseValidDto>(
			CHUNK_SIZE);

	@Autowired
	@Qualifier("DefaultReconUserResponseValidator")
	private ObjectFactory<ReconUserResponseValidator> processorFactory;

	private ReconUserResponseValidator processor;

	private int objCount = 0;
	private Long requestId = 0L;
	private Long fileId = 0L;
	private Long totalCount = 0L;

	@Override
	public boolean handleRow(int rowNo, Object[] row,
			TabularDataLayout layout) {
		ReconUserResponseValidator processor = getValidatorInstance();
		GetReconResponseValidDto dto = createReconRespValidDto(row);
		dto.setTaxPeriod(dto.getTaxPeriod().replace("'", ""));
		requestId = dto.getRequestId();
		chunk.add(dto);
		objCount++; // Increment the object count.
		totalCount++;
		if (objCount >= CHUNK_SIZE) {

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Processing chunk data with requestID"
								+ " : %s , fileId : %s ",
						requestId.toString(), fileId.toString());
				LOGGER.debug(msg);
			}
			processor.validateAndProcessChunk(chunk, fileId);
			chunk.clear(); // Remove all elements.
			objCount = 0; // Reset the object count.
		}
		return true;
	}

	private ReconUserResponseValidator getValidatorInstance() {
		if (processor == null)
			processor = processorFactory.getObject();
		return processor;
	}

	public void processPendingObjects() {

		LOGGER.debug("Processing pending objects ");
		if (chunk.size() > 0) {
			processor.validateAndProcessChunk(chunk, fileId);
		}
		Long errorCount = processor.getErrorCount();

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Total error count after processing the file: %s",
					errorCount.toString());
			LOGGER.debug(msg);
		}

		if (errorCount > 0) {
			Long reqId = requestId;

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Generating and uploading error report "
								+ "with RequestId : %s and fileId : %s",
						reqId.toString(), fileId.toString());
				LOGGER.debug(msg);
			}
			generateErrorReport.uploadErrorReport(reqId, fileId);
		}
		Integer total = Integer.valueOf(totalCount.intValue());
		Integer error = Integer.valueOf(errorCount.intValue());
		Integer processedCount = total - error;

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Updating Records count in file status "
							+ " , totalRecord : %s , "
							+ "processedRecord : %s, errorRecord : %s",
					total.toString(), processedCount.toString(),
					error.toString());
			LOGGER.debug(msg);
		}
		fileStatusRepository.updateCountSummary(fileId, total, processedCount,
				error);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Updating file status as processed with " + "fileID : %s",
					fileId.toString());
			LOGGER.debug(msg);
		}
		fileStatusRepository.updateFileStatus(fileId, "Processed");

	}

	private GetReconResponseValidDto createReconRespValidDto(Object[] arr) {

		GetReconResponseValidDto obj = new GetReconResponseValidDto();
		obj.setUserResponse((arr[0] != null) ? arr[0].toString() : null);
		obj.setReportType((arr[2] != null) ? arr[2].toString() : null);
		obj.setTaxPeriod(
				(arr[17] != null) ? arr[17].toString().replace("`", "") : null);
		obj.setGstin(!arr[22].toString().isEmpty()
				? (String) arr[22] : (String) arr[23]);
		String reqId = (arr[136] != null) ? arr[136].toString().replace("`", "")
				: "0000";
		Long requestId = Long.valueOf(reqId);
		obj.setRequestId("".equals(arr[136]) ? null : requestId);
		obj.setIdPR((arr[137] != null) ? arr[137].toString() : null);
		obj.setIdA2((arr[138] != null) ? arr[138].toString() : null);
		obj.setInvoiceKeyPR("".equals(arr[139]) ? null : arr[139].toString());
		obj.setInvoiceKeyA2("".equals(arr[140]) ? null : arr[140].toString());

		return obj;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

}
