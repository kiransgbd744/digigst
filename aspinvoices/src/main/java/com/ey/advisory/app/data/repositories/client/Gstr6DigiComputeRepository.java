package com.ey.advisory.app.data.repositories.client;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.gstr6.Gstr6DigiComputeEntity;

/**
 * @author Shashikant Shukla
 *
 */
@Repository("Gstr6DigiComputeRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface Gstr6DigiComputeRepository
		extends JpaRepository<Gstr6DigiComputeEntity, Long>,
		JpaSpecificationExecutor<Gstr6DigiComputeEntity> {

	@Query("SELECT doc FROM Gstr6DigiComputeEntity doc "
			+ "WHERE doc.custGstinAsp =:gstin AND doc.taxPeriodAsp =:taxPeriod AND doc.isDelete = false ")
	public List<Gstr6DigiComputeEntity> findByGstinAndTaxPeriod(
			@Param("gstin") String gstin, @Param("taxPeriod") String taxPeriod);

	@Query("SELECT distinct doc.onbANSWER FROM Gstr6DigiComputeEntity doc "
			+ "WHERE doc.isDelete = FALSE AND doc.custGstinAsp = :gstin AND"
			+ " doc.taxPeriodAsp = :retPeriod")
	public Optional<String> findOptionOpted(
			@Param("gstin") String gstin, @Param("retPeriod") String retPeriod);

}
