package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.GstnGetStatusEntity;

@Repository("GstinGetStatusRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GstinGetStatusRepository
		extends CrudRepository<GstnGetStatusEntity, Long>,
		JpaSpecificationExecutor<GstnGetStatusEntity> {

	GstnGetStatusEntity findByGstinAndTaxPeriodAndReturnTypeAndSection(
			String gstin, String taxPeriod, String returnType, String section);

	@Modifying
	@Query("update GstnGetStatusEntity set status= :status, "
			+ "updatedOn = :updatedOn"
			+ "  where gstin = :gstin and returnType = :returnType"
			+ " and taxPeriod = :taxPeriod and section= :section")
	public int updateStatusAndFilePath(@Param("gstin") String gstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("returnType") String returnType,
			@Param("section") String section, @Param("status") String status,
			@Param("updatedOn") LocalDateTime updatedOn);

	@Modifying
	@Query("UPDATE GstnGetStatusEntity tblGstinStatus SET "
			+ "tblGstinStatus.csvGenerationFlag = :isCsvGenerated,"
			+ "tblGstinStatus.updatedOn = :updatedDate "
			+ "WHERE tblGstinStatus.gstin =:gstinId and "
			+ "tblGstinStatus.taxPeriod =:taxPeriod and "
			+ "tblGstinStatus.returnType =:returnType and "
			+ "tblGstinStatus.section =:apiCall")
	public int updateCsvGenerationStatus(
			@Param("isCsvGenerated") boolean isCsvGenerated,
			@Param("updatedDate") LocalDateTime updatedDate,
			@Param("gstinId") String gstinId,
			@Param("taxPeriod") String taxPeriod,
			@Param("returnType") String returnType,
			@Param("apiCall") String apiCall);

	@Query(nativeQuery = true, value = "select count(id) from GstnGetStatusEntity where "
			+ "gstin =:gstinId and returnType =:returnType and "
			+ "section =:apiCall and derivedTaxPeriod >= :derivedTaxPeriod"
			+ " and (csvGenerationFlag = 0 " + "or csvGenerationFlag IS NULL)")
	public long countCsvNotGeneratedRecords(@Param("gstinId") String gstinId,
			@Param("apiCall") String apiCall,
			@Param("returnType") String returnType,
			@Param("derivedTaxPeriod") Long derivedTaxPeriod);

	@Modifying
	@Query("UPDATE GstnGetStatusEntity tblGstinStatus SET"
			+ " tblGstinStatus.jobStatus = :jobStatus,"
			+ " tblGstinStatus.updatedOn = :updatedDate "
			+ "WHERE tblGstinStatus.gstin =:gstinId and"
			+ " tblGstinStatus.taxPeriod =:taxPeriod and"
			+ " tblGstinStatus.returnType =:returnType and"
			+ " tblGstinStatus.section =:apiCall")
	public int updateWorkflowStatus(@Param("jobStatus") String jobStatus,
			@Param("updatedDate") LocalDateTime updatedDate,
			@Param("gstinId") String gstinId,
			@Param("taxPeriod") String taxPeriod,
			@Param("returnType") String returnType,
			@Param("apiCall") String apiCall);

	@Modifying
	@Query("UPDATE GstnGetStatusEntity tblGstinStatus SET "
			+ "tblGstinStatus.csvGenerationFlag = :isCsvGenerated, "
			+ "tblGstinStatus.updatedOn = :updatedDate,"
			+ "tblGstinStatus.errorDescription = :errorDescription,"
			+ "tblGstinStatus.status = :status "
			+ "WHERE tblGstinStatus.gstin =:gstinId and "
			+ "tblGstinStatus.taxPeriod =:taxPeriod and "
			+ "tblGstinStatus.returnType =:returnType and "
			+ "tblGstinStatus.section =:section")
	public int updateGetGstnStatus(
			@Param("isCsvGenerated") boolean isCsvGenerated,
			@Param("status") String status,
			@Param("updatedDate") LocalDateTime updatedDate,
			@Param("errorDescription") String errorDescription,
			@Param("gstinId") String gstinId,
			@Param("taxPeriod") String taxPeriod,
			@Param("returnType") String returnType,
			@Param("section") String section);

	@Modifying
	@Query("SELECT tblGstinStatus.gstin, tblGstinStatus.taxPeriod, "
			+ " tblGstinStatus.section from GstnGetStatusEntity tblGstinStatus "
			+ " WHERE tblGstinStatus.csvGenerationFlag = :isCsvGenerated and "
			+ " tblGstinStatus.gstin in :gstins and "
			+ " tblGstinStatus.derivedTaxPeriod >= :derivedStartPeriod "
			+ " and tblGstinStatus.derivedTaxPeriod <= :derivedEndPeriod and "
			+ " tblGstinStatus.returnType =:returnType and "
			+ " tblGstinStatus.status = :jobStatus")
	public List<Object[]> getCsvFilesEligibleForZip(
			@Param("isCsvGenerated") boolean isCsvGenerated,
			@Param("gstins") List<String> gstins,
			@Param("derivedStartPeriod") Integer derivedStartPeriod,
			@Param("derivedEndPeriod") Integer derivedEndPeriod,
			@Param("returnType") String returnType,
			@Param("jobStatus") String jobStatus);

	@Query("SELECT e.section from GstnGetStatusEntity e where e.gstin = :gstin "
			+ "and e.taxPeriod = :taxPeriod and e.returnType = :returnType "
			+ "and e.status = :status")
	List<String> getSuccessSections(@Param("gstin") String gstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("returnType") String returnType,
			@Param("status") String status);

	@Modifying
	@Query("update GstnGetStatusEntity set status = :status, "
			+ "updatedOn = :updatedOn, errorDescription = :errorDesc"
			+ "  where gstin = :gstin and returnType = :returnType"
			+ " and taxPeriod = :taxPeriod")
	public int updateStatus(@Param("gstin") String gstin,
			@Param("taxPeriod") String taxPeriod,
			@Param("returnType") String returnType,
			@Param("status") String status,
			@Param("updatedOn") LocalDateTime updatedOn,
			@Param("errorDesc") String errorDesc);

	List<GstnGetStatusEntity> findByGstinAndTaxPeriodAndReturnType(String gstin,
			String taxPeriod, String returnType);

	List<GstnGetStatusEntity> findByGstinAndTaxPeriodInAndReturnType(
			String gstin, List<String> fyTaxPeriods, String retrunType);

	@Modifying
	@Query("update GstnGetStatusEntity set status= 'SUCCESS' "
			+ "  where gstin = :gstin and returnType = :returnType "
			+ " and section = :section")
	void updateStatusForSuccess(@Param("gstin") String gstin,
			@Param("section") String section,
			@Param("returnType") String returnType);

	@Modifying
	@Query("update GstnGetStatusEntity set status= :status "
			+ "  where gstin = :gstin and returnType = :returnType and "
			+ "section = :section and taxPeriod in (:taxPeriod)")
	void updateStatusForSuccessForReturnPeriodIn(@Param("gstin") String gstin,
			@Param("section") String section,
			@Param("taxPeriod") List<String> taxPeriod,
			@Param("status") String status,
			@Param("returnType") String returnType);

	@Modifying
	@Query("update GstnGetStatusEntity set status= :status "
			+ "  where gstin = :gstin and returnType = :returnType and "
			+ "section = :section and taxPeriod not in (:taxPeriod)")
	void updateStatusForSuccessForReturnPeriodNotIn(
			@Param("gstin") String gstin, @Param("section") String section,
			@Param("taxPeriod") List<String> taxPeriod,
			@Param("status") String status,
			@Param("returnType") String returnType);

	@Modifying
	@Query("update GstnGetStatusEntity set isdbLoad= :isdbLoad "
			+ "  where gstin = :gstin and returnType = :returnType and "
			+ "section = :section and taxPeriod = :taxPeriod")
	void updateDBLoad(@Param("gstin") String gstin,
			@Param("section") String section,
			@Param("taxPeriod") String taxPeriod,
			@Param("returnType") String returnType,
			@Param("isdbLoad") boolean isdbLoad);

	@Query("select gstin,taxPeriod,returnType,count(*)"
			+ " from  GstnGetStatusEntity "
			+ "  where isdbLoad= true and csvGenerationFlag = true and  "
			+ " returnType ='GSTR1' and  derivedTaxPeriod >= "
			+ " :derivedStPeriod and derivedTaxPeriod <= :derivedEndPeriod and "
			+ " section not in ('DOC_ISSUE','HSN_SUMMARY') group by gstin,taxPeriod,returnType")
	List<Object[]> findCsvDBLoadCompletedRecordsGstr1(
			@Param("derivedStPeriod") Integer derivedStPeriod,
			@Param("derivedEndPeriod") Integer derivedEndPeriod);

	@Query("select gstin,taxPeriod,returnType,count(*)"
			+ " from  GstnGetStatusEntity "
			+ "  where isdbLoad= true and csvGenerationFlag = true and  "
			+ " returnType = 'GSTR3B' and  derivedTaxPeriod >= "
			+ " :derivedStPeriod and derivedTaxPeriod <= :derivedEndPeriod "
			+ "group by gstin,taxPeriod,returnType")
	List<Object[]> findCsvDBLoadCompletedRecordsGstr3B(
			@Param("derivedStPeriod") Integer derivedStPeriod,
			@Param("derivedEndPeriod") Integer derivedEndPeriod);
}
