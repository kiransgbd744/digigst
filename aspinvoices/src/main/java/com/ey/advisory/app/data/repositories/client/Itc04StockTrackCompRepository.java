package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Itc04StockTrackingComputeEntity;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Repository("Itc04StockTrackCompRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Itc04StockTrackCompRepository
		extends JpaRepository<Itc04StockTrackingComputeEntity, Long>,
		JpaSpecificationExecutor<Itc04StockTrackingComputeEntity> {

	@Query("SELECT doc FROM Itc04StockTrackingComputeEntity doc "
			+ "WHERE doc.gstin IN (:gstins) "
			+ "AND doc.challanFromDate = :fromChallanDate AND doc.challanToDate = :toChallanDate AND "
			+ "doc.isActive = true AND doc.fy = :fy ")
	List<Itc04StockTrackingComputeEntity> findActiveDocsByChallanDate(
			@Param("fromChallanDate") LocalDate fromChallanDate,
			@Param("toChallanDate") LocalDate toChallanDate,
			@Param("fy") String fy, @Param("gstins") List<String> gstins);

	@Query("SELECT doc FROM Itc04StockTrackingComputeEntity doc "
			+ "WHERE doc.gstin IN (:gstins) "
			+ "AND doc.fromRetPeriod = :fromRetPeriod AND doc.toRetPeriod = :toRetPeriod AND "
			+ "doc.isActive = true AND doc.fy = :fy ")
	List<Itc04StockTrackingComputeEntity> findActiveDocsByReturnPeriod(
			@Param("fromRetPeriod") String fromRetPeriod,
			@Param("toRetPeriod") String toRetPeriod, @Param("fy") String fy,
			@Param("gstins") List<String> gstins);

	@Query("SELECT doc FROM Itc04StockTrackingComputeEntity doc "
			+ "WHERE doc.gstin IN (:gstins) " + "AND "
			+ "doc.isActive = true AND doc.fy = :fy ")
	List<Itc04StockTrackingComputeEntity> findActiveDocsByFy(
			@Param("fy") String fy, @Param("gstins") List<String> gstins);

	@Modifying
	@Query("UPDATE Itc04StockTrackingComputeEntity doc SET doc.reportStatus = :reportStatus "
			+ "WHERE doc.id in (:ids) and doc.isActive = true")
	public void updateReportStatus(@Param("ids") List<Long> ids,
			@Param("reportStatus") String reportStatus);

}
