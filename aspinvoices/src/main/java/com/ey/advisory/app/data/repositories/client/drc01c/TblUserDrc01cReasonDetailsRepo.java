package com.ey.advisory.app.data.repositories.client.drc01c;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.drc01c.TblUserDrc01cReasonDetails;


@Repository("TblUserDrc01cReasonDetailsRepo")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface TblUserDrc01cReasonDetailsRepo
		extends JpaRepository<TblUserDrc01cReasonDetails, Long>,
		JpaSpecificationExecutor<TblUserDrc01cReasonDetails> {
	


	public List<TblUserDrc01cReasonDetails> findByGstinInAndTaxPeriodAndIsActiveTrue(
			List<String> gstin, String taxPeriod);

	@Modifying
	@Query("UPDATE TblUserDrc01cReasonDetails g SET g.isActive = false,g.modifiedOn = CURRENT_TIMESTAMP,g.modifiedBy = :userName "
			+ "WHERE g.gstin IN (:gstins) and g.taxPeriod = :taxPeriod and g.isActive = true")
	public int inActivateRecords(@Param("gstins") List<String> gstins,
			@Param("taxPeriod") String taxPeriod,
			@Param("userName") String userName);
	
	public List<TblUserDrc01cReasonDetails> findByGstinAndTaxPeriodAndIsActiveTrue(
			String gstin, String taxPeriod);


	
}