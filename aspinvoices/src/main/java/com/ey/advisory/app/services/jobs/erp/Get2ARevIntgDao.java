package com.ey.advisory.app.services.jobs.erp;

import java.util.List;

public interface Get2ARevIntgDao {

	public List<Object[]> get2ARevIntgDao(final String gstin, int chunkId);
}
