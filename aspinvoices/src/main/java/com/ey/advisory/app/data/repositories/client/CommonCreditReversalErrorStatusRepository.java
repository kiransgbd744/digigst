package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.CommonCreditReversalErr;

@Repository("CommonCreditReversalErrorStatusRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface CommonCreditReversalErrorStatusRepository
		extends JpaRepository<CommonCreditReversalErr, Long>,
		JpaSpecificationExecutor<CommonCreditReversalErr> {
	
	@Query("SELECT COUNT(DISTINCT doc.commonCreditDocKey) FROM CommonCreditReversalErr doc "
			+ "WHERE doc.fileId=:fileId AND "
			+ "doc.isDelete = false")
	public Integer errorCount(@Param("fileId") String fileId);

}
