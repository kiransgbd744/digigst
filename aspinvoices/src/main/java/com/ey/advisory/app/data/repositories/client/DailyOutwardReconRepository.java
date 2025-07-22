/**
 * 
 */
package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.DailyOutwardReconEntity;

/**
 * @author Laxmi.Salukuti
 *
 */
@Repository("DailyOutwardReconRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface DailyOutwardReconRepository
		extends CrudRepository<DailyOutwardReconEntity, Long> {

	@Query("SELECT doc.accountingVoucherDate,SUM(doc.extractedDocCount),"
			+ "SUM(doc.structuralErrorCount),SUM(doc.onHold),SUM(doc.availableForPush),"
			+ "SUM(doc.pushedToCloud),SUM(doc.erroredInPush) FROM DailyOutwardReconEntity doc "
			+ "WHERE doc.accountingVoucherDate BETWEEN :accVoucherDateFrom "
			+ "AND :accVoucherDateTo AND doc.isDelete = FALSE GROUP BY "
			+ "doc.accountingVoucherDate ORDER BY doc.accountingVoucherDate DESC")
	List<Object[]> findByAccVoucherDate(
			@Param("accVoucherDateFrom") LocalDate accVoucherDateFrom,
			@Param("accVoucherDateTo") LocalDate accVoucherDateTo);

	@Query("SELECT rec.accountingVoucherDate,COUNT(distinct hdr.id ) as AVAIL_CLOUD "
			+ "FROM DailyOutwardReconEntity rec INNER JOIN OutwardTransDocument hdr "
			+ "ON hdr.accountingVoucherDate = rec.accountingVoucherDate AND "
			+ "dataOriginTypeCode = 'A' WHERE rec.accountingVoucherDate BETWEEN "
			+ ":accVoucherDateFrom AND :accVoucherDateTo AND rec.isDelete = FALSE "
			+ "AND hdr.isDeleted = FALSE GROUP BY rec.accountingVoucherDate "
			+ "ORDER BY rec.accountingVoucherDate DESC")
	List<Object[]> findAvailCountFromDocHeader(
			@Param("accVoucherDateFrom") LocalDate accVoucherDateFrom,
			@Param("accVoucherDateTo") LocalDate accVoucherDateTo);
	
	@Modifying
	@Query(value = "UPDATE DailyOutwardReconEntity SET isDelete = TRUE "
			+ "WHERE accountingVoucherDate = :accVoucherDate "
			//+ "AND sourceId = :sourceId"
			+ " AND isDelete = FALSE")
	void softDelete(@Param("accVoucherDate") LocalDate accVoucherDate/*,
		@Param("sourceId") String sourceId*/);

}
