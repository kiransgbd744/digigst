package com.ey.advisory.app.data.repositories.client.drc;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.drc.TblDrcDetails;

/**
 * 
 * @author Siva.Reddy
 *
 */

@Repository("TblDrcDetailsRepo")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface TblDrcDetailsRepo extends JpaRepository<TblDrcDetails, Long>,
		JpaSpecificationExecutor<TblDrcDetails> {

	public List<TblDrcDetails> findByGstinInAndTaxPeriodAndIsActiveTrue(
			List<String> gstin, String taxPeriod);

	public TblDrcDetails findByGstinAndTaxPeriodAndIsActiveTrue(String gstin,
			String taxPeriod);

	public List<TblDrcDetails> findByGstinInAndTaxPeriodAndIsActiveTrueAndRefId(
			List<String> gstin, String taxPeriod, String refId);

	@Modifying
	@Query("UPDATE TblDrcDetails g SET g.isActive = false,g.modifiedOn = CURRENT_TIMESTAMP,g.modifiedBy = :userName "
			+ "WHERE g.gstin = :gstin and g.taxPeriod = :taxPeriod and g.isActive = true")
	public int inActivateRecords(@Param("gstin") String gstins,
			@Param("taxPeriod") String taxPeriod,
			@Param("userName") String userName);

}
