package com.ey.advisory.app.data.daos.client;

import java.util.List;

import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;

public interface CheckStatusDao {
	
	List<Gstr1SaveBatchEntity> checkStatusSection(String buildQuery,
			List<String> sgstins, String periodFrom, String periodTo);

}
