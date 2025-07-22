package com.ey.advisory.app.data.repositories.client.gstr9;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.gstr9.Gstr9ComputeDigiProcedureEntity;

/**
 * @author Shashikant Shukla
 *
 */
@Repository("Gstr9ComputeDigiProcedureRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface Gstr9ComputeDigiProcedureRepository
		extends JpaRepository<Gstr9ComputeDigiProcedureEntity, Long>,
		JpaSpecificationExecutor<Gstr9ComputeDigiProcedureEntity> {

}
