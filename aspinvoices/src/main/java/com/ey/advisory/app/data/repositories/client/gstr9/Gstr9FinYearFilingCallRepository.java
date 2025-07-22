package com.ey.advisory.app.data.repositories.client.gstr9;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.gstr9.Gstr9FinYearFilingCallEntity;

/**
 * @author Hema G M
 *
 */
@Repository("Gstr9FinYearFilingCallRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface Gstr9FinYearFilingCallRepository
		extends JpaRepository<Gstr9FinYearFilingCallEntity, Long>,
		JpaSpecificationExecutor<Gstr9FinYearFilingCallEntity> {

	Gstr9FinYearFilingCallEntity findByFy(Integer fy);
}
