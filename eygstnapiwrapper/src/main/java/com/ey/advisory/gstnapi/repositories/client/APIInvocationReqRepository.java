/**
 * 
 */
package com.ey.advisory.gstnapi.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.gstnapi.domain.client.APIInvocationReqEntity;

/**
 * @author Khalid1.Khan
 *
 */
@Repository("APIInvocationReqRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface APIInvocationReqRepository
		extends JpaRepository<APIInvocationReqEntity, Long>,
		JpaSpecificationExecutor<APIInvocationReqEntity> {

	@Modifying
	@Query("UPDATE APIInvocationReqEntity A set A.refReqId = :refReqId"
			+ " WHERE A.id = :id")
	boolean updateRefReqId(@Param("refReqId") Long invocationId,
			@Param("id") Long id) ;
	
	@Query("SELECT id from APIInvocationReqEntity  where "
			+ "apiParamsHashValue = :apiParamsHashValue and status"
			+ " = 'SUCCESS'")
	List<Long> apiParamExistCheck(
			@Param("apiParamsHashValue") String apiParamsHashValue);
	
	@Modifying
	@Query("UPDATE APIInvocationReqEntity A set A.status = :status"
			+ " WHERE A.id = :id")
	void updateStatus(@Param("status") String status,
			@Param("id") Long id);

}
