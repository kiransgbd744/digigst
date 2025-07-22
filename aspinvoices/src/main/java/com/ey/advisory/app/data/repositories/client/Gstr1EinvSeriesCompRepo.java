/**
 * 
 */
package com.ey.advisory.app.data.repositories.client;

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

/**
 * @author Siva.Reddy
 *
 */
@Repository("Gstr1EinvSeriesCompRepo")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1EinvSeriesCompRepo
		extends CrudRepository<Gstr1EinvSeriesCompEntity, Long> {

	Optional<Gstr1EinvSeriesCompEntity> findByGstinAndReturnPeriodAndRequestStatusInAndIsActiveTrue(
			@Param("gstin") String gstin,
			@Param("returnPeriod") String returnPeriod,
			@Param("requestStatus") List<String> requestStatus);

	Optional<Gstr1EinvSeriesCompEntity> findByGstinAndReturnPeriodAndIsActiveTrue(
			@Param("gstin") String gstin,
			@Param("returnPeriod") String returnPeriod);

	@Modifying
	@Query("UPDATE Gstr1EinvSeriesCompEntity log SET log.requestStatus = :requestStatus,"
			+ "log.modifiedBy = 'SYSTEM',log.modifiedOn = CURRENT_TIMESTAMP,"
			+ " log.startTime = :startTime,log.endTime = :endTime "
			+ "WHERE  log.id = :id")
	void updateRequestStatus(@Param("id") Long id,
			@Param("requestStatus") String requestStatus,
			@Param("startTime") LocalDateTime startTime,
			@Param("endTime") LocalDateTime endTime);

	@Modifying
	@Query("UPDATE Gstr1EinvSeriesCompEntity log SET log.isActive = false,"
			+ "log.modifiedBy = 'SYSTEM',log.modifiedOn = CURRENT_TIMESTAMP "
			+ "WHERE log.gstin = :gstin and log.returnPeriod = :returnPeriod")
	void updateRequestStatus(@Param("gstin") String gstin,
			@Param("returnPeriod") String returnPeriod);

	public List<Gstr1EinvSeriesCompEntity> findByRequestStatusInAndIsActiveTrue(
			List<String> status);
	
	public Gstr1EinvSeriesCompEntity findTop1ByRequestStatusInAndIsActiveTrueOrderByIdAsc(
			List<String> status);
}
