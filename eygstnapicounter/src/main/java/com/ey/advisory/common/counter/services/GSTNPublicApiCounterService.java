package com.ey.advisory.common.counter.services;

/**
 * @author Jithendra.B
 *
 */
import org.javatuples.Pair;

public interface GSTNPublicApiCounterService {

	public PublicApiLimitDTO getLimitsForGroupCode(String groupCode);

	public String saveLimitsForGroupCode(PublicApiLimitDTO dto);

	public boolean isApiCallAllowedForGroupCode(String groupCode);

	public Pair<Integer, Integer> getUsageAndLimitForGroupCode(
			String groupCode);

	public void decrementUsageCountForGroupCode(String groupCode);

}
