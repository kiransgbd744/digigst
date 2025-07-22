package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.SaveToGstnEventStatusEntity;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Repository("saveToGstnEventStatusRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface SaveToGstnEventStatusRepository
		extends CrudRepository<SaveToGstnEventStatusEntity, Long> {

	@Query("SELECT e.statusCode FROM SaveToGstnEventStatusEntity e"
			+ " WHERE e.id IN (SELECT MAX(id) FROM SaveToGstnEventStatusEntity "
			+ "e WHERE e.taxPeriod = :retPeriod AND e.gstin = :sgstin)")
	public Integer findStatsCode(@Param("retPeriod") String retPeriod,
			@Param("sgstin") String sgstin);

	@Query("SELECT e.statusCode FROM SaveToGstnEventStatusEntity e"
			+ " WHERE e.id IN (SELECT MAX(id) FROM SaveToGstnEventStatusEntity "
			+ "e WHERE e.taxPeriod = :retPeriod AND e.gstin = :sgstin AND e.section = :section)")
	public Integer findStatsCode(@Param("retPeriod") String retPeriod,
			@Param("sgstin") String sgstin, @Param("section") String section);

}
