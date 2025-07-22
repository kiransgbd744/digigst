package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.ImsSaveAutomationEntity;

/**
 * @author Ravindra V S
 *
 */
@Repository("ImsAutoSaveRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface ImsAutoSaveRepository
		extends CrudRepository<ImsSaveAutomationEntity, Long> {

	List<ImsSaveAutomationEntity> findByIsDeleteFalseOrderByEntityIdDesc();

	@Modifying
	@Transactional
	@Query("UPDATE ImsSaveAutomationEntity SET isDelete=true WHERE "
			+ " isDelete=false")
	void updateInActiveIms();

	@Query("Select e from  ImsSaveAutomationEntity e where "
			+ " e.isDelete=false")
	List<ImsSaveAutomationEntity> getAutomationEntity();

}
