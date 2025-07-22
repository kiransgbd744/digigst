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

import com.ey.advisory.app.data.entities.client.AutoRecon2AERPPushReportEntity;

/**
 * @author Jithendra.B
 *
 */
@Repository("AutoRecon2AERPPushReportRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface AutoRecon2AERPPushReportRepository
		extends JpaRepository<AutoRecon2AERPPushReportEntity, Long>,
		JpaSpecificationExecutor<AutoRecon2AERPPushReportEntity> {

	@Modifying
	@Query("UPDATE AutoRecon2AERPPushReportEntity e SET e.isERPPush =:isERPPush,"
			+ " e.erpPushDate = CURRENT_TIMESTAMP "
			+ " WHERE e.reconLinkID IN (:reconLinkID)")
	public int updateErpPushStatusAndTime(
			@Param("reconLinkID") List<Long> reconLinkIdList,
			@Param("isERPPush") boolean isErpPush);

}