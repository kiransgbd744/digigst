package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.gstr3b.Gstr3BGstinAspUserInputEntity;

import jakarta.persistence.QueryHint;

/**
 * @author vishal.verma
 *
 */
@Repository("Gstr3BGstinAspUserInputRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr3BGstinAspUserInputRepository
		extends JpaRepository<Gstr3BGstinAspUserInputEntity, Long>,
		JpaSpecificationExecutor<Gstr3BGstinAspUserInputEntity> {

	@Modifying
	@Query("Update Gstr3BGstinAspUserInputEntity SET isActive = false, "
			+ " isITCActive = false WHERE "
			+ "taxPeriod = :taxPeriod AND gstin =:gstin  AND sectionName "
			+ "in (:sectionNameList) and isActive = true and isITCActive = true")
	void updateActiveFlag(@Param("taxPeriod") String taxPeriod,
			@Param("gstin") String gstin,
			@Param("sectionNameList") List<String> sectionNameList);

	@Modifying
	@Query("Update Gstr3BGstinAspUserInputEntity SET isActive = false, "
			+ " isITCActive = false  WHERE "
			+ "taxPeriod = :taxPeriod AND gstin =:gstin"
			+ "  AND sectionName =:sectionName and isActive = true and isITCActive = true")
	void updatePerActiveFlag(@Param("taxPeriod") String taxPeriod,
			@Param("gstin") String gstin,
			@Param("sectionName") String sectionName);

	/**
	 * @author Balakrishna.S
	 * @param taxPeriod
	 * @param gstin
	 * @param sectionName
	 */

	@Modifying
	@Query("Update Gstr3BGstinAspUserInputEntity SET isActive = false, "
			+ " isITCActive = false WHERE "
			+ "taxPeriod = :taxPeriod AND gstin =:gstin  AND sectionName "
			+ "= :sectionName and isActive = true and isITCActive = true")
	void updateActiveITCFlag(@Param("taxPeriod") String taxPeriod,
			@Param("gstin") String gstin,
			@Param("sectionName") String sectionName);
    
	@QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "false"))
	@Query("FROM Gstr3BGstinAspUserInputEntity WHERE "
	        + "taxPeriod = :taxPeriod AND gstin = :gstin "
	        + "AND sectionName IN :sectionNameList AND isActive = true")
	List<Gstr3BGstinAspUserInputEntity> getITC10PercSectionData(
			@Param("taxPeriod") String taxPeriod, @Param("gstin") String gstin,
			@Param("sectionNameList") List<String> sectionNameList);

	@Modifying
	@Query("Update Gstr3BGstinAspUserInputEntity SET isActive = false, "
			+ " isITCActive = false WHERE "
			+ "taxPeriod = :taxPeriod AND gstin =:gstin"
			+ " and isActive = true and isITCActive = true")
	void updateActiveFlagforAllSections(@Param("taxPeriod") String taxPeriod,
			@Param("gstin") String gstin);

	List<Gstr3BGstinAspUserInputEntity> findByGstinAndTaxPeriodAndIsActive(
			String gstin, String taxPeriod, boolean isActve);

	@Modifying
	@Query("Update Gstr3BGstinAspUserInputEntity SET isActive = false  WHERE "
			+ "docKey in (:docKeyList) AND isActive = true")
	int updateIsDeleteFlag(@Param("docKeyList") List<String> docKeyList);

	@Modifying
	@Query("Update Gstr3BGstinAspUserInputEntity SET isActive = false "
			+ "WHERE taxPeriod = :taxPeriod AND gstin =:gstin AND "
			+ "sectionName = :sectionName AND pos =:pos"
			+ " and isActive = true")
	void updateUserActiveFlagforAllSections(
			@Param("taxPeriod") String taxPeriod, @Param("gstin") String gstin,
			@Param("sectionName") String sectionName, @Param("pos") String pos);
	
	@Modifying
	@Query("Update Gstr3BGstinAspUserInputEntity SET isActive = false "
			+ "WHERE taxPeriod = :taxPeriod AND gstin =:gstin AND "
			+ "sectionName = :sectionName"
			+ " and isActive = true")
	void updateUserActiveFlagforAllSectionsWithoutPos(
			@Param("taxPeriod") String taxPeriod, @Param("gstin") String gstin,
			@Param("sectionName") String sectionName);

	public List<Gstr3BGstinAspUserInputEntity> findByFileId(Long fileId);
	
	List<Gstr3BGstinAspUserInputEntity> findByGstinAndTaxPeriodAndSectionNameInAndIsActiveTrue(
			String gstin, String taxPeriod, List<String> sectionName);

	
	List<Gstr3BGstinAspUserInputEntity> findByGstinAndTaxPeriodAndSectionNameAndIsActiveTrue(
			String gstin, String taxPeriod, String sectionName);

}
