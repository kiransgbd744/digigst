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

import com.ey.advisory.app.data.entities.client.asprecon.VendorFilingStatusEntity;

@Repository("VendorFilingStatusRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface VendorFilingStatusRepository
		extends JpaRepository<VendorFilingStatusEntity, Long>,
		JpaSpecificationExecutor<VendorFilingStatusEntity> {

	VendorFilingStatusEntity findByFinancialYearAndGstin(String financialYear,
			String gstin);

	@Query("Select doc.gstin from VendorFilingStatusEntity doc where doc.gstin IN (:gstins)"
			+ " and doc.financialYear =:financialYear")
	public List<String> findByFinancialYearAndGstinIn(
			@Param("gstins") List<String> gstins,
			@Param("financialYear") String financialYear);

	@Query("select doc from VendorFilingStatusEntity doc where modifiedOn >= :daysAgoDate"
			+ " and doc.gstin IN (:gstins) and doc.financialYear =:financialYear and doc.status = 'Failed'")
	List<VendorFilingStatusEntity> findAllFailedWithDateAfter(
			@Param("daysAgoDate") LocalDateTime daysAgoDate,
			@Param("gstins") List<String> gstins,
			@Param("financialYear") String financialYear);

	@Query("select doc from VendorFilingStatusEntity doc where modifiedOn <= :daysAgoDate"
			+ " and doc.gstin IN (:gstins) and doc.financialYear =:financialYear")
	List<VendorFilingStatusEntity> findAllNotInitiatedWithDateBefore(
			@Param("daysAgoDate") LocalDateTime daysAgoDate,
			@Param("gstins") List<String> gstins,
			@Param("financialYear") String financialYear);
	
	@Modifying
	@Query("UPDATE VendorFilingStatusEntity SET status =:status, modifiedOn =:modifiedOn "
			+ " WHERE gstin IN (:gstins) and financialYear =:financialYear")
	public void updateStatus(@Param("gstins") List<String> gstins,
			@Param("status") String status,
			@Param("modifiedOn") LocalDateTime modifiedOn,@Param("financialYear") String financialYear);

}
