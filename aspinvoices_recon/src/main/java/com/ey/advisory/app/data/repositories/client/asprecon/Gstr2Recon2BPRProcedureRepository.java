package com.ey.advisory.app.data.repositories.client.asprecon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.Gstr2Recon2BPRProcedureEntity;

/**
 * @author vishal.verma
 *
 */

@Repository("Gstr2Recon2BPRProcedureRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface Gstr2Recon2BPRProcedureRepository
		extends JpaRepository<Gstr2Recon2BPRProcedureEntity, Long>,
		JpaSpecificationExecutor<Gstr2Recon2BPRProcedureEntity> {

}
