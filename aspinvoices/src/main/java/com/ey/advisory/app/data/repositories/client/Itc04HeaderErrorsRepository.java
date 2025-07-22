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

import com.ey.advisory.app.data.entities.client.Itc04HeaderErrorsEntity;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Repository("Itc04HeaderErrorsRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Itc04HeaderErrorsRepository
		extends JpaRepository<Itc04HeaderErrorsEntity, Long>,
		JpaSpecificationExecutor<Itc04HeaderErrorsEntity> {

		@Modifying
		@Query("UPDATE Itc04HeaderErrorsEntity doc SET doc.isDelete='true',"
				+ "doc.modifiedOn=:updatedDate WHERE doc.id IN (:ids)")
		void updateDocDeletion(@Param("ids") List<Long> ids,
				@Param("updatedDate") LocalDateTime updatedDate);
	
}
