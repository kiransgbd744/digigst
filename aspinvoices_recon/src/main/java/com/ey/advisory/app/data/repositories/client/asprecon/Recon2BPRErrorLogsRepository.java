package com.ey.advisory.app.data.repositories.client.asprecon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.asprecon.gstr2.initiaterecon.Recon2BPRErrorLogEntity;

/**
 * @author vishal.verma
 *
 */

@Repository("Recon2BPRErrorLogsRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface Recon2BPRErrorLogsRepository
		extends JpaRepository<Recon2BPRErrorLogEntity, Long>,
		JpaSpecificationExecutor<Recon2BPRErrorLogEntity> {

}
