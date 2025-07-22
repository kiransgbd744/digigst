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

import com.ey.advisory.app.data.entities.client.IsdDistributionPsdEntity;

@Repository("IsdDistributionPsdRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface IsdDistributionPsdRepository
		extends JpaRepository<IsdDistributionPsdEntity, Long>,
		JpaSpecificationExecutor<IsdDistributionPsdEntity> {

	@Query("SELECT COUNT(doc) FROM IsdDistributionPsdEntity doc "
			+ "WHERE doc.fileId=:fileId AND "
			+ "doc.isError=true AND doc.isDelete = false")
	public Integer businessValidationCount(@Param("fileId") Long fileId);

	@Modifying
	@Query("Update IsdDistributionPsdEntity SET isDelete = true  WHERE "
			+ "distrbDocKey in (:docKeyList) AND isDelete = false")
	int updateIsDeleteFlag(@Param("docKeyList") List<String> docKeyList);

	public List<IsdDistributionPsdEntity> findByDistrbDocKeyAndActionTypeAndIsDeleteFalse(
			String distrbDocKey, String actionType);

}
