package com.ey.advisory.app.services.savetogstn.jobs.gstr3B;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Service("saveGstr3BDataFetcherImpl")
public class Gstr3BSaveDataFetcherImpl implements Gstr3BSaveDataFetcher {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr3BSaveDataFetcherImpl.class);

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	@Override
	public List<Object[]> findInvoiceLevelData(String jsonString,
			String groupCode) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		String gstin = requestObject.get("gstin").getAsString();
		String retPeriod = requestObject.get("retPeriod").getAsString();
		List<Object[]> result = null;
		try {
			/**
			 * This method is used to handle Gstr3B SaveToGstn Operation from
			 * different screens(different input criteria), using with the
			 * screen given criteria we can fetch the reurnPeriod and gstin
			 * because SaveToGstn operation is meant at return period and gstin
			 * level.
			 **/

			if (gstin != null && !gstin.isEmpty() && retPeriod != null
					&& !retPeriod.isEmpty()) {
				TenantContext.setTenantId(groupCode);
				LOGGER.info("groupCode {} is set", groupCode);

				result = docRepository
						.findGstr3BInvoiceLevelData(gstin, retPeriod);
			}

			if (result != null) {
				LOGGER.info("{} docs are eligible to do Save To Gstn.",
						result.size());
			}
		} catch (Exception ex) {
			LOGGER.error("Exception while fetching the GSTR3B data ", ex);
			throw new AppException("Error in fetching the gstn save Data", ex);

		} 
		return result;
	}
	
	
	@Override
	public List<Object[]> findInvoiceGstnLevelData(String jsonString,
			String groupCode) {

		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();
		String gstin = requestObject.get("gstin").getAsString();
		String retPeriod = requestObject.get("retPeriod").getAsString();
		List<Object[]> result = null;
		try {
			/**
			 * This method is used to handle Gstr3B SaveToGstn Operation from
			 * different screens(different input criteria), using with the
			 * screen given criteria we can fetch the reurnPeriod and gstin
			 * because SaveToGstn operation is meant at return period and gstin
			 * level.
			 **/

			if (gstin != null && !gstin.isEmpty() && retPeriod != null
					&& !retPeriod.isEmpty()) {
								result = docRepository
						.findGstr3BInvoiceGstnLevelData(gstin, retPeriod);
			}

			if (result != null) {
				LOGGER.info("{} docs are eligible to do Save To Gstn.",
						result.size());
			}
		} catch (Exception ex) {
			LOGGER.error("Exception while fetching the GSTR3B data ", ex);
			throw new AppException("Error in fetching the gstn save Data", ex);

		} 
		return result;
	}

}
