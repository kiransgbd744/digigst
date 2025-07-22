package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetIrnSearchDetailsPayloadEntity;


@Repository("GetIrnSearchDtlPayloadRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface GetIrnSearchDtlPayloadRepository
		extends JpaRepository<GetIrnSearchDetailsPayloadEntity, Long>,
		JpaSpecificationExecutor<GetIrnSearchDetailsPayloadEntity> {

	 @Query("SELECT p FROM GetIrnSearchDetailsPayloadEntity p WHERE p.irn = :irn")
	 GetIrnSearchDetailsPayloadEntity findByIrnAndIrnStatus(
	            @Param("irn") String irn
	    );
}