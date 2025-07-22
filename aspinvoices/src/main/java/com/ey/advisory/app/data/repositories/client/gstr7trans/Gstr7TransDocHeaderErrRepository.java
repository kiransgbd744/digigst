package com.ey.advisory.app.data.repositories.client.gstr7trans;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.gstr7trans.Gstr7TransDocErrHeaderEntity;

@Repository("Gstr7TransDocHeaderErrRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr7TransDocHeaderErrRepository
		extends JpaRepository<Gstr7TransDocErrHeaderEntity, Long> {

	@Modifying
	@Query("UPDATE Gstr7TransDocErrHeaderEntity doc SET doc.isDelete='true',"
			+ "doc.modifiedOn=:modifiedOn WHERE doc.id IN (:ids)")
	void updateDocDeletion(@Param("ids") List<Long> ids,
			@Param("modifiedOn") LocalDateTime updatedDate);
	
	@Modifying
	@Query("UPDATE Gstr7TransDocErrHeaderEntity doc SET doc.isDelete='true',"
			+ "doc.modifiedOn=:modifiedOn WHERE doc.docKey IN (:docKeys)")
	void updateDocDeletionByDocKeys(@Param("docKeys") List<String> docKeys,
			@Param("modifiedOn") LocalDateTime modifiedOn);
}