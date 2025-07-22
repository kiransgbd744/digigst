package com.ey.advisory.app.data.repositories.client.asprecon;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.service.ims.ImsErrorInvoiceEntity;

/**
 * 
 * @author Sakshi.jain
 *
 */
@Repository("ImsErrorInvoiceRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface ImsErrorInvoiceRepository
		extends JpaRepository<ImsErrorInvoiceEntity, Long>,
		JpaSpecificationExecutor<ImsErrorInvoiceEntity> {
	
	public List<ImsErrorInvoiceEntity> findByFileId(Long fileId);


	 @Query(value = "SELECT * FROM  TBL_GETIMS_ERROR "
				+ " WHERE SOURCE_TYPE = 'API' "
				+ "  AND TO_DATE(CREATED_ON) BETWEEN :fromDate AND :toDate", nativeQuery = true)
		List<ImsErrorInvoiceEntity> findBySourceTypeApiAndCreatedOnBetweenTotal(
				@Param("fromDate") LocalDateTime fromDate,
				@Param("toDate") LocalDateTime toDate);

	
}
