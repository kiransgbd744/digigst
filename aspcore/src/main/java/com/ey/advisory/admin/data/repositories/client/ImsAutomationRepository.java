package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.ImsAutomationEntity;

/**
 * @author Shashikant.Shukla
 *
 */
@Repository("ImsAutomationRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface ImsAutomationRepository
		extends CrudRepository<ImsAutomationEntity, Long> {

	List<ImsAutomationEntity> findByIsDeleteFalseOrderByEntityIdDesc();

	@Modifying
	@Transactional
	@Query("UPDATE ImsAutomationEntity SET isDelete=true WHERE "
			+ " isDelete=false")
	void updateInActiveIms();

	@Query("Select e from  ImsAutomationEntity e where "
			+ " e.isDelete=false")
	List<ImsAutomationEntity> getAutomationEntity();

}
