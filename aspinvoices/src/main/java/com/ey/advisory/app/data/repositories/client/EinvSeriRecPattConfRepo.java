/**
 * 
 */
package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.EinvSeriRecPattConfEntity;

/**
 * @author Siva.Reddy
 *
 */
@Repository("EinvSeriRecPattConfRepo")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface EinvSeriRecPattConfRepo
		extends CrudRepository<EinvSeriRecPattConfEntity, Long> {

	@Modifying
	@Query("UPDATE EinvSeriRecPattConfEntity log SET log.isDelete = true,"
			+ "log.modifiedOn = CURRENT_TIMESTAMP,log.modifiedBy = 'SYSTEM' "
			+ "WHERE  log.pattern IN (:patterns) and log.isDelete = false")
	void updateExistPatterns(@Param("patterns") List<String> patterns);

	EinvSeriRecPattConfEntity findByPatternAndIsDeleteFalse(String Pattern);
}
