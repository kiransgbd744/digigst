package com.ey.advisory.app.services.credit.reversal;

import java.util.List;

public interface CreditRevForFinancialYearWiseDao {

	public int proceCallForFinancialYear(String gstin, int fromRetPeriod,
			int toRetPeriod);

	public List<Object[]> getReviewReversalFinancialYear(String gstin,
			int fromRetPeriod, int toRetPeriod);

	public List<Object[]> getTurnOverFinaYear(String gstin, int fromRetPeriod,
			int toRetPeriod);
}
