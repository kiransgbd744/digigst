package com.ey.advisory.app.data.services.einvseries;

public interface GSTR1EinvSeriesCompService {

	void compandperstSeriesData(Long configId, String gstin, String retPeriod,String implType);
}
