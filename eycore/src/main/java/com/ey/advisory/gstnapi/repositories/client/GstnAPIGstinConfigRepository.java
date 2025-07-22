package com.ey.advisory.gstnapi.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.gstnapi.domain.client.GstnAPIGstinConfig;

@Repository("GstnAPIGstinConfigRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GstnAPIGstinConfigRepository extends 
			JpaRepository<GstnAPIGstinConfig, Long>, 
			JpaSpecificationExecutor<GstnAPIGstinConfig> {
	
	/**
	 * Load the API Gstin Configuration for the specified Gstin.
	 * 
	 * @param gstin
	 * @return
	 */
	public GstnAPIGstinConfig findByGstin(String gstin);
	
}
