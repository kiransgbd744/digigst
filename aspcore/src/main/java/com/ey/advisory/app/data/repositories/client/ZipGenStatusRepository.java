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

import com.ey.advisory.admin.data.entities.client.ZipGenStatusEntity;

@Repository("ZipGenStatusRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface ZipGenStatusRepository
		extends CrudRepository<ZipGenStatusEntity, Long>,
		JpaSpecificationExecutor<ZipGenStatusEntity> {

	@Modifying
	@Query("UPDATE ZipGenStatusEntity zipGenSt SET "
			+ "zipGenSt.zipFilePath = :zipFilePath,"
			+ "zipGenSt.updatedOn = :updatedDate, "
			+ "zipGenSt.jobStatus = :jobStatus "
			+ "WHERE zipGenSt.gstin =:gstin and "
			+ "zipGenSt.taxPeriod =:taxPeriod and "
			+ "zipGenSt.returnType =:returnType")
	public int updateGstr9ZipFilePathIfNotNull(
			@Param("returnType") String returnType,
			@Param("gstin") String gstin,
			@Param("taxPeriod") String stTaxPeriod,
			@Param("zipFilePath") String zipFilePath,
			@Param("updatedDate") LocalDateTime updatedDate,
			@Param("jobStatus") String jobStatus);

	@Query(nativeQuery = true, value = "select top 50 * from (select arc.GSTIN, arc.TAX_PERIOD, "
			+ "arc.RETURN_TYPE from MONTHLY_ZIP_GEN_STATUS arc "
			+ "where arc.ZIP_FILE_PATH is NULL and (arc.JOB_STATUS is null or "
			+ "arc.JOB_STATUS not in ('ZIP_POSTED', 'ZIP_STARTED')) "
			+ "and arc.RETURN_TYPE in ('GSTR1', 'GSTR3B', 'GSTR2A', 'ITC04', 'GSTR7', 'GSTR2X', 'GSTR6', 'GSTR8','GSTR1A') "
			+ "except select  distinct GSTIN, TAX_PERIOD, RETURN_TYPE "
			+ "from GSTN_GET_STATUS as a where "
			+ "a.RETURN_TYPE in ('GSTR1', 'GSTR3B', 'GSTR2A', 'ITC04', 'GSTR7', 'GSTR2X', 'GSTR6', 'GSTR8','GSTR1A') "
			+ "and a.CSV_GENERATION_FLAG = false and "
			+ "a.STATUS not in ('SUCCESS_WITH_NO_DATA', 'FAILED'))")
	public List<Object[]> fetchGstinAndInvoiceTypeEligibleForZipMonthwise();
	
	@Modifying
	@Query("UPDATE ZipGenStatusEntity zipGenSt SET "
			+ "zipGenSt.jobStatus = :jobStatus,"
			+ "zipGenSt.updatedOn = :updatedDate "
			+ "WHERE zipGenSt.gstin = :gstin and "
			+ "zipGenSt.taxPeriod = :taxPeriod and "
			+ "zipGenSt.returnType = :returnType")
	public int updateJobstatusForList(@Param("jobStatus") String jobStatus,
			@Param("gstin") String gstin, @Param("taxPeriod") String taxPeriod,
			@Param("returnType") String returnType,
			@Param("updatedDate") LocalDateTime updatedDate);

	@Modifying
	@Query("UPDATE ZipGenStatusEntity zipGenSt SET "
			+ "zipGenSt.zipFilePath = :zipFilePath, "
			+ "zipGenSt.updatedOn = :updatedDate, "
			+ "zipGenSt.jobStatus = :jobStatus "
			+ "WHERE zipGenSt.gstin =:gstin and "
			+ "zipGenSt.taxPeriod =:taxPeriod and "
			+ "zipGenSt.returnType =:returnType")
	public int updateMonthlyZipGenStatus(@Param("returnType") String returnType,
			@Param("gstin") String gstin, @Param("taxPeriod") String taxPeriod,
			@Param("zipFilePath") String zipFilePath,
			@Param("updatedDate") LocalDateTime updatedDate,
			@Param("jobStatus") String jobStatus);

	@Query("SELECT z.gstin, z.taxPeriod FROM ZipGenStatusEntity z WHERE z.gstin in (:gstins) "
			+ "and z.jobStatus = :jobStatus and z.derivedTaxPeriod >= :derivedStartPeriod "
			+ "and z.derivedTaxPeriod <= :derivedEndPeriod and z.returnType = :returnType")
	public List<Object[]> getGstinZipGenStatus(
			@Param("gstins") List<String> gstinDtoList,
			@Param("jobStatus") String jobStatus,
			@Param("derivedStartPeriod") Integer derivedStartPeriod,
			@Param("derivedEndPeriod") Integer derivedEndPeriod,
			@Param("returnType") String returnType);

	public ZipGenStatusEntity findByGstinAndTaxPeriodAndReturnType(String gstin,
			String taxPeriod, String returnType);

}
