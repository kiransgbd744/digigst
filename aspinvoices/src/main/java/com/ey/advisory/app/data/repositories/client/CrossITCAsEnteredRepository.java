package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.CrossItcAsEnteredEntity;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Repository("CrossITCAsEnteredRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface CrossITCAsEnteredRepository
		extends JpaRepository<CrossItcAsEnteredEntity, Long>,
		JpaSpecificationExecutor<CrossItcAsEnteredEntity> {
}
