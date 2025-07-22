package com.ey.advisory.app.data.repositories.client.drc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.drc.TblDrcCommRequestDetails;


@Repository("Drc01RequestCommDetailsRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Drc01RequestCommDetailsRepository
		extends JpaRepository<TblDrcCommRequestDetails, Long>,
		JpaSpecificationExecutor<TblDrcCommRequestDetails> {
	
	List<TblDrcCommRequestDetails> findByRequestId(Long requestId);
	
	@Modifying
	@Query("UPDATE TblDrcCommRequestDetails SET reportStatus = :reportStatus, "
			+ " filePath = :filePath, updatedOn = :updatedOn "
			+ " where requestId = :requestId ")
	void updateStatus(@Param("requestId") Long requestId,
			@Param("reportStatus") String reportStatus,
			@Param("filePath") String filePath,
			@Param("updatedOn") LocalDateTime updatedOn);
	
	@Modifying
	@Query("UPDATE TblDrcCommRequestDetails SET reportStatus = :reportStatus, "
			+ " filePath = :filePath, updatedOn = :updatedOn, docId = :docId "
			+ " where requestId = :requestId ")
	void updateStatus(@Param("requestId") Long requestId,
			@Param("reportStatus") String reportStatus,
			@Param("filePath") String filePath,
			@Param("updatedOn") LocalDateTime updatedOn, @Param("docId") String docId);
	
	Optional<TblDrcCommRequestDetails> findByRequestIdAndIsDelete(Long requestId, Boolean isDelete);
	
	Optional<TblDrcCommRequestDetails> findByGstinAndTaxPeriodAndCommTypeAndEmailType(String gstin, String taxPeriod, String commType, String emailType);
	
	
	
	
}
