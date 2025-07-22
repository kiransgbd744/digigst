package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.gstr3b.Gstr3BGetLiabilityAutoCalcEntity;

/**
 * 
 * @author Rajesh N K
 *
 */

@Repository("Gstr3BGetLiabilityAutoCalcRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr3BGetLiabilityAutoCalcRepository
		extends JpaRepository<Gstr3BGetLiabilityAutoCalcEntity, Long>,
		JpaSpecificationExecutor<Gstr3BGetLiabilityAutoCalcEntity> {

	
	@Modifying
	@Query("Update Gstr3BGetLiabilityAutoCalcEntity SET isActive = false  WHERE "
			+ "taxPeriod = :taxPeriod AND gstin =:gstin AND isActive = true and "
			+ "sectionName Not IN ('5.1(a)', '5.1(b)')" )
		void softDeleteActiveRecords(@Param("taxPeriod") String taxPeriod, 
				@Param("gstin") String  gstin);
	
	
	@Query("from Gstr3BGetLiabilityAutoCalcEntity WHERE "
			+ "taxPeriod = :taxPeriod AND gstin =:gstin"
			+ "  AND sectionName IN (:sectionNameList) and isActive = true ")
	List<Gstr3BGetLiabilityAutoCalcEntity> findBySectionNameList(
			@Param("taxPeriod") String taxPeriod, @Param("gstin") String gstin,
			@Param("sectionNameList") List<String> sectionNameList);
	
	
	@Modifying
	@Query("Update Gstr3BGetLiabilityAutoCalcEntity SET isActive = false  WHERE "
			+ "taxPeriod = :taxPeriod AND gstin =:gstin AND isActive = true and "
			+ "sectionName IN ('5.1(a)', '5.1(b)')" )
		void softDeleteActiveInterest(@Param("taxPeriod") String taxPeriod, 
				@Param("gstin") String  gstin);
}
