package com.ey.advisory.app.data.repositories.client.gstr9;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.gstr9.Gstr9ComputeProcedureEntity;

/**
 * @author Hema G M
 *
 */
@Repository("Gstr9ComputeProcedureRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface Gstr9ComputeProcedureRepository
		extends JpaRepository<Gstr9ComputeProcedureEntity, Long>,
		JpaSpecificationExecutor<Gstr9ComputeProcedureEntity> {

}
