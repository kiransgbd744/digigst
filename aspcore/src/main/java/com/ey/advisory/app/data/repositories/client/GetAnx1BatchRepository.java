/**
 * 
 */
package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;

/**
 * @author Hemasundar.J
 *
 */
@Repository("getAnx1BatchRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GetAnx1BatchRepository
		extends CrudRepository<GetAnx1BatchEntity, Long> {

	@Modifying
	@Query("UPDATE GetAnx1BatchEntity b SET b.isDelete = true WHERE "
			+ "b.isDelete = false AND b.type = "
			+ ":type AND b.apiSection = :apiSection AND "
			+ "b.sgstin =:sgstin AND b.taxPeriod =:taxPeriod")
	void softlyDelete(@Param("type") String type,
			@Param("apiSection") String apiSection,
			@Param("sgstin") String sgstin,
			@Param("taxPeriod") String taxPeriod);

	@Modifying
	@Query("UPDATE GetAnx1BatchEntity b SET b.isDelete = true WHERE "
			+ "b.isDelete = false AND b.type = "
			+ ":type AND b.apiSection = :apiSection AND "
			+ "b.sgstin =:sgstin AND b.taxPeriod =:taxPeriod AND "
			+ "b.fromTime =:fromTime")
	void softlyDeleteByFromTime(@Param("type") String type,
			@Param("apiSection") String apiSection,
			@Param("sgstin") String sgstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("fromTime") String fromTime);

	@Modifying
	@Query("UPDATE GetAnx1BatchEntity b SET b.isDelete = true WHERE "
			+ "b.isDelete = false AND b.type = "
			+ ":type AND b.apiSection = :apiSection AND "
			+ "b.sgstin =:sgstin AND b.portCode =:portCode "
			+ "AND b.billOfEntryNum =:billOfEntryNum AND b.billOfEntryCreated =:billOfEntryCreated")
	void softlyDeleteByPortCodeAndBillOfDetails(@Param("type") String type,
			@Param("apiSection") String apiSection,
			@Param("sgstin") String sgstin, @Param("portCode") String portCode,
			@Param("billOfEntryNum") Long billOfEntryNum,
			@Param("billOfEntryCreated") LocalDate billOfEntryCreated);

	@Query("SELECT g.sgstin FROM GetAnx1BatchEntity g WHERE g.requestId = "
			+ ":requestId and g.isDelete=false")
	public List<String> findGstinrequestId(@Param("requestId") Long requestId);

	public GetAnx1BatchEntity findByIdAndIsDeleteFalse(@Param("id") Long id);

	@Query("SELECT type FROM GetAnx1BatchEntity b WHERE b.sgstin =:sgstin AND "
			+ "b.taxPeriod = :taxPeriod AND b.apiSection = :apiSection "
			+ "AND b.status IN :status AND b.isDelete = false")
	public List<String> findBatchByStatus(@Param("sgstin") String sgstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("apiSection") String apiSection,
			@Param("status") List<String> status);

	@Query("SELECT type,taxPeriod FROM GetAnx1BatchEntity b WHERE b.sgstin =:sgstin AND "
			+ "b.taxPeriod IN :taxPeriod AND b.apiSection = :apiSection "
			+ "AND b.status IN :status AND b.isDelete = false")
	public List<String[]> findBatchByStatus(@Param("sgstin") String sgstin,
			@Param("taxPeriod") List<String> taxPeriod,
			@Param("apiSection") String apiSection,
			@Param("status") List<String> status);

	@Query("SELECT b.status FROM GetAnx1BatchEntity b WHERE b.sgstin =:sgstin "
			+ "AND b.taxPeriod = :taxPeriod AND b.userRequestId = "
			+ ":userRequestId and b.isDelete=false")
	public List<String> findStatusByUserRequestIdAndIsDeleteFalse(
			@Param("sgstin") String sgstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("userRequestId") Long userRequestId);

	@Query("SELECT e.createdOn FROM GetAnx1BatchEntity e WHERE e.id = (SELECT "
			+ "MAX(b.id) FROM GetAnx1BatchEntity b WHERE b.apiSection = "
			+ ":apiSection AND b.sgstin =:sgstin AND b.taxPeriod =:taxPeriod "
			+ "AND b.type =:type AND b.status IN :status)")
	public Object findLastSuccessDate(@Param("apiSection") String apiSection,
			@Param("sgstin") String sgstin,
			@Param("taxPeriod") String taxPeriod, @Param("type") String type,
			@Param("status") List<String> status);

	@Modifying
	@Query("UPDATE GetAnx1BatchEntity b SET b.retryCount = :retryCount WHERE "
			+ "b.id = :id")
	void updateRetryCount(@Param("retryCount") Long retryCount,
			@Param("id") Long id);

	@Query("SELECT COUNT(*) FROM GetAnx1BatchEntity "
			+ "WHERE sgstin=:gstin AND taxPeriod = :taxperiod ")
	public int gstinCount(@Param("gstin") String gstin,
			@Param("taxperiod") String taxperiod);

	@Query(value = "SELECT top 1 b.IS_FILED FROM GETANX1_BATCH_TABLE b WHERE "
			+ "b.GSTIN =:sgstin AND b.RETURN_PERIOD = :taxPeriod AND "
			+ "b.API_SECTION = :apiSection AND b.GET_TYPE =:type AND "
			+ "b.IS_DELETE = false", nativeQuery = true)
	public String findByGstinAndTaxPeriod(@Param("sgstin") String sgstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("apiSection") String apiSection, @Param("type") String type);

	@Query(value = "SELECT MAX(status)status,MAX(START_TIME)START_TIME FROM GETANX1_BATCH_TABLE WHERE "
			+ "GSTIN =:sgstin AND RETURN_PERIOD BETWEEN :taxPeriodFrom AND :taxPeriodTo AND "
			+ "API_SECTION = 'GSTR1' AND IS_DELETE = false", nativeQuery = true)
	public List<Object[]> getGstr1StatusByGstinDetails(
			@Param("sgstin") String sgstin,
			@Param("taxPeriodFrom") String taxPeriodFrom,
			@Param("taxPeriodTo") String taxPeriodTo);

	@Query("FROM GetAnx1BatchEntity WHERE id=:id ")
	public GetAnx1BatchEntity findGstinAndTaxPeriodById(@Param("id") Long id);

	public GetAnx1BatchEntity findByUserRequestIdAndTypeAndIsDeleteFalse(
			@Param("userRequestId") Long userRequestId,
			@Param("type") String type);

	@Query("SELECT COUNT(*) FROM GetAnx1BatchEntity "
			+ "WHERE sgstin=:sgstin AND derTaxPeriod BETWEEN :taxperiodFrom AND :taxperiodTo ")
	public int gstinCountByTaxPerFromTo(@Param("sgstin") String sgstin,
			@Param("taxperiodFrom") int taxperiodFrom,
			@Param("taxperiodTo") int taxperiodTo);

	@Query("SELECT distinct status FROM GetAnx1BatchEntity "
			+ "WHERE type = 'GSTR1_GETSUM' AND apiSection = 'GSTR1' AND isDelete = FALSE "
			+ " AND sgstin=:sgstin AND derTaxPeriod BETWEEN :dertaxperiodFrom AND :dertaxperiodTo ")
	public List<String> getStatusforGstinAndTaxperiod(
			@Param("sgstin") String sgstin,
			@Param("dertaxperiodFrom") int dertaxperiodFrom,
			@Param("dertaxperiodTo") int dertaxperiodTo);

	@Query("SELECT status,taxPeriod,startTime FROM GetAnx1BatchEntity "
			+ "WHERE type = 'GSTR1_GETSUM' AND apiSection = 'GSTR1' AND isDelete = FALSE "
			+ " AND sgstin =:sgstin AND derTaxPeriod BETWEEN :dertaxperiodFrom AND :dertaxperiodTo "
			+ "  ORDER BY taxPeriod ASC ")
	public List<Object[]> getGstr1StatusAndReturnPeriod(
			@Param("sgstin") String sgstin,
			@Param("dertaxperiodFrom") int dertaxperiodFrom,
			@Param("dertaxperiodTo") int dertaxperiodTo);

	@Query("SELECT b.sgstin,b.status,b.createdOn FROM GetAnx1BatchEntity b"
			+ " WHERE b.sgstin in (:listGstin) AND b.apiSection =:apiSection "
			+ "AND b.type in (:getType) AND b.taxPeriod =:taxPeriod AND b.isDelete=false")
	List<Object[]> findActiveGstr1GetStatus(
			@Param("listGstin") List<String> gstnsList,
			@Param("apiSection") String apiSection,
			@Param("getType") List<String> getType,
			@Param("taxPeriod") String taxPeriod);

	@Query("SELECT b FROM GetAnx1BatchEntity b"
			+ " WHERE b.sgstin in (:listGstin) AND b.apiSection =:apiSection "
			+ "AND b.type = :getType AND b.taxPeriod =:taxPeriod AND b.isDelete=false")
	List<GetAnx1BatchEntity> findActiveStatus(
			@Param("listGstin") List<String> gstnsList,
			@Param("apiSection") String apiSection,
			@Param("getType") String getType,
			@Param("taxPeriod") String taxPeriod);

	@Query("SELECT isFiled FROM GetAnx1BatchEntity "
			+ "WHERE sgstin =:sgstin AND taxPeriod = :taxPeriod AND "
			+ " apiSection =:apiSection AND isDelete = FALSE AND status = :status")
	Object findIsFiled(@Param("sgstin") String sgstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("status") String status,@Param("apiSection") String apiSection);

	@Query("SELECT type FROM GetAnx1BatchEntity b WHERE b.sgstin =:sgstin AND "
			+ "b.apiSection = :apiSection AND b.status IN :status AND b.isDelete = false")
	public List<String> findBatchByStatus(@Param("sgstin") String sgstin,
			@Param("apiSection") String apiSection,
			@Param("status") List<String> status);

	@Query("SELECT b.status FROM GetAnx1BatchEntity b WHERE b.sgstin =:sgstin AND "
			+ " b.taxPeriod in (:taxPeriodList) AND"
			+ " b.apiSection = :apiSection AND  b.type in (:type)"
			+ " AND b.status in (:status) AND b.isDelete = false AND "
			+ " b.isAutoGet = true")
	public List<String> findBatchByStatusForRecon(
			@Param("sgstin") String sgstin,
			@Param("taxPeriodList") List<String> taxPeriodList,
			@Param("apiSection") String apiSection,
			@Param("type") List<String> type,
			@Param("status") List<String> status);

	@Query("SELECT b.status FROM GetAnx1BatchEntity b WHERE b.sgstin =:sgstin AND "
			+ "(b.taxPeriod = :prevTaxPeriod OR b.taxPeriod = :curTaxPeriod) AND"
			+ " b.apiSection = :apiSection AND  b.type in (:type)"
			+ " AND b.status in (:status) AND b.isDelete = false AND "
			+ " b.isAutoGet = true")
	public List<String> findBatchByStatusForRecon(
			@Param("sgstin") String sgstin,
			@Param("prevTaxPeriod") String prevTaxPeriod,
			@Param("curTaxPeriod") String curTaxPeriod,
			@Param("apiSection") String apiSection,
			@Param("type") List<String> type,
			@Param("status") List<String> status);

	@Query("SELECT COUNT(*) FROM GetAnx1BatchEntity WHERE sgstin IN :gstins AND "
			+ "taxPeriod BETWEEN :taxperiodFrom AND :taxperiodTo AND apiSection =:apiSection "
			+ "AND status IN :status AND isAutoGet = true AND isDelete = false")
	public int inprogressAutoGetBatchesCountByByGstinAndTaxPerFromToAndReturnType(
			@Param("gstins") List<String> gstins,
			@Param("taxperiodFrom") String taxperiodFrom,
			@Param("taxperiodTo") String taxperiodTo,
			@Param("apiSection") String apiSection,
			@Param("status") List<String> status);

	@Query("SELECT taxPeriod,status,type FROM GetAnx1BatchEntity WHERE sgstin =:gstin AND "
			+ "taxPeriod BETWEEN :taxperiodFrom AND :taxperiodTo AND apiSection =:apiSection AND "
			+ "isAutoGet = true AND isDelete = false ORDER BY taxPeriod,status,type")
	public List<Object[]> getBatchesByByGstinAndTaxPerFromToAndReturnType(
			@Param("gstin") String gstin,
			@Param("taxperiodFrom") String taxperiodFrom,
			@Param("taxperiodTo") String taxperiodTo,
			@Param("apiSection") String apiSection);

	@Query("SELECT DISTINCT sgstin FROM GetAnx1BatchEntity WHERE sgstin IN :gstins AND "
			+ "taxPeriod BETWEEN :taxperiodFrom AND :taxperiodTo AND apiSection =:apiSection "
			+ "AND isAutoGet = true AND isDelete = false")
	public List<String> findDistinctGstins(@Param("gstins") List<String> gstins,
			@Param("taxperiodFrom") String taxperiodFrom,
			@Param("taxperiodTo") String taxperiodTo,
			@Param("apiSection") String apiSection);

	@Query("SELECT MIN(taxPeriod) FROM GetAnx1BatchEntity WHERE sgstin IN :gstins AND "
			+ "apiSection =:apiSection AND (createdOn >= :getcallDate OR endTime  >= :getcallDate) "
			+ "AND isAutoGet = true AND isDelete = false")
	public String findMinTaxPeriods(@Param("gstins") List<String> gstins,
			@Param("apiSection") String apiSection,
			@Param("getcallDate") LocalDate getcallDate);

	@Query("SELECT MAX(taxPeriod) FROM GetAnx1BatchEntity WHERE sgstin IN :gstins AND "
			+ "apiSection =:apiSection AND (createdOn >= :getcallDate OR endTime  >= :getcallDate) "
			+ "AND isAutoGet = true AND isDelete = false")
	public String findMaxTaxPeriods(@Param("gstins") List<String> gstins,
			@Param("apiSection") String apiSection,
			@Param("getcallDate") LocalDate getcallDate);

	@Modifying
	@Query("UPDATE GetAnx1BatchEntity b SET b.status =:status, b.endTime =:endTime"
			+ " , b.isTokenResponse =:isTokenResponse,b.errorCode =:errorCode,"
			+ "b.errorDesc =:errorDesc WHERE b.isDelete = false "
			+ " AND b.type =:type AND b.apiSection =:apiSection AND "
			+ " b.sgstin =:sgstin AND b.taxPeriod =:taxPeriod")
	void updateStatus(@Param("status") String status,
			@Param("type") String type, @Param("apiSection") String apiSection,
			@Param("sgstin") String sgstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("endTime") LocalDateTime endTime,
			@Param("isTokenResponse") boolean isTokenResponse,
			@Param("errorCode") String errorCode,
			@Param("errorDesc") String errorDesc);

	Optional<GetAnx1BatchEntity> findByTypeAndApiSectionAndSgstinAndTaxPeriodAndStatusInAndIsDeleteFalse(
			String type, String apiSection, String sgstin, String taxPeriod,
			List<String> status);

	Optional<GetAnx1BatchEntity> findByTypeAndApiSectionAndSgstinAndTaxPeriodAndIsDeleteFalse(
			String type, String apiSection, String sgstin, String taxPeriod);

	@Query("SELECT b.status FROM GetAnx1BatchEntity b WHERE b.sgstin =:sgstin AND "
			+ " b.taxPeriod in (:taxPeriodList) AND"
			+ " b.apiSection = :apiSection AND  b.type in (:type)"
			+ " AND b.isDelete = false")
	public List<String> findBatchForRecon(@Param("sgstin") String sgstin,
			@Param("taxPeriodList") List<String> taxPeriodList,
			@Param("apiSection") String apiSection,
			@Param("type") List<String> type);

	@Query("SELECT sgstin,taxPeriod,type,status,endTime,startTime FROM GetAnx1BatchEntity "
			+ "WHERE type IN ('B2B','B2BA','CDN','CDNA') AND apiSection = 'GSTR6A' AND isDelete = FALSE "
			+ " AND sgstin IN :gstins AND derTaxPeriod BETWEEN :dertaxperiodFrom AND :dertaxperiodTo "
			+ "  ORDER BY taxPeriod ASC ")
	public List<Object[]> getGstr6AStatusAndReturnPeriod(
			@Param("gstins") List<String> gstins,
			@Param("dertaxperiodFrom") int dertaxperiodFrom,
			@Param("dertaxperiodTo") int dertaxperiodTo);

	@Modifying
	@Query("UPDATE GetAnx1BatchEntity b SET b.sftpStatus =:status,b.errorDesc =:errorDesc where b.id =:id ")
	void updateSftpErrorStatus(@Param("status") String status,
			@Param("id") Long id, @Param("errorDesc") String errorDesc);

	@Modifying
	@Query("UPDATE GetAnx1BatchEntity b SET b.sftpStatus =:status where b.id =:id ")
	void updateSftpStatus(@Param("status") String status, @Param("id") Long id);

	@Query("SELECT b FROM GetAnx1BatchEntity b WHERE b.isDelete = false "
			+ "AND b.status IN (:status) "
			+ "AND (TIMESTAMPDIFF(SECOND, b.createdOn, CURRENT_TIMESTAMP) / 3600) > :waitTime")
	public List<GetAnx1BatchEntity> findbatchestobeFailed(
			@Param("status") List<String> status,
			@Param("waitTime") Long waitTime);

	@Modifying
	@Query("UPDATE GetAnx1BatchEntity b SET b.errorDesc =:errorDesc,b.status =:status where"
			+ " b.id in (:ids)")
	void updateBatchStatus(@Param("errorDesc") String errorDesc,
			@Param("status") String status, @Param("ids") List<Long> ids);

	@Query("SELECT errorDesc FROM GetAnx1BatchEntity WHERE sgstin =:gstin AND "
			+ "taxPeriod =:taxPeriod AND apiSection =:apiSection AND "
			+ " isDelete = false AND type =:type AND status IN ('FAILED','SUCCESS_WITH_NO_DATA') ")
	public String getFailedErrMsg(@Param("gstin") String gstin,
			@Param("type") String type, @Param("taxPeriod") String taxPeriod,
			@Param("apiSection") String apiSection);

	@Query("SELECT b FROM GetAnx1BatchEntity b"
			+ " WHERE b.sgstin in (:listGstin) AND b.apiSection =:apiSection "
			+ "AND b.type = :getType AND b.taxPeriod =:taxPeriod AND b.isDelete=false")
	List<GetAnx1BatchEntity> findRequestIDs(
			@Param("listGstin") List<String> gstnsList,
			@Param("apiSection") String apiSection,
			@Param("getType") String getType,
			@Param("taxPeriod") String taxPeriod);

	@Query("SELECT b FROM GetAnx1BatchEntity b "
			+ "WHERE b.apiSection = 'GSTR2A'  AND b.type = 'GSTR2B_GET_ALL' "
			+ " AND  b.sgstin IN (:gstins) AND derTaxPeriod BETWEEN :dertaxperiodFrom AND :dertaxperiodTo "
			+ " AND b.status = 'SUCCESS'  AND  b.isDelete = false ORDER BY taxPeriod ASC")
	List<GetAnx1BatchEntity> findRequestIDs(
			@Param("gstins") List<String> gstins,
			@Param("dertaxperiodFrom") int dertaxperiodFrom,
			@Param("dertaxperiodTo") int dertaxperiodTo);

	@Query("SELECT b FROM GetAnx1BatchEntity b "
			+ "WHERE b.apiSection = 'GSTR2B'  AND b.type = 'GSTR2B_GET_ALL' "
			+ "AND b.sgstin IN :gstins AND b.derTaxPeriod BETWEEN :dertaxperiodFrom AND :dertaxperiodTo "
			+ "AND b.status IN ('SUCCESS') " + "AND b.isDelete = false")
	List<GetAnx1BatchEntity> findGSTR2bGetAllStatus(
			@Param("gstins") List<String> gstins,
			@Param("dertaxperiodFrom") int dertaxperiodFrom,
			@Param("dertaxperiodTo") int dertaxperiodTo);

	@Query("SELECT b FROM GetAnx1BatchEntity b "
			+ "WHERE b.apiSection = 'GSTR2A'  "
			+ "AND b.type IN ('B2B','B2BA','CDNR','CDNRA','ECOM','ECOMA') "
			+ "AND b.status IN ('SUCCESS_WITH_NO_DATA', 'SUCCESS') "
			+ "AND b.sgstin IN :gstins AND b.startTime BETWEEN :startDate AND :endDate "
			+ "AND b.isDelete = false ORDER BY taxPeriod ASC")
	List<GetAnx1BatchEntity> findGSTR2aGetAllStatuses(
			@Param("gstins") List<String> gstins,
			@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate);

	@Query("SELECT b FROM GetAnx1BatchEntity b "
			+ "WHERE b.apiSection = 'GSTR2A'  "
			+ "AND b.status IN ('SUCCESS_WITH_NO_DATA', 'SUCCESS') "
			+ "AND b.sgstin = :gstin " + "AND b.taxPeriod = :taxPeriod "
			+ "AND b.type = :type "
			+ "AND b.isDelete = false ORDER BY taxPeriod ASC")
	List<GetAnx1BatchEntity> findGSTR2StatusBasedOnType(
			@Param("gstin") String gstin, @Param("taxPeriod") String taxPeriod,
			@Param("type") String type);

	@Query("SELECT b FROM GetAnx1BatchEntity b WHERE b.sgstin in (:sgstin) AND "
			+ " b.taxPeriod in (:taxPeriodList) AND"
			+ " b.apiSection = :apiSection AND  b.type in (:type)"
			+ " AND b.isDelete = false")
	public List<GetAnx1BatchEntity> findBatchDetails(
			@Param("sgstin") List<String> sgstin,
			@Param("taxPeriodList") List<String> taxPeriodList,
			@Param("apiSection") String apiSection,
			@Param("type") List<String> type);

	@Modifying
	@Query("UPDATE GetAnx1BatchEntity b SET b.status = :status WHERE b.id = :id")
	void updateStatusById(@Param("id") Long id, @Param("status") String status);

	@Modifying
	@Query("UPDATE GetAnx1BatchEntity b SET b.status =:status,b.errorDesc =:errorDesc,"
			+ "b.errorCode =:errorCode,b.endTime =:endTime,b.isTokenResponse =:isTokenResponse where"
			+ " b.id =:id and b.status NOT IN ('FAILED','SUCCESS_WITH_NO_DATA','SUCCESS')")
	int updateBatchDetails(@Param("status") String status,
			@Param("errorDesc") String errorDesc,
			@Param("errorCode") String errorCode,
			@Param("endTime") LocalDateTime endTime,
			@Param("isTokenResponse") boolean isTokenResponse,
			@Param("id") Long id);
	
	@Query("SELECT isFiled FROM GetAnx1BatchEntity "
			+ "WHERE sgstin =:gstin AND taxPeriod = :taxPeriod AND "
			+ " apiSection = 'GSTR1A' AND isDelete = FALSE AND status = :status")
	Object findIsFiledGstr1A(@Param("gstin") String gstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("status") String status);
	
	
	@Query("SELECT b FROM GetAnx1BatchEntity b WHERE b.sgstin IN :gstins "
	        + "AND b.taxPeriod = :period AND b.apiSection = :apiSection AND b.isDelete= false")
	List<GetAnx1BatchEntity> findStatusesByGstinInAndApiSection(
	        @Param("gstins") List<String> gstins,
	        @Param("period") String period,
	        @Param("apiSection") String apiSection);

	@Query("SELECT b FROM GetAnx1BatchEntity b WHERE b.sgstin IN :gstins "
	        + "AND b.taxPeriod = :period AND b.apiSection = :apiSection AND b.type = :getType AND b.isDelete= false")
	List<GetAnx1BatchEntity> findStatusesByGstinInAndApiSectionAndGetType(
	        @Param("gstins") List<String> gstins,
	        @Param("period") String period,
	        @Param("apiSection") String apiSection,
	        @Param("getType") String getType);
	
	@Query("SELECT g FROM GetAnx1BatchEntity g WHERE g.sgstin = :gstin"
			+ " AND g.taxPeriod = :taxPeriod AND g.apiSection = :apiSection "
			+ " AND g.type IN :getType And g.isDelete= false")
	List<GetAnx1BatchEntity> findBySgstinAndTaxPeriodAndApiSectionAndTypeIn(@Param("gstin") String gstin,
			@Param("taxPeriod") String taxPeriod, @Param("apiSection") String apiSection,
			@Param("getType") List<String> types);


	@Modifying
	@Query("UPDATE GetAnx1BatchEntity b SET b.errorCode =:errorCode,b.errorDesc =:errorDesc, b.status =:status where b.id =:id ")
	void updateGstr2bStatus(@Param("status") String status,
			@Param("id") Long id, @Param("errorDesc") String errorDesc,@Param("errorCode") String errorCode );
	
	
	@Query("SELECT b from GetAnx1BatchEntity b "
			+ "WHERE b.taxPeriod IN (:taxPeriod) AND b.apiSection = 'GSTR3B' And b.isDelete= false ")
	public List<GetAnx1BatchEntity> getAllwithTaxPeriod(
			@Param("taxPeriod") List<String> taxPeriod);
	
	@Query("SELECT g.startTime FROM GetAnx1BatchEntity g " +
	           "WHERE g.isDelete = false " +
	           "AND g.requestId = :requestId")
	Optional<LocalDateTime> findStartTimeByRequestId(@Param("requestId") Long requestId);
	
	@Query("SELECT g FROM GetAnx1BatchEntity g WHERE g.sgstin  IN (:gstin) "
			+ "AND g.derTaxPeriod >= :derivedStartPeriod AND "
			+ "g.derTaxPeriod <= :derivedEndPeriod "
			+ "AND g.apiSection = :apiSection "
			+ "And g.isDelete= false")
	List<GetAnx1BatchEntity> findBySgstinAndDerTaxPeriodAndApiSection(
			@Param("gstin") List<String> gstinList,
			@Param("derivedStartPeriod") Integer derivedStartPeriod,
			@Param("derivedEndPeriod") Integer derivedEndPeriod, 
			@Param("apiSection") String apiSection);
	
	@Modifying
	@Query("UPDATE GetAnx1BatchEntity b SET b.isDelete = true WHERE "
			+ "b.isDelete = false AND b.type = "
			+ ":type AND b.apiSection = :apiSection AND "
			+ "b.sgstin =:sgstin AND b.taxPeriod =:taxPeriod AND "
			+ " b.imsReturnType =:imsReturnType ")
	void softlyDeleteSupplierIms(@Param("type") String type,
			@Param("apiSection") String apiSection,
			@Param("sgstin") String sgstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("imsReturnType") String imsReturnType);
	
	@Query("SELECT b FROM GetAnx1BatchEntity b "
			+ "WHERE b.apiSection = 'SUPPLIER_IMS' "
			+ "AND b.sgstin = :gstin " + "AND b.taxPeriod = :taxPeriod "
			+ "AND b.type IN (:type) "
			+ "AND b.isDelete = false ")
	List<GetAnx1BatchEntity> findImsStatusBasedOnType(
			@Param("gstin") String gstin, @Param("taxPeriod") String taxPeriod,
			@Param("type") List<String> type);
	
	@Query("SELECT b FROM GetAnx1BatchEntity b "
			+ "WHERE b.apiSection = 'SUPPLIER_IMS' "
			+ "AND b.sgstin = :gstin " 
			+ "AND b.taxPeriod = :taxPeriod "
			+ "AND b.type = :type "
			+ "AND b.imsReturnType =:imsReturnType "
			+ "AND b.isDelete = true " 
			+ "ORDER BY b.createdOn DESC")
	 List<GetAnx1BatchEntity> findImsStatusForDuplicateCheck(
			@Param("gstin") String gstin, @Param("taxPeriod") String taxPeriod,
			@Param("type") String type,
			@Param("imsReturnType") String imsReturnType);
	
	@Modifying
	@Query("UPDATE GetAnx1BatchEntity b SET b.hashKey =:hashKey WHERE "
			+ "b.isDelete = false AND b.type = :type "
			+ "AND b.apiSection = 'SUPPLIER_IMS' "
			+ "AND b.sgstin =:sgstin AND b.taxPeriod =:taxPeriod "
			+ "AND b.imsReturnType =:imsReturnType ")
	void updateHashSupplierIms(@Param("hashKey") String hashKey,
			@Param("type") String type,
			@Param("sgstin") String sgstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("imsReturnType") String imsReturnType);
}