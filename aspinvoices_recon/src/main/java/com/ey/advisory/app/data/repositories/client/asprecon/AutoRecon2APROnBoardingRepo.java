package com.ey.advisory.app.data.repositories.client.asprecon;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.AutoRecon2APROnBoardingEntity;

/**
 * 
 * @author Siva.Reddy
 *
 */

@Repository("AutoRecon2APROnBoardingRepo")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface AutoRecon2APROnBoardingRepo
		extends CrudRepository<AutoRecon2APROnBoardingEntity, Long> {

	@Modifying
	@Query("UPDATE AutoRecon2APROnBoardingEntity g SET g.isActive = false,g.modifiedDate = CURRENT_TIMESTAMP,"
			+ "g.modifiedBy =:modifiedBy "
			+ "WHERE g.entityId = :entityId and g.isActive = true")
	public int updateActiveExistingRecords(@Param("entityId") String entityId,
			@Param("modifiedBy") String updatedBy);

	public List<AutoRecon2APROnBoardingEntity> findByEntityIdAndIsActiveTrue(
			String entityId);

}
