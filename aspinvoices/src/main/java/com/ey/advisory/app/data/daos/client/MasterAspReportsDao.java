package com.ey.advisory.app.data.daos.client;

import java.util.List;

import com.ey.advisory.app.data.entities.simplified.client.MasterAspReportsEntity;

public interface MasterAspReportsDao {

	public List<MasterAspReportsEntity> getMasterAspReports() throws Exception;

	List<Object[]> loadSupplyTypeDetails(String reportsKey);

	List<Object[]> loadDocumentTypeDetails(String reportsKey);

	List<Object[]> loadAttributes(String reportsKey);
}
