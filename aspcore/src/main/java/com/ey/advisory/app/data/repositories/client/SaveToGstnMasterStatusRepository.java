package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.SaveToGstnMasterStatusEntity;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Repository("saveToGstnMasterStatusRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface SaveToGstnMasterStatusRepository extends 
						CrudRepository<SaveToGstnMasterStatusEntity, Integer> {

	@Query("SELECT m.status FROM SaveToGstnMasterStatusEntity m"
			+ " WHERE m.statusCode = :statusCode")
	public String findByCode(@Param("statusCode") int statusCode);

}
