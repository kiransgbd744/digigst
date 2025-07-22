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

import com.ey.advisory.app.data.entities.drc01c.TblDrc01cChallanDetails;



@Repository("TblDrc01cChallanDetailsRepo")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface TblDrc01cChallanDetailsRepo
		extends JpaRepository<TblDrc01cChallanDetails, Long>,
		JpaSpecificationExecutor<TblDrc01cChallanDetails> {

	public List<TblDrc01cChallanDetails> findByGstinInAndTaxPeriodAndIsActiveTrue(
			List<String> gstin, String taxPeriod);

	@Modifying
	@Query("UPDATE TblDrc01cChallanDetails g SET g.isActive = false,g.modifiedOn = CURRENT_TIMESTAMP,g.modifiedBy = :userName "
			+ "WHERE g.gstin = :gstin and g.taxPeriod = :taxPeriod and g.isActive = true")
	public int inActivateRecords(@Param("gstin") String gstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("userName") String userName);
	
	
	@Query("Select g.drcArnNo from TblDrc01cChallanDetails g WHERE "
			+ "g.refId =:refId  and g.isActive = true")
	public List<String> findByRefIdAndIsActiveTrue(@Param("refId") String refId);
			


	
}