/**
 * 
 */
package com.ey.advisory.app.data.gstr1A.repositories.client;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Gstr1EinvSeriesCompEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AEinvSeriesCompEntity;

/**
 * @author Siva.Reddy
 *
 */
@Repository("Gstr1AEinvSeriesCompRepo")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1AEinvSeriesCompRepo
		extends CrudRepository<Gstr1AEinvSeriesCompEntity, Long> {

	Optional<Gstr1AEinvSeriesCompEntity> findByGstinAndReturnPeriodAndRequestStatusInAndIsActiveTrue(
			@Param("gstin") String gstin,
			@Param("returnPeriod") String returnPeriod,
			@Param("requestStatus") List<String> requestStatus);

	Optional<Gstr1AEinvSeriesCompEntity> findByGstinAndReturnPeriodAndIsActiveTrue(
			@Param("gstin") String gstin,
			@Param("returnPeriod") String returnPeriod);

	@Modifying
	@Query("UPDATE Gstr1AEinvSeriesCompEntity log SET log.requestStatus = :requestStatus,"
			+ "log.modifiedBy = 'SYSTEM',log.modifiedOn = CURRENT_TIMESTAMP,"
			+ " log.startTime = :startTime,log.endTime = :endTime "
			+ "WHERE  log.id = :id")
	void updateRequestStatus(@Param("id") Long id,
			@Param("requestStatus") String requestStatus,
			@Param("startTime") LocalDateTime startTime,
			@Param("endTime") LocalDateTime endTime);

	@Modifying
	@Query("UPDATE Gstr1AEinvSeriesCompEntity log SET log.isActive = false,"
			+ "log.modifiedBy = 'SYSTEM',log.modifiedOn = CURRENT_TIMESTAMP "
			+ "WHERE log.gstin = :gstin and log.returnPeriod = :returnPeriod")
	void updateRequestStatus(@Param("gstin") String gstin,
			@Param("returnPeriod") String returnPeriod);

}
