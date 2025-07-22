package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.inward.einvoice.InwardEInvoiceRevIntgReqDto;
import com.ey.advisory.app.inward.einvoice.InwardEInvoiceRevIntgService;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Vishal.verma
 *
 */
@Slf4j
@Component("InwardEInvoiceRevIntgProcessor")
public class InwardEInvoiceRevIntgProcessor implements TaskProcessor {

	/*@Autowired
	@Qualifier("GSTR2aAutoReconRevIntgServiceImpl")
	private GSTR2aAutoReconRevIntgService service;*/
	
	@Autowired
	@Qualifier("InwardEInvoiceRevIntgServiceImpl")
	private InwardEInvoiceRevIntgService service;
	
	@Override
	public void execute(Message message, AppExecContext context) {

		LOGGER.debug("Begin InwardEInvoiceRevIntgProcessor job");

		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"InwardEInvoiceRevIntgProcessor Job"
							+ " executing for groupcode %s and params %s",
					groupCode, jsonString);
			LOGGER.debug(msg);
		}
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			InwardEInvoiceRevIntgReqDto dto = gson.fromJson(jsonString,
					InwardEInvoiceRevIntgReqDto.class);
			dto.setGroupCode(groupCode);
			LOGGER.debug(
					"InwardEInvoiceRevIntgProcessor status is in progress ");
			
			Integer respcode = service.getInwardEInvoiceErpPush(dto);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(" Inward EInvoice  ERP Push response code is {}",
						respcode);
			}
		} catch (Exception e) {
			String msg = "InwardEInvoiceRevIntgProcessor got interrupted. ";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}
	}

}
