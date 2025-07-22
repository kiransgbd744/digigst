package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.services.gstr3b.itc.reclaim.Gstr3BGstinItcReclaimEntity;

/**
 * @author vishal.verma
 *
 */
@Repository("Gstr3BGstinItcReclaimRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr3BGstinItcReclaimRepository
		extends JpaRepository<Gstr3BGstinItcReclaimEntity, Long>,
		JpaSpecificationExecutor<Gstr3BGstinItcReclaimEntity> {

	Gstr3BGstinItcReclaimEntity findByGstinAndTaxPeriodAndIsActive(String gstin,
			String taxPeriod, boolean isActve);

	@Modifying
	@Query("Update Gstr3BGstinItcReclaimEntity SET isActive = false "
			+ "WHERE taxPeriod = :taxPeriod AND gstin =:gstin "
			+ " and isActive = true")
	void updateUserActiveFlag(@Param("taxPeriod") String taxPeriod,
			@Param("gstin") String gstin);

}
