/**
 * 
 */
package com.ey.advisory.app.data.repositories.client;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.gstr3b.Gstr3bMonthlyTrendTaxAmountsEntity;

/**
 * @author Sakshi.jain
 *
 */
@Repository("Gstr3BMonthlyTrendTaxAmountRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr3BMonthlyTrendTaxAmountRepository
		extends CrudRepository<Gstr3bMonthlyTrendTaxAmountsEntity, Long> {

	public Optional<Gstr3bMonthlyTrendTaxAmountsEntity> findBySuppGstinAndTaxPeriodAndIsActiveTrue(
			String gstin, String taxPeriod);
	@Modifying
	@Query(" update Gstr3bMonthlyTrendTaxAmountsEntity set isActive = false "
			+ " where taxPeriod =:taxPeriod and suppGstin =:suppGstin ")
	public void softDeleteByGstinandTaxPeriod(
			@Param("suppGstin") String suppGstin,
			@Param("taxPeriod") String taxPeriod);

	@Query(" select e from  Gstr3bMonthlyTrendTaxAmountsEntity e"
			+ " where e.suppGstin in (:suppGstin) and e.taxPeriod in (:taxPeriod) and isActive = true ")
	public List<Gstr3bMonthlyTrendTaxAmountsEntity> getGstinAndTaxPeriod(@Param("suppGstin") List<String> suppGstin,
			@Param("taxPeriod") List<String> taxPeriod);
}
