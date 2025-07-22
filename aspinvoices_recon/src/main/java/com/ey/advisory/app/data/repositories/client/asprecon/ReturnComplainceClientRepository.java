/**
 * 
 */
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

import com.ey.advisory.app.data.entities.client.asprecon.ReturnComplianceClientEntity;

/**
 * @author Sujith.Nanga
 *
 */

@Repository("ReturnComplainceClientRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface ReturnComplainceClientRepository
		extends JpaRepository<ReturnComplianceClientEntity, Long>,
		JpaSpecificationExecutor<ReturnComplianceClientEntity> {

	@Query("select returnComplianceClientEntity from ReturnComplianceClientEntity "
			+ " returnComplianceClientEntity where returnComplianceClientEntity.requestId = :requestId")
	public List<ReturnComplianceClientEntity> getGstin(
			@Param("requestId") Long requestId);

	@Modifying
	@Query("UPDATE ReturnComplianceClientEntity SET reportStatus = :reportStatus,"
			+ " filePath = :filePath, updatedOn =:updatedOn"
			+ " where requestId  = :requestId and clientGstin =:clientGstin")
	void updateReportStatus(@Param("requestId") Long requestId,
			@Param("reportStatus") String reportStatus,
			@Param("filePath") String filePath,
			@Param("updatedOn") LocalDateTime updatedOn,
			@Param("clientGstin") String clientGstin);

	public List<ReturnComplianceClientEntity> findByRequestId(Long requestId);

}
