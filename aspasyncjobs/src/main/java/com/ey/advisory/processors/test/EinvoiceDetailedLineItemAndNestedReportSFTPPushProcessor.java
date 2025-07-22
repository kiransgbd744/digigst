package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.asprecon.InwardEInvoiceERPRequestRepository;
import com.ey.advisory.app.inward.einvoice.EinvoiceDetailedLineItemAndNestedReportSFTPService;
import com.ey.advisory.app.inward.einvoice.InwardEInvoiceRevIntgReqDto;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Service("EinvoiceDetailedLineItemAndNestedReportSFTPPushProcessor")
public class EinvoiceDetailedLineItemAndNestedReportSFTPPushProcessor
		implements TaskProcessor {

	@Autowired
	@Qualifier("InwardEInvoiceERPRequestRepository")
	InwardEInvoiceERPRequestRepository revIntCheckRepo;

	@Autowired
	@Qualifier("EinvoiceDetailedLineItemAndNestedReportSFTPService")
	EinvoiceDetailedLineItemAndNestedReportSFTPService service;

	@Override
	public void execute(Message message, AppExecContext context) {

		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();

		Gson gson = GsonUtil.newSAPGsonInstance();

		try {

			InwardEInvoiceRevIntgReqDto dto = gson.fromJson(jsonString,
					InwardEInvoiceRevIntgReqDto.class);
			dto.setGroupCode(groupCode);
			LOGGER.debug(
					"EinvoiceDetailedLineItemAndNestedReportSFTPPushProcessor"
							+ " - SFTP Push status is in progress ");

			revIntCheckRepo.updateStatusByBatchId(dto.getBatchId(),
					"INPROGRESS", false);

			if (LOGGER.isDebugEnabled()) {
				String msg = "Updated  status as 'INPROGRESS'";
				LOGGER.debug(msg);
			}
			int resp = service.generateAndPushReports(dto.getBatchId());

			revIntCheckRepo.updateStatusByBatchId(dto.getBatchId(), "SUCCESS", true);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(" Inward EInvoice  SFTP Push response code is {}",
						resp);
			}

		} catch (Exception ex) {
			String msg = "Exception occured while pushing csv"
					+ " report to SFTP - "
					+ "EinvoiceDetailedLineItemAndNestedReportSFTPPushProcessor";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

	}

}
