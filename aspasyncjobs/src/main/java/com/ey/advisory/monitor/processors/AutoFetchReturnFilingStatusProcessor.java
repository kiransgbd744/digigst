package com.ey.advisory.monitor.processors;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.services.noncomplaintvendor.NonComplaintVendorCommunicationServiceImpl;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.core.async.domain.master.Group;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("AutoFetchReturnFilingStatusProcessor")
public class AutoFetchReturnFilingStatusProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	@Qualifier("NonComplaintVendorCommunicationServiceImpl")
	private NonComplaintVendorCommunicationServiceImpl nonComplaintVendorCommunicationServiceImpl;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinRepo;

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(jsonString);
		String returnType = json.get("returnType").getAsString();
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("InitiateGetVendorFilingStatusProcessor Job"
								+ " executing for params %s", jsonString);
				LOGGER.debug(msg);
			}

			String financialYear = json.get("financialYear").getAsString();
			String complianceType = "MyCompliance";
			List<String> registrationType = new ArrayList<String>();
			if (returnType.equalsIgnoreCase("GSTR1,GSTR3B")
					|| returnType.equalsIgnoreCase("GSTR9")
					|| returnType.equalsIgnoreCase("ITC04")) {
				registrationType.add("REGULAR");
				registrationType.add("SEZ");
				registrationType.add("SEZU");
				registrationType.add("SEZD");
			} else if (returnType.equalsIgnoreCase("GSTR6")) {
				registrationType.add("ISD");
			} else if (returnType.equalsIgnoreCase("GSTR1")) {
				registrationType.add("TDS");
			}
			List<String> vendorGstins = gstinRepo
					.getGstinBasedOnRegistraionType(registrationType);

			if (vendorGstins.isEmpty()) {
				LOGGER.info("There is No Gstin Onborded for ", returnType);
			}
			nonComplaintVendorCommunicationServiceImpl
					.persistGstnApiForSelectedFinancialYear(financialYear,
							vendorGstins, complianceType);
		} catch (Exception e) {
			LOGGER.error(
					"Exception while processing the vendor filing request:", e);
		}
	}
}
