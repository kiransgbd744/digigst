package com.ey.advisory.app.data.services.common;

import org.javatuples.Pair;
/**
 *
 * @author Jithendra.B
 *
 */
public interface PublicApiLimitUsageService {

	public Pair<String, String> getLimitAndUsageForGroupCode(String groupCode);
}
