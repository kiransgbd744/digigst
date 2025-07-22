package com.ey.advisory.app.data.repositories.client.asprecon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.Recon3WayToleranceLimitEntity;

/**
 * @author Sakshi.jain
 *
 */

@Repository("Recon3WayToleranceLimitRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface Recon3WayToleranceLimitRepository
		extends JpaRepository<Recon3WayToleranceLimitEntity, Long>,
		JpaSpecificationExecutor<Recon3WayToleranceLimitEntity> {

}
