package com.ey.advisory.app.data.repositories.client.asprecon;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconFailedProcedureEntity;

/**
 * @author Ravindra V S
 *
 */

@Repository("Gstr2ReconFailedProcedureRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface Gstr2ReconFailedProcedureRepository
		extends JpaRepository<Gstr2ReconFailedProcedureEntity, Integer>,
		JpaSpecificationExecutor<Gstr2ReconFailedProcedureEntity> {

	@Query("Select entity from Gstr2ReconFailedProcedureEntity entity WHERE"
			+ " isActive=TRUE")
	public List<Gstr2ReconFailedProcedureEntity> findProcedure();

}
