package com.ey.advisory.app.data.repositories.client.asprecon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconProcedureEntity;

/**
 * @author vishal.verma
 *
 */

@Repository("Gstr2ReconProcedureRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface Gstr2ReconProcedureRepository
		extends JpaRepository<Gstr2ReconProcedureEntity, Long>,
		JpaSpecificationExecutor<Gstr2ReconProcedureEntity> {

}
