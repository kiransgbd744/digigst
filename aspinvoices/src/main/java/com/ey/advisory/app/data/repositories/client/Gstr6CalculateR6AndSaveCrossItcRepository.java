/**
 * 
 *//*
package com.ey.advisory.app.data.repositories.client;

import java.sql.Clob;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Gstr6CalculateR6AndSaveCrossItcEntity;

*//**
 * @author Hemasundar.J
 *
 *//*
@Repository("Gstr6CalculateR6AndSaveCrossItcRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr6CalculateR6AndSaveCrossItcRepository
		extends JpaRepository<Gstr6CalculateR6AndSaveCrossItcEntity, Long>{
	
	@Modifying
	@Query("UPDATE Gstr6CalculateR6AndSaveCrossItcEntity b SET "
			+ "b.crossStatusCode = :crossStatusCode,b.crossJobTime = "
			+ ":crossJobTime, b.payload = :payload WHERE b.id = :id")
	void updateSuccess(@Param("crossStatusCode") String crossStatusCode,
			@Param("crossJobTime") LocalDateTime crossJobTime,
			@Param("payload") Clob payload,
			@Param("id") Long id);
	
	@Modifying
	@Query("UPDATE Gstr6CalculateR6AndSaveCrossItcEntity b SET "
			+ "b.crossErrorCode = :crossErrorCode,b.crossErrorDesc = "
			+ ":crossErrorDesc,b.crossJobTime = :crossJobTime, b.payload = "
			+ ":payload WHERE b.id = :id")
	void updateFailure(@Param("crossErrorCode") String crossErrorCode,
			@Param("crossErrorDesc") String crossErrorDesc,
			@Param("crossJobTime") LocalDateTime crossJobTime,
			@Param("payload") Clob payload,
			@Param("id") Long id);

}
*/