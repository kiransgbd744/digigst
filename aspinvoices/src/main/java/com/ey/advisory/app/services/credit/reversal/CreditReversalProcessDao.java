package com.ey.advisory.app.services.credit.reversal;

import java.util.List;

public interface CreditReversalProcessDao {

	public int proceCallForComputeReversal(final String gstin,
			final Integer retPeriod);

	public List<Object[]> getCreditReversalProcess(final List<String> gstinList,
			final Integer retPeriod);

	public List<Object[]> getCreditReverseSummary(final List<String> gstinList,
			final Integer retPeriod);

	public List<Object[]> getCreditReversalAndTurnvolProcess(
			final String processType, final String gstin,
			final Integer retPeriod);

	public List<Object[]> getGstr3BRuleCompute(final List<String> gstin,
			final Integer retPeriod, final String subSectionName);

}
