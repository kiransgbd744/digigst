package com.ey.advisory.app.data.repositories.client.asprecon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.GetIrnDetailPayloadEntity;

/**
 * @author Sakshi.jain
 *
 */
@Repository("GetIrnDtlPayloadRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface GetIrnDtlPayloadRepository
		extends JpaRepository<GetIrnDetailPayloadEntity, Long>,
		JpaSpecificationExecutor<GetIrnDetailPayloadEntity> {

	 @Query("SELECT p FROM GetIrnDetailPayloadEntity p WHERE p.irn = :irn AND p.irnSts = :irnStatus")
	 GetIrnDetailPayloadEntity findByIrnAndIrnStatus(
	            @Param("irn") String irn,
	            @Param("irnStatus") String irnStatus
	    );
}