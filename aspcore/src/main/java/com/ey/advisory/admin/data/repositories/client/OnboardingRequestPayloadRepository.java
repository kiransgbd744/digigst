package com.ey.advisory.admin.data.repositories.client;

import java.sql.Clob;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.OnboardingRequestPayloadEntity;

/**
 * @author vishal.verma
 *
 */
@Repository("OnboardingRequestPayloadRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface OnboardingRequestPayloadRepository
		extends CrudRepository<OnboardingRequestPayloadEntity, Long> {
	
	
	@Modifying
	@Query("Update OnboardingRequestPayloadEntity SET errorMsg =:errorMsg "
			+ " WHERE id =:id" )
		void updateErrorLog(@Param("errorMsg") Clob errorMsg, 
				@Param("id") Long  id);
	
	@Modifying
	@Query("Update OnboardingRequestPayloadEntity SET respPaylaod =:respPaylaod "
			+ " WHERE id =:id" )
		void updateRespMsg(@Param("respPaylaod") Clob respPaylaod, 
				@Param("id") Long  id);
}
