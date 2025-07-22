package com.ey.advisory.admin.data.repositories.client;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.GetAutoJobDtlsEntity;

/**
 * @author Siva.Reddy
 *
 */
@Repository("GetAutoJobDtlsRepo")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GetAutoJobDtlsRepo
		extends CrudRepository<GetAutoJobDtlsEntity, Long> {

	Optional<GetAutoJobDtlsEntity> findByEntityIdAndPostedDateAndReturnType(
			Long entityId, LocalDate postedDate, String returnType);

	@Query("SELECT MAX(createdOn) FROM GetAutoJobDtlsEntity WHERE  "
			+ "returnType =:returnType")
	public LocalDateTime findMaxGetCall(@Param("returnType") String returnType);

}
