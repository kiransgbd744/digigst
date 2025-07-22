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

import com.ey.advisory.app.data.entities.client.asprecon.ReturnComplianceRequestEntity;

/**
 * @author Laxmi.Salukuti
 *
 */

@Repository("ReturnComplianceRequestRepository")
@Transactional(propagation = Propagation.REQUIRED)

public interface ReturnComplianceRequestRepository
		extends JpaRepository<ReturnComplianceRequestEntity, Long>,
		JpaSpecificationExecutor<ReturnComplianceRequestEntity> {

	@Modifying
	@Query("UPDATE ReturnComplianceRequestEntity SET status = :status,"
			+ " filePath = :filePath, updatedOn =:updatedOn"
			+ " where id  = :id")
	void updateStatus(@Param("id") Long id, @Param("status") String status,
			@Param("filePath") String filePath,
			@Param("updatedOn") LocalDateTime updatedOn);

	public ReturnComplianceRequestEntity findByRequestId(
			@Param("requestId") Long requestId);

	public List<ReturnComplianceRequestEntity> findByCreatedBy(
			String createdBy);

	@Query("select returnComplianceRequestEntity.filePath "
			+ "from ReturnComplianceRequestEntity returnComplianceRequestEntity "
			+ "  where returnComplianceRequestEntity.requestId = :requestId")
	public String getFilePath(@Param("requestId") Long requestId);

}
