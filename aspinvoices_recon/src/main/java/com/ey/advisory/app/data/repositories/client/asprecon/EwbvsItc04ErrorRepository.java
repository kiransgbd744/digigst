package com.ey.advisory.app.data.repositories.client.asprecon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.reconewbvsitc04.ErrorLogItemEntity;

/**
 * @author Ravindra V S
 *
 */

@Repository("EwbvsItc04ErrorRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface EwbvsItc04ErrorRepository
		extends JpaRepository<ErrorLogItemEntity, Long>,
		JpaSpecificationExecutor<ErrorLogItemEntity> {

}
