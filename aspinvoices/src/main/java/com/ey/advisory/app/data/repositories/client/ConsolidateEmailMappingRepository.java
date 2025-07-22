package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.ConsolidateEmailMappingEntity;

@Repository("ConsolidateEmailMappingRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface ConsolidateEmailMappingRepository
		extends JpaRepository<ConsolidateEmailMappingEntity, Long>,
		JpaSpecificationExecutor<ConsolidateEmailMappingEntity> {

	List<ConsolidateEmailMappingEntity> findByReturnTypeAndIsFirstEmailTriggeredTrueAndIsSecondEmailTriggeredFalseAndIsSecondEmailEligibleTrue(
			String returnType);

	@Modifying
	@Query("UPDATE ConsolidateEmailMappingEntity c SET "
			+ " c.isSecondEmailTriggered = true, c.updatedOn =:updatedOn"
			+ " where c.primaryEmailId =:primaryEmailId and"
			+ " c.returnType =:returnType and c.getCallDate =:getCallDate"
			+ " and c.isSecondEmailEligible = true")
	public int updateSecondEmailTriggered(
			@Param("updatedOn") LocalDateTime updatedOn,
			@Param("primaryEmailId") String primaryEmailId,
			@Param("returnType") String returnType,
			@Param("getCallDate") LocalDate getCallDate);
	
}
