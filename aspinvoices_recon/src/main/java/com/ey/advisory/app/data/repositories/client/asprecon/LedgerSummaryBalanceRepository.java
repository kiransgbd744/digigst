package com.ey.advisory.app.data.repositories.client.asprecon;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.GetSummarizedLedgerBalanceEntity;

/**
 * @author Arun K.A
 *
 */
@Repository("LedgerSummaryBalanceRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface LedgerSummaryBalanceRepository
		extends JpaRepository<GetSummarizedLedgerBalanceEntity, Long>,
		JpaSpecificationExecutor<GetSummarizedLedgerBalanceEntity> {

	@Modifying
	@Query("UPDATE GetSummarizedLedgerBalanceEntity doc SET doc.refreshStatus= :status,"
			+ "doc.lastUpdatedDate=:updatedDate WHERE "
			+ "doc.supplierGstin IN (:gstins) AND doc.taxPeriod =:taxPeriod And doc.refreshStatus='Active'")
	int updateRefreshStatus(@Param("gstins") List<String> gstins,
			@Param("taxPeriod") String taxPeriod,
			@Param("status") String status,
			@Param("updatedDate") LocalDateTime updatedDate);

	List<GetSummarizedLedgerBalanceEntity> findBySupplierGstinInAndTaxPeriodAndRefreshStatus(
			List<String> supplierGstin, String taxPeriod, String refreshStatus);
	
	List<GetSummarizedLedgerBalanceEntity> findBySupplierGstinInAndRefreshStatus(
			List<String> supplierGstin, String refreshStatus);
	
	@Modifying
	@Query("UPDATE GetSummarizedLedgerBalanceEntity doc SET doc.refreshStatus= :status, "
			+ "doc.lastUpdatedDate=:updatedDate WHERE "
			+ "doc.supplierGstin IN (:gstins) And doc.refreshStatus='Active'")
	int updateRefreshStatusWithOutTaxPeriod(@Param("gstins") List<String> gstins,
			@Param("status") String status,
			@Param("updatedDate") LocalDateTime updatedDate);

	
	@Query("SELECT g.supplierGstin FROM GetSummarizedLedgerBalanceEntity g " +
		       "WHERE g.refreshStatus = 'Active' " +
		       "AND (g.status IS NULL OR g.status NOT IN ('Initiated', 'InProgress', '')) " +
		       "AND g.supplierGstin IN :activeGstnList")
		List<String> findActiveSupplierGstin(@Param("activeGstnList") List<String> activeGstnList);

	@Modifying
	@Query("UPDATE GetSummarizedLedgerBalanceEntity g " +
	       "SET g.status = :status, " +
	       "g.getCallStatusTimeStamp = :lastUpdatedDate " +
	       "WHERE g.refreshStatus = 'Active' " +
	       "AND g.supplierGstin IN :gstins")
	int updateStatusAndLastUpdatedDate(@Param("status") String status, 
	                                   @Param("lastUpdatedDate") LocalDateTime lastUpdatedDate, 
	                                   @Param("gstins") List<String> gstins);

}
