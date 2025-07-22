package com.ey.advisory.app.data.repositories.client.drc01c;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.drc01c.TblDrc01cGetRetCompListDetails;


@Repository("TblDrc01cGetRetCompListDetailsRepo")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface TblDrc01cGetRetCompListDetailsRepo
		extends JpaRepository<TblDrc01cGetRetCompListDetails, Long>,
		JpaSpecificationExecutor<TblDrc01cGetRetCompListDetails> {


	@Modifying
	@Query("UPDATE TblDrc01cGetRetCompListDetails g SET g.isActive = false,g.modifiedOn = CURRENT_TIMESTAMP,g.modifiedBy = :userName "
			+ "WHERE g.gstin IN (:gstins) and g.taxPeriod = :taxPeriod and g.isActive = true")
	public int inActivateRecords(@Param("gstins") List<String> gstins,
			@Param("taxPeriod") String taxPeriod,
			@Param("userName") String userName);

	public Optional<TblDrc01cGetRetCompListDetails> findByGstinAndTaxPeriodAndIsActiveTrue(
			String gstin, String taxPeriod);
	
	public List<TblDrc01cGetRetCompListDetails> findByGstinInAndTaxPeriodAndIsActiveTrue(
			List<String> gstin, String taxPeriod);



	
}