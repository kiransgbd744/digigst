package com.ey.advisory.app.data.repositories.client;

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

import com.ey.advisory.app.data.entities.client.Gstr3bGenerateStatusEntity;

@Repository("Gstr3bDigiStatusRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr3bDigiStatusRepository
		extends JpaRepository<Gstr3bGenerateStatusEntity, Long>,
		JpaSpecificationExecutor<Gstr3bGenerateStatusEntity> {

	Gstr3bGenerateStatusEntity findByGstinAndTaxPeriodAndIsActive(String gstin,
			String taxPeriod, Boolean isActive);

	Gstr3bGenerateStatusEntity findByGstinAndTaxPeriodAndIsActiveAndStatusIn(String gstin,
			String taxPeriod, Boolean isActive,List<String> statusList);
	
	@Modifying
	@Query("UPDATE Gstr3bGenerateStatusEntity doc SET doc.status=:status, doc.modifiedOn= :now WHERE doc.id=:id ")
	void updateRecordById(@Param("id") Long id, @Param("status") String status,
			@Param("now") LocalDateTime now);

	@Modifying
	@Query("UPDATE Gstr3bGenerateStatusEntity SET isActive = false WHERE gstin= :gstin "
			+ "AND taxPeriod= :taxPeriod AND isActive = true")
	void softDeleteRecord(@Param("gstin") String gstin,
			@Param("taxPeriod") String taxPeriod); 
	
	@Modifying
	@Query("UPDATE Gstr3bGenerateStatusEntity SET isActive = false WHERE gstin= :gstin "
			+ "AND taxPeriod= :taxPeriod AND isActive = true AND status Not IN ('InProgress', 'Initiated')")
	void softDeleteActiveRecord(@Param("gstin") String gstin,
			@Param("taxPeriod") String taxPeriod); 
}
