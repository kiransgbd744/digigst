package com.ey.advisory.processors.test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.docs.InvoiceFileArrivalHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.TaskProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("InvoiceFileArrivalProcessor")
@Slf4j
public class InvoiceFileArrivalProcessor implements TaskProcessor {
	@Autowired
	@Qualifier("InvoiceFileArrivalHandler")
	private InvoiceFileArrivalHandler  invoiceFileArrivalHandler;

	@Override
	public void execute(Message message, AppExecContext context) {
		try{
			LOGGER.debug("Invoice file arrived");
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"INVOICE_SERICE_FILE_ARRIVAL_PROCESSOR_START",
					"InvoiceFileArrivalProcessor", "execute",
					null);
			invoiceFileArrivalHandler.processInvoiceFile(message,context);
			PerfUtil.logEventToFile(PerfamanceEventConstants.FILE_PROCESSING,
					"INVOICE_SERICE_FILE_ARRIVAL_PROCESSOR_END",
					"InvoiceFileArrivalProcessor", "execute",
					null);
			LOGGER.debug("Invoice file Arrival processed");
		}
		catch(AppException e){
			LOGGER.debug("Exception occured while Invoice Processor" , e);
			throw new AppException();
		}
		
	}

}
