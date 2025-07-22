package com.ey.advisory.app.common;

import java.util.List;

import com.ey.advisory.app.data.entities.client.GstinValidatorEntity;

public interface TaxPayerConfigDetailsDao {
	
	public List<GstinValidatorEntity> 
			getTaxPayerConfigDetailsFromDb(boolean einvApplicable);

}
