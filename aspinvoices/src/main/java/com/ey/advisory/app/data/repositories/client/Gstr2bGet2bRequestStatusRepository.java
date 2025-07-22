package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.gstr2b.Gstr2bGet2bRequestStatusEntity;

/**
 * @author Hema G M
 *
 */

	@Repository("Gstr2bGet2bRequestStatusRepository")
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public interface Gstr2bGet2bRequestStatusRepository
			extends CrudRepository<Gstr2bGet2bRequestStatusEntity, Long>,
			JpaSpecificationExecutor<Gstr2bGet2bRequestStatusEntity> {
		
		
		@Modifying
		@Query("UPDATE Gstr2bGet2bRequestStatusEntity b SET b.status = :status,"
				+ "  b.completedOn = :completedOn, b.filePath =:filePath  "
				+ " WHERE b.reqId = :reqId ")
		void updateStatus(@Param("status") String status,
				@Param("completedOn") LocalDateTime completedOn,
				@Param("filePath") String filePath,
				@Param("reqId") Long reqId);

		@Modifying
		@Query("UPDATE Gstr2bGet2bRequestStatusEntity b SET b.status = :status,"
				+ "  b.completedOn = :completedOn, b.filePath =:filePath,  "
				+ " b.docId =:docId WHERE b.reqId = :reqId ")
		void updateStatusAndDcoId(@Param("status") String status,
				@Param("completedOn") LocalDateTime completedOn,
				@Param("filePath") String filePath,
				@Param("docId") String docId,
				@Param("reqId") Long reqId);
		
	@Query("from Gstr2bGet2bRequestStatusEntity where createdBy =:createdBy")
	public List<Gstr2bGet2bRequestStatusEntity> findByCreatedBy(
			@Param("createdBy") String createdBy);
	
	@Query("from Gstr2bGet2bRequestStatusEntity where filePath =:filePath")
	public Gstr2bGet2bRequestStatusEntity findByFilePath(
			@Param("filePath") String filePath);
	
	@Query("from Gstr2bGet2bRequestStatusEntity where reqId =:reqId")
	public Gstr2bGet2bRequestStatusEntity findByReqId(
			@Param("reqId") Long reqId);

}
