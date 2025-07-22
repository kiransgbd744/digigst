package com.ey.advisory.admin.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.client.Gstr3bRatioUserInputEntity;

import jakarta.transaction.Transactional;

/**
 * @author ashutosh.kar
 *
 */
@Repository("Gstr3bRatioUserInputRepository")
public interface Gstr3bRatioUserInputRepository
		extends JpaRepository<Gstr3bRatioUserInputEntity, Long>, JpaSpecificationExecutor<Gstr3bRatioUserInputEntity> {

	@Modifying
	@Transactional
	@Query("UPDATE Gstr3bRatioUserInputEntity SET isDeleted = true "
			+ "WHERE taxPeriod = :taxPeriod AND gstin = :gstin AND isDeleted = false")
	void updateActiveFlag(@Param("taxPeriod") String taxPeriod, @Param("gstin") String gstin);

	@Query("SELECT r.userInputRatio1, r.userInputRatio2 FROM Gstr3bRatioUserInputEntity r WHERE r.gstin = :gstin "
			+ "AND r.taxPeriod = :taxPeriod AND r.isDeleted = false")
	Object[] findRatio1AndRatio2ByGstinAndTaxPeriod(@Param("gstin") String gstin, @Param("taxPeriod") String taxPeriod);

}
