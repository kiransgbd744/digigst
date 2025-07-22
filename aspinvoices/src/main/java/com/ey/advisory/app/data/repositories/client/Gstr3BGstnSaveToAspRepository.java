package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.gstr3b.Gstr3bGstnSaveToAspEntity;

/**
 * 
 * @author vishal.verma
 *
 */

@Repository("Gstr3BGstnSaveToAspRepository")
@Transactional(propagation = Propagation.REQUIRED,readOnly = false)
public interface Gstr3BGstnSaveToAspRepository
		extends JpaRepository<Gstr3bGstnSaveToAspEntity, Long>,
		JpaSpecificationExecutor<Gstr3bGstnSaveToAspEntity> {
	

	@Modifying
	@Query("Update Gstr3bGstnSaveToAspEntity SET isActive = false  WHERE "
			+ "taxPeriod = :taxPeriod AND gstin =:gstin  AND sectionName "
			+ "in (:sectionNameList)")
		void updateActiveFlag(@Param("taxPeriod") String taxPeriod, 
				@Param("gstin") String  gstin,
				@Param("sectionNameList")List<String> sectionNameList);
	
	@Modifying
	@Query("Update Gstr3bGstnSaveToAspEntity SET isActive = false  WHERE "
			+ "taxPeriod = :taxPeriod AND gstin =:gstin AND isActive = true" )
		void updateAllActiveFlag(@Param("taxPeriod") String taxPeriod, 
				@Param("gstin") String  gstin);
	
	@Modifying
	@Query("Update Gstr3bGstnSaveToAspEntity SET isActive = false  WHERE "
			+ "taxPeriod = :taxPeriod AND gstin =:gstin"
			+ "  AND sectionName =:sectionName")
		void updatePerActiveFlag(@Param("taxPeriod") String taxPeriod, 
				@Param("gstin") String  gstin,
				@Param("sectionName") String sectionName);
	
	@Query("select e from Gstr3bGstnSaveToAspEntity e where "
			+ "taxPeriod = :taxPeriod AND gstin =:gstin"
			+ "  AND isActive = true ")
	public List<Gstr3bGstnSaveToAspEntity> findByGstinAndTaxPeriod(@Param("gstin") String gstin,
			@Param("taxPeriod") String taxPeriod, Pageable pageable);
	
	@Query("from Gstr3bGstnSaveToAspEntity WHERE taxPeriod=:taxPeriod AND gstin=:gstin "
			+ " AND sectionName IN('5.1(a)','5.1(b)') AND isActive=true")
	public List<Gstr3bGstnSaveToAspEntity> findInterestAndLateFee(
			@Param("taxPeriod") String taxPeriod, @Param("gstin") String gstin);
	
	@Query("from Gstr3bGstnSaveToAspEntity WHERE taxPeriod=:taxPeriod AND gstin=:gstin AND "
			+ "sectionName IN('3.1(a)','3.1(b)','3.1(d)','4(c)') AND isActive=true ")
	public List<Gstr3bGstnSaveToAspEntity> findOtherLiabilityAndReverseChargeTax(
			@Param("taxPeriod") String taxPeriod, @Param("gstin") String gstin);
	
	@Query("from Gstr3bGstnSaveToAspEntity WHERE taxPeriod=:taxPeriod AND gstin IN (:gstins) AND "
			+ "sectionName IN('3.1(a)','3.1(b)','3.1(d)','4(c)','3.1.1(a)') AND isActive=true ")
	public List<Gstr3bGstnSaveToAspEntity> findByGstinInAndTaxPeriodAndIsActiveAndSectionName(
			@Param("gstins") List<String> gstins, @Param("taxPeriod") String taxPeriod);
	
	@Query("from Gstr3bGstnSaveToAspEntity WHERE taxPeriod=:taxPeriod "
			+ " AND gstin =:gstin AND "
			+ " sectionName ='4(c)' AND isActive=true ")
	public Gstr3bGstnSaveToAspEntity findByGstinAndTaxPeriodAndSectionName(
			@Param("gstin") String gstin, @Param("taxPeriod") String taxPeriod);
	
	@Query("from Gstr3bGstnSaveToAspEntity WHERE taxPeriod=:taxPeriod AND gstin=:gstin AND "
			+ "sectionName IN('3.1(a)','3.1(b)','3.1(d)','4(c)','5.1(a)','5.1(b)','3.1.1(a)') AND isActive=true ")
	public List<Gstr3bGstnSaveToAspEntity> findOtherLiabilityAndReverseChargeTaxAndInterestAndLateFee(
			@Param("taxPeriod") String taxPeriod, @Param("gstin") String gstin);
	
}
