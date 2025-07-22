package com.ey.advisory.admin.publicapi.limit.service;

import org.javatuples.Pair;

/**
 * 
 * 
 * @author Jithendra.B
 *
 */
public interface PublicApiLimitService {

	public Pair<String, String> getLimitsForGroupCode(String groupCode);

	public Pair<String, String> saveLimitsForGroupCode(PublicApiLimitDTO dto);

}
