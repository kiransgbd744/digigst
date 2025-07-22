package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.einv.client.EinvoiceEntity;

/**
 * @author Arun K.A
 *
 */
@Repository("EinvoiceRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface EinvoiceRepository extends JpaRepository<EinvoiceEntity, Long>,
		JpaSpecificationExecutor<EinvoiceEntity> {

	@Query("SELECT doc FROM  EinvoiceEntity doc "
			+ "WHERE doc.irn = :irn")
	Optional<EinvoiceEntity> getEinvDetails(@Param("irn") String irn);
	
	@Modifying
	@Query("UPDATE EinvoiceEntity einv SET "
			+ "einv.isDelete=:status,einv.cancelDate = :cancelDate"
			+ " WHERE einv.irn=:irn")
	void updateEinvStatusByIrn(@Param("irn") String irn,
			@Param("status") boolean status,
			@Param("cancelDate") LocalDateTime cancelDate);

	@Modifying
	@Query("UPDATE EinvoiceEntity einv SET "
			+ "einv.isDelete=:status,einv.cancelDate=:cancelDate,einv.cancelReason=:cancelReason,"
			+ "einv.cancelRemarks=:cancelRemarks WHERE einv.irn=:irn")
	void updateEinvCanStatusByIrn(@Param("irn") String irn,
			@Param("status") boolean status,
			@Param("cancelDate") LocalDateTime cancelDate,
			@Param("cancelRemarks") String cancelRemarks,
			@Param("cancelReason") String cancelReason);

	EinvoiceEntity findByIrn(String irn);

}