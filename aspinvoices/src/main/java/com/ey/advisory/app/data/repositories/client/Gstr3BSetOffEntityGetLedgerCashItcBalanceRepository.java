package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.services.gstr3b.entity.setoff.Gstr3BSetOffEntityGetLedgerCashItcBalanceEntity;

/**
 * @author Hema G M
 *
 */

@Repository("Gstr3BSetOffEntityGetLedgerCashItcBalanceRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)

public interface Gstr3BSetOffEntityGetLedgerCashItcBalanceRepository extends
		CrudRepository<Gstr3BSetOffEntityGetLedgerCashItcBalanceEntity, Long>,
		JpaSpecificationExecutor<Gstr3BSetOffEntityGetLedgerCashItcBalanceEntity> {

	List<Gstr3BSetOffEntityGetLedgerCashItcBalanceEntity> findByGstinInAndTaxPeriodAndIsActive(
			List<String> gstinsList, String taxPeriod, Boolean active);

	@Query("from Gstr3BSetOffEntityGetLedgerCashItcBalanceEntity where "
			+ "taxPeriod =:taxPeriod AND gstin =:gstin AND isActive =true")
	public Gstr3BSetOffEntityGetLedgerCashItcBalanceEntity findByGstinAndTaxPeriod(
			@Param("taxPeriod") String taxPeriod, @Param("gstin") String gstin);
	
	@Modifying
	@Query("Update Gstr3BSetOffEntityGetLedgerCashItcBalanceEntity SET "
			+ "isActive = false  WHERE "
			+ "taxPeriod = :taxPeriod AND gstin =:gstin and isActive = true")
	public void updateIsActive(@Param("taxPeriod") String taxPeriod,
			@Param("gstin") String gstin);

}