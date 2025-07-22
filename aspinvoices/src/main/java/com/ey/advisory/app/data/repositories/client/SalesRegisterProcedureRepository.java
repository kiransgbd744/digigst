package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.service.gstr1.sales.register.SalesRegisterProcedureEntity;

/**
 * @author Shashikant.Shukla
 *
 */

@Repository("SalesRegisterProcedureRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface SalesRegisterProcedureRepository
		extends JpaRepository<SalesRegisterProcedureEntity, Long>,
		JpaSpecificationExecutor<SalesRegisterProcedureEntity> {
	
	@Query("Select entity from SalesRegisterProcedureEntity entity WHERE"
			+ " isActive=TRUE")
	public List<SalesRegisterProcedureEntity> findProcedure();

}
