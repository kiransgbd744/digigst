package com.ey.advisory.gstnapi.repositories.master;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.gstnapi.domain.master.GstnAPIGroupConfig;

@Repository("GstnAPIGroupConfigRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GstnAPIGroupConfigRepository extends 
				JpaRepository<GstnAPIGroupConfig, Long>, 
				JpaSpecificationExecutor<GstnAPIGroupConfig> {

	/**
	 * Find the API related Group Configuration information for the specified
	 * group code.
	 * 
	 * @param groupCode
	 * @return
	 */
	public GstnAPIGroupConfig findByGroupCode(String groupCode);
}
