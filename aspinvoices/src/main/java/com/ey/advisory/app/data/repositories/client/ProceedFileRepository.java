package com.ey.advisory.app.data.repositories.client;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.ProceedFileEntity;

/**
 * 
 * @author SriBhavya
 *
 */
@Repository("ProceedFileRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface ProceedFileRepository
		extends JpaRepository<ProceedFileEntity, Long>, JpaSpecificationExecutor<ProceedFileEntity> {

	Optional<ProceedFileEntity> findTop1ByGstinAndReturnPeriodAndReturnTypeAndIsDeleteFalseOrderByIdDesc(
			@Param("gstin") String gstin, 
			@Param("returnPeriod") String returnPeriod,
			@Param("returnType") String returnType);
	
}