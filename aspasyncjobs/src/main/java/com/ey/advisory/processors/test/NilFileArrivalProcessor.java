package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.docs.NilFileArrivalHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;

import lombok.extern.slf4j.Slf4j;

@Component("NilFileArrivalProcessor")
@Slf4j
public class NilFileArrivalProcessor implements TaskProcessor {
	@Autowired
	@Qualifier("NilFileArrivalHandler")
	private NilFileArrivalHandler nilFileArrivalHandler;

	/*@Autowired
	@Qualifier("ComprehensiveEinvoicePopProcedureImpl")
	private ComprehensiveEinvoicePopProcedureImpl comprehensiveEinvoicePopProcedureImpl;*/

	@Override
	public void execute(Message message, AppExecContext context) {
		try {
			LOGGER.debug("Nil/exempted/non file has Arrived");
			nilFileArrivalHandler.processNilFile(message, context);
			//comprehensiveEinvoicePopProcedureImpl.procNilNonExmptCall(message);
			LOGGER.debug("Nil/exempted/non file has arrival process");
		} catch (AppException e) {
			LOGGER.error("Exception while in Processor Nil Non Ext", e);
			throw new AppException();

		}
	}

}
