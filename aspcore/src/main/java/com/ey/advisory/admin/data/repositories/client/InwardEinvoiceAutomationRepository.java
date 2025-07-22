package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.InwardEinvoiceAutomationEntity;

/**
 * @author Sakshi.jain
 *
 */
@Repository("InwardEinvoiceAutomationRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface InwardEinvoiceAutomationRepository
		extends CrudRepository<InwardEinvoiceAutomationEntity, Long> {

	List<InwardEinvoiceAutomationEntity> findByIsDeleteFalseOrderByEntityIdDesc();

	@Modifying
	@Transactional
	@Query("UPDATE InwardEinvoiceAutomationEntity SET isDelete=true WHERE "
			+ "entityId=:entityId and isDelete=false")
	void updateInActiveInwardEinvoiceBasedOnEntityId(@Param("entityId") Long entityId);

	@Query("Select e from  InwardEinvoiceAutomationEntity e where "
			+ " e.entityId=:entityId and e.isDelete=false")
 List <InwardEinvoiceAutomationEntity>  getAutomationEntity (@Param("entityId") Long entityId);

}
