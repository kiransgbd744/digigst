package com.ey.advisory.app.data.repositories.client.asprecon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.Gstr2Recon2BPRProcedureTestEntity;

/**
 * @author Shashikant.Shukla
 *
 */

@Repository("Gstr2Recon2BPRProcedureTestRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface Gstr2Recon2BPRProcedureTestRepository
		extends JpaRepository<Gstr2Recon2BPRProcedureTestEntity, Long>,
		JpaSpecificationExecutor<Gstr2Recon2BPRProcedureTestEntity> {
	
//	@Query("SELECT procEntity from Gstr2Recon2BPRProcedureTestEntity procEntity "
//			+ "where reportName =:reportName ")
//	public List<Gstr2Recon2BPRProcedureTestEntity> getLogicalProc(@Param("reportName") String reportName);
//	
//	@Query("SELECT procName from Gstr2Recon2BPRProcedureTestEntity "
//			+ "where reportName =:reportName ")
//	public String getProcName(@Param("reportName") String reportName);
}
