package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GstinSourceInfoEntity;


/**
 * @author vishal.verma
 *
 */

@Repository("GstinSourceInfoRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface GstinSourceInfoRepository
		extends JpaRepository<GstinSourceInfoEntity, Long>,
		JpaSpecificationExecutor<GstinSourceInfoEntity> {

	@Query("SELECT e.sourceId FROM GstinSourceInfoEntity e WHERE "
			+ "e.gstin=:gstin ")
	public String findByGstin(@Param("gstin") String gstin);

}
