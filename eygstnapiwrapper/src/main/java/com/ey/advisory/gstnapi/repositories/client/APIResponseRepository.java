/**
 * 
 */
package com.ey.advisory.gstnapi.repositories.client;

import java.sql.Clob;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.gstnapi.domain.client.APIResponseEntity;

/**
 * @author Khalid1.Khan
 *
 */
@Repository("APIResponseRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface APIResponseRepository
		extends JpaRepository<APIResponseEntity, Long>,
		JpaSpecificationExecutor<APIResponseEntity> {
	
	@Query("Select A from APIResponseEntity A where"
			+ " A.status = 'SUCCESS' AND A.invocationId = :invocationId")
	List<APIResponseEntity> findByInvocationId(@Param("invocationId")Long invocationId);
	
	
	@Query("Select A from APIResponseEntity A where"
			+ " A.status = 'SUCCESS' AND A.id = :id")
	APIResponseEntity getResponseById(@Param("id")Long id);
	
	
	/*@Modifying
	@Query("Select A from APIResponseEntity A where"
			+ " A.status = 'SUCCESS' AND A.id = :id")
	APIResponseEntity getResponseById(@Param("id") Long id);*/
	
	@Modifying
	@Query("Select A from APIResponseEntity A where A.status = 'Success'"
			+ " AND A.id in (:ids)")
	List<Clob> getResponseListByIds(@Param("id") List<Long> ids);

	@Query("Select A.id from APIResponseEntity A where"
			+ " A.status = 'SUCCESS' AND A.invocationId = :invocationId")
	List<Long> getResultIdByInvocationId(@Param("invocationId") Long invocationId);
	
	@Modifying
	@Query("update APIResponseEntity A set "
			+ " A.status = :status where A.invocationId = :invocationId")
	void updateRequestStatus(@Param("invocationId") Long invocationId,
			@Param("status") String status);

}
