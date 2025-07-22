package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.gstr6.Gstr6DigiComputeDistributionEntity;

/**
 * @author Shashikant Shukla
 *
 */
@Repository("Gstr6DigiComputeDistributionRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface Gstr6DigiComputeDistributionRepository
		extends JpaRepository<Gstr6DigiComputeDistributionEntity, Long>,
		JpaSpecificationExecutor<Gstr6DigiComputeDistributionEntity> {

	@Query("SELECT doc FROM Gstr6DigiComputeDistributionEntity doc "
			+ "WHERE doc.isdGstinAsp =:gstin AND doc.taxPeriodAsp =:taxPeriod AND doc.isDelete = false ")
	public List<Gstr6DigiComputeDistributionEntity> findByGstinAndTaxPeriod(
			@Param("gstin") String gstin, @Param("taxPeriod") String taxPeriod);
	
}
