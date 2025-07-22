package com.ey.advisory.app.data.repositories.client.asprecon;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.asprecon.gstr2.recon.result.Gstr2Link2APREntity;

/**
 * @author vishal.verma
 *
 */

@Repository("Gstr2Link2APRrepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface Gstr2Link2APRrepository
		extends JpaRepository<Gstr2Link2APREntity, Long>,
		JpaSpecificationExecutor<Gstr2Link2APREntity> {

	@Modifying
	@Query("UPDATE Gstr2Link2APREntity SET "
			+ " gstr3BTaxPeriod =:gstr3BTaxPeriod, forceMatch = :forceMatch, "
			+ "userResponse =:userResponse, "
			+ "prePrReportType =:prePrReportType, "
			+ "currentReportType =:currentReportType, " + " reportTypeId = 17 "
			+ " WHERE  reconLinkId =:reconLinkId")
	public int updateCount(@Param("reconLinkId") Long reconLinkId,
			@Param("gstr3BTaxPeriod") String gstr3BTaxPeriod,
			@Param("forceMatch") String forceMatch,
			@Param("userResponse") String userResponse,
			@Param("prePrReportType") String prePrReportType,
			@Param("currentReportType") String currentReportType);

	@Modifying
	@Query("UPDATE Gstr2Link2APREntity SET "
			+ "userResponse =:userResponse, gstr3BTaxPeriod = null, "
			+ " forceMatch =:userResponse, "
			+ " currentReportType =:currentReportType, "
			+ " reportTypeId =:reportTypeId "
			+ " WHERE  reconLinkId =:reconLinkId")
	public int updateNoActionCount(@Param("reconLinkId") Long reconLinkId,
			@Param("userResponse") String userResponse,
			@Param("currentReportType") String currentReportType,
			@Param("reportTypeId") int reportTypeId);

	@Query("Select IFNULL(prTaxPeriod,a2TaxPeriod) AS prTaxPeriod "
			+ " from Gstr2Link2APREntity WHERE reconLinkId =:reconLinkId")
	public String findPrTaxPeriod(@Param("reconLinkId") Long reconLinkId);

	@Query("Select currentReportType from Gstr2Link2APREntity "
			+ "WHERE reconLinkId =:reconLinkId")
	public String findCurrentReportType(@Param("reconLinkId") Long reconLinkId);

	@Query("Select prePrReportType from Gstr2Link2APREntity "
			+ "WHERE reconLinkId =:reconLinkId")
	public String findPreviousReportType(
			@Param("reconLinkId") Long reconLinkId);

	@Query("select distinct currentReportType from Gstr2Link2APREntity "
			+ " WHERE isActive = true")
	public List<String> findAllReportType();

	@Query("select distinct gstr2Link2APREntity.prSuppGstin, "
			+ " gstr2Link2APREntity.prRecpGstin from Gstr2Link2APREntity gstr2Link2APREntity "
			+ " where gstr2Link2APREntity.prRetPeriod >= :fromTaxPeriod and "
			+ " gstr2Link2APREntity.prRetPeriod <= :toTaxPeriod  "
			+ " and gstr2Link2APREntity.currentReportType in :currentReportType ")
	public List<Object[]> findDistinctPrSupplierAndRecipient(
			@Param("fromTaxPeriod") Integer fromTaxPeriod,
			@Param("toTaxPeriod") Integer toTaxPeriod,
			@Param("currentReportType") List<String> prePrReportType);
	
}
