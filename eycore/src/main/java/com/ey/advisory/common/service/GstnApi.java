/**
 * 
 */
package com.ey.advisory.common.service;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("GstnApi")
public class GstnApi {

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	/**
	 * This will gives the GSTR1 sandbox active working version which is
	 * configured in DB for the new schema changes to consider.
	 * 
	 * @return
	 */
	/*public String getActiveGstnVersion() {
		Config config = configManager.getConfig("GSTNAPI",
				"api.gstn.id(GSTR1_API_VERSION).active_version");
		return config != null ? config.getValue() : "v1.1";
	}*/
	
	public int getSummryGetRetrySize() {
		Config config = configManager.getConfig("GSTNAPI",
				"api.gstn.id(SUMMARY_RETRY_COUNT).retry_count");
		return Integer.parseInt(config != null ? config.getValue() : "5");
	}
	
	/**
	 * 1 - to invoke return filing status api
	 * 0 - not to invoke return filing status api
	 * @return
	 */
	public String getReturnFilingStatusConfig() {
		Config config = configManager.getConfig("GSTNAPI",
				"api.gstn.id(PUBLIC_API).allow_return_filing_status");
		return config != null && config.getValue() != null ? config.getValue() : "1";
	}
	
	public Boolean isDelinkingEligible(String returnType) {
		if (Strings.isNullOrEmpty(returnType)) {
			LOGGER.error("Invalid Retrun Type found in isDelinkingEligible %s",
					returnType);
			return false;
		}
		Config config = configManager.getConfig("GSTNAPI",
				"api.gstn.id(GSTN_DELINKING).returns");
		return config != null && config.getValue() != null ? Arrays
				.asList(config.getValue().split(",")).contains(returnType)
				: Boolean.FALSE;
	}
	
	public Boolean isRateIncludedInHsn(String taxPeriod) {
		if (taxPeriod == null || StringUtils.isEmpty(taxPeriod)) {
			LOGGER.error("Invalid TaxPeriod found in isRateIncludedInHsn %s",
					taxPeriod);
			return false;
		}
		Integer derivedTaxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriod);
		Config config = configManager.getConfig("GSTNAPI",
				"api.gstn.id(INCLUDE_HSN_RATE).from_period");
		Boolean useStubs = Boolean.FALSE;
		if (config != null && config.getValue() != null) {
			Integer cutOverPeriod = GenUtil
					.convertTaxPeriodToInt(config.getValue());

			useStubs = cutOverPeriod <= derivedTaxPeriod ? Boolean.TRUE
					: Boolean.FALSE;
		}

		return useStubs;
	}
	
	public Boolean isNAConsideredAsUqcValueInHsn(String taxPeriod) {
		if (Strings.isNullOrEmpty(taxPeriod)) {
			LOGGER.error("Invalid TaxPeriod found in isNAConsideredAsUqcValueInHsn %s",
					taxPeriod);
			return false;
		}
		Integer derivedTaxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriod);
		Config config = configManager.getConfig("GSTNAPI",
				"api.gstn.id(CONSIDER_NA_UQC).from_period");
		Boolean useStubs = Boolean.FALSE;
		if (config != null && config.getValue() != null) {
			Integer cutOverPeriod = GenUtil
					.convertTaxPeriodToInt(config.getValue());

			useStubs = cutOverPeriod <= derivedTaxPeriod ? Boolean.TRUE
					: Boolean.FALSE;
		}

		return useStubs;
	}
}
