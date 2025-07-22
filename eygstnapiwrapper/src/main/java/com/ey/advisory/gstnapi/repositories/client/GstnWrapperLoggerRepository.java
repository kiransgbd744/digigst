package com.ey.advisory.gstnapi.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.gstnapi.domain.client.APIInvocationLogger;

/**
 * @author Khalid1.Khan
 *
 */
@Repository("GstnWrapperLoggerRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GstnWrapperLoggerRepository
		extends JpaRepository<APIInvocationLogger, Long>,
		JpaSpecificationExecutor<APIInvocationLogger> {

}
