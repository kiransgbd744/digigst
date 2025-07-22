/**
 * 
 */
package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.RequestForApprovalEntity;

/**
 * @author Balakrishna.S
 *
 */
@Repository("RequestForApprovalRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface RequestForApprovalRepository
		extends CrudRepository<RequestForApprovalEntity, Long> {

	@Query("SELECT req.id,req.requestTime,req.checkerId,req.approvalStatus, "
			+ "req.checkerComments,req.responseTime FROM RequestForApprovalEntity "
			+ "req WHERE req.isDelete =FALSE AND req.entityId =:entityId "
			+ "AND req.gstin =:gstin AND req.returnPeriod =:returnPeriod "
			+ "AND req.workFlowType =:workFlowType ORDER BY req.id DESC")
	public List<Object[]> findRequestDetails(@Param("entityId") Long entityId,
			@Param("gstin") String gstin,
			@Param("returnPeriod") String returnPeriod,
			@Param("workFlowType") String workFlowType);

}
