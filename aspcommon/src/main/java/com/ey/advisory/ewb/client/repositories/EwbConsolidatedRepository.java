/**
 * 
 */
package com.ey.advisory.ewb.client.repositories;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.ewb.client.domain.EwbConsolidateEntity;

/**
 * @author Arun.KA
 *
 */
@Repository("EwbConsolidatedRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
@Qualifier("EwbConsolidatedRepository")
public interface EwbConsolidatedRepository extends JpaRepository<EwbConsolidateEntity, Long>,
		JpaSpecificationExecutor<EwbConsolidateEntity> {

}
