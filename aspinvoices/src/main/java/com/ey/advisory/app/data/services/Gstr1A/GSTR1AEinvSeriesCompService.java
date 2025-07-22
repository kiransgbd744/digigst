package com.ey.advisory.app.data.services.Gstr1A;

public interface GSTR1AEinvSeriesCompService {

	void compandperstSeriesData(Long configId, String gstin, String retPeriod,
			String implType);
}
