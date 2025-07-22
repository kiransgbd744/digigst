package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.CommonCreditReversalPsdEntity;

@Repository("CommonCreditReversalPsdRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface CommonCreditReversalPsdRepository
		extends JpaRepository<CommonCreditReversalPsdEntity, Long>,
		JpaSpecificationExecutor<CommonCreditReversalPsdEntity> {

	@Query("SELECT COUNT(DISTINCT doc.commonCreditDocKey) FROM CommonCreditReversalPsdEntity doc "
			+ "WHERE doc.fileId=:fileId AND "
			+ "doc.isDelete = false")
	public Integer businessValidationCount(@Param("fileId") Long fileId);

	@Modifying
	@Query("Update CommonCreditReversalPsdEntity SET isDelete = true  WHERE "
			+ "commonCreditDocKey in (:docKeyList) AND isDelete = false")
	int updateIsDeleteFlag(@Param("docKeyList") List<String> docKeyList);

}
