package com.ey.advisory.gstnapi;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.APIPersistenceManager;
import com.ey.advisory.common.AppException;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.GSTNAPIUtil;
import com.ey.advisory.core.api.impl.APIExecParties;
import com.ey.advisory.core.api.impl.APIExecPartiesBuilder;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.gstnapi.domain.client.GstnAPIGstinConfig;
import com.ey.advisory.gstnapi.domain.master.GstnAPIGroupConfig;

@Component("APIExecPartiesBuilderImpl")
public class APIExecPartiesBuilderImpl implements APIExecPartiesBuilder {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(APIExecPartiesBuilderImpl.class);

	@Autowired
	@Qualifier("APIDBPersistenceManagerImpl")
	private APIPersistenceManager persistenceManager;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	GSTNCertificate cert;

	@Autowired
	@Qualifier("DefaultAPIGstinGroupCodeLocator")
	private APIGstinGroupLocator apiGstinGroupLocator;

	/**
	 * Build the APIExecParties containing all the relevant information about
	 * the 3 parties involved in the execution of the API.
	 */
	@Override
	public APIExecParties build(APIParams params) {

		try {

			// First build the GSTN Provider config.
			GSTNAPIProvider apiProvider = new GSTNAPIProvider(
					cert.getGSTNCertificate());

			// After building the GSTN Provider, build the Channel Config.
			EYGSTNChannel apiChannel = buildChannelConfig();

			// After building the Provider Config and Channel Config, build
			// the API End user Config.
			GSTNAPIEndUser apiEndUser = buildEndUserConfig(params);

			// Return the APIExecParties object with all the party details.
			return new APIExecParties(apiProvider, apiChannel, apiEndUser);

		} catch (Exception ex) {
			String errorMsg = "Exception while building auth info";
			LOGGER.error(errorMsg, ex);
			throw new APIException(errorMsg);
		}
	}

	private EYGSTNChannel buildChannelConfig() {
		Map<String, Config> gspConfig = this.configManager.getConfigs("GSTNAPI",
				"api.gstn.global");
		return new EYGSTNChannel(
				gspConfig.get("api.gstn.global.client_id").getValue(),
				gspConfig.get("api.gstn.global.client_secret").getValue(),
				gspConfig.get("api.gstn.global.ey_public_ip").getValue());
	}

	/**
	 * Since GSTIN is the End User for executing the APIs, we need to load the
	 * GSTIN details for the end user.
	 * 
	 * @param params
	 * @return
	 */
	private GSTNAPIEndUser buildEndUserConfig(APIParams params) {

		// Get the GSTIN value of the user.
		String gstin = apiGstinGroupLocator.locateGstin(params);

		// if GSTIN is not present in the API param Values, then throw an
		// exception.
		if (gstin == null || gstin.trim().isEmpty()) {
			String msg = "GSTIN is not present in the " + "API Params => "
					+ params;
			LOGGER.error(msg);
			throw new AppException(msg);
		}

		String identifier = params.getApiIdentifier();

		// Load all GSTIN related information from the repository.
		GstnAPIGstinConfig apiGstinCfg = GSTNAPIUtil.isPublicApiRelatedRequest(
				identifier) ? GstnAPIGstinConfig.getPublicApiInstance()
						: persistenceManager.loadAPIGStinConfig(gstin);

		// Check if the loaded GSTIN Configuraiton is null. If so, throw an
		// Exception and end the process right away.
		if (apiGstinCfg == null) {
			String msg = String.format(
					"GSTIN information for GSTIN: '%s' "
							+ "is not configured. These details are required for"
							+ "framing the headers to execute the GSTN API",
					gstin);
			LOGGER.error(msg);
			throw new AppException(msg);
		}

		// Get the group code for the Gstin.
		String groupCode = apiGstinCfg.getGroupCode();

		if (groupCode == null || groupCode.trim().isEmpty()) {
			String msg = String.format("Group Code is not configured as part "
					+ "of the GstnAPIGstinConfig domain object for "
					+ "the GSTIN: '%s'.", gstin);
			LOGGER.error(msg);
			throw new AppException(msg);
		}

		// Load all Group related information from the repository.
		GstnAPIGroupConfig apiGrpCfg = persistenceManager
				.loadAPIGroupConfig(groupCode);

		// Check if the loaded GSTIN Configuraiton is null. If so, throw an
		// Exception and end the process right away.
		if (apiGrpCfg == null) {
			String msg = String.format("Group information for GroupCode: '%s' "
					+ "is not configured while trying to execute GSTN API for "
					+ "the GSTIN '%s'. These details are required for"
					+ "framing the headers to execute the GSTN API "
					+ "(passing through GSP)", groupCode, gstin);

			LOGGER.error(msg);
			throw new AppException(msg);
		}

		// Return the new GSTN API End User details with the gstin and
		// Group information available at this point.
		return new GSTNAPIEndUser(apiGstinCfg, apiGrpCfg);

	}

}
