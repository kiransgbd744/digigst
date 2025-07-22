package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Itc04ItemErrorsEntity;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Repository("Itc04ItemErrorsRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Itc04ItemErrorsRepository
		extends JpaRepository<Itc04ItemErrorsEntity, Long>,
		JpaSpecificationExecutor<Itc04ItemErrorsEntity> {
	
}
